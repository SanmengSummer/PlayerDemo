//
// Created by 高成佳 on 2021/7/30.
//
#define TAG "BFSTravel"
#include <Log.h>

#include <MediaScanConfig.h>
#include <StringUtils.h>
#include <dirent.h>
#include <MediaStatus.h>
#include <FileInfo.h>
#include <sys/stat.h>
#include "BFSTravel.h"

#define SEP "/"

BFSTravel::BFSTravel()
        : mTravelThread(nullptr),
          mConfig(nullptr),
          mListener(nullptr),
          mRunning(false) {

}

BFSTravel::~BFSTravel() {
    stopTravel();
}

void BFSTravel::startTravelAsync(const char *rootDir) {
    if (!rootDir) {
        LOGE("root dir is null.");
        return;
    }
    mTravelThread = new std::thread([this](const char *rootDir) {
        this->startTravel(rootDir);
    }, rootDir);
}

void BFSTravel::stopTravel() {
    mMutex.lock();
    mRunning = false;
    mMutex.unlock();
    if (mTravelThread) {
        if (mTravelThread->joinable()) {
            mTravelThread->join();
        }
        delete mTravelThread;
        mTravelThread = nullptr;
    }
}

void BFSTravel::setBFSTravelListener(BFSTravelListener *listener) {
    mListener = listener;
}

void BFSTravel::setScanConfig(MediaScanConfig *config) {
    mConfig = config;
}

void BFSTravel::startTravel(const char *rootDir) {
    if (mConfig == nullptr) {
        LOGE("config is null error.");
        return;
    }
    if (mListener == nullptr) {
        LOGE("listener is null error plz set listener.");
        return;
    }
    std::list<std::string> dirQueue;
    int mediaFileCount = 0;
//    int numFastPlay = mConfig->getLimit(MediaScanConfig::NUM_LIMIT_FAST_PLAY);
//    int numBrowser = mConfig->getLimit(MediaScanConfig::NUM_LIMIT_BROWSE);
    int numDirDepth = mConfig->getLimit(MediaScanConfig::NUM_LIMIT_DIR_DEPTH);

    mMutex.lock();
    mRunning = true;
    mMutex.unlock();

    std::string path = StringUtils::filePath(rootDir);
    int rootDepth = StringUtils::strcnt(rootDir, SEP);
    dirQueue.push_back(path);
    if (mListener){
        mListener->onTravelStatus(STATUS_START);
    }
    while (!dirQueue.empty() && mRunning) {
        std::string path = dirQueue.front();
        dirQueue.pop_front();

        int depth = StringUtils::strcnt(path.c_str(), SEP);
        if (depth - rootDepth > numDirDepth) {
            continue;
        }
        if (mListener){
            mListener->onTravelStatus(STATUS_SCANNING);
        }
        DIR *dir;
        struct dirent *ent;
        if ((dir = opendir(path.c_str())) != nullptr) {
            std::list<MediaInfo::SharePtr> infos;
            bool skip = false;
            while (mRunning && (ent = readdir(dir)) != nullptr) {
                int fileType = MediaInfo::F_TYPE_UNKNOWN;
                std::string currentFile = path + SEP + ent->d_name;
                switch (ent->d_type) {
                    case DT_DIR: {
                        if (strncmp(".", ent->d_name, 1) == 0) {
                            continue;
                        } else {
                            fileType = MediaInfo::F_TYPE_DIR;
                            dirQueue.push_back(currentFile);
                        }
                        break;
                    }
                    case DT_REG:{
                        const char *suffix = StringUtils::suffix(ent->d_name);
                        if (!suffix) {
                            break;
                        }
                        int type = mConfig->getFileType(suffix);
                        if(type != MediaInfo::F_TYPE_UNKNOWN){
                            fileType = type;
                        }
                        if (strncmp(".nomedia", ent->d_name, strlen(ent->d_name)) == 0) {
                            skip = true;
                            goto nextdir;
                        }
                        break;
                    }
                    default:{
                        continue;
                        break;
                    }
                }

                if (fileType != FileInfo::F_TYPE_UNKNOWN) {
                    MediaInfo::SharePtr info = std::make_shared<MediaInfo>();
                    info->mFileName = ent->d_name;
                    info->mFilePath = currentFile;
                    info->mParentPath = path;
                    info->mFileType = fileType;

                    struct stat st;
                    if (stat(currentFile.c_str(), &st) == 0) {
                        info->mFileModTime = st.st_mtime;
                        info->mFileChangeTime = st.st_ctime;
                        info->mFileSize = st.st_size;
                        infos.push_back(info);
                        mediaFileCount++;
                    }
                    else {
                        LOGE(TAG, "stat file failed: %s", currentFile.c_str());
                    }
                }
                //todo divide page

            }
            nextdir:
                closedir(dir);
                if(!skip){
                    mListener->onTravel(infos);
                }
        } else {
            LOGE("opendir() failed, path:%s error:%s", path.c_str(), strerror(errno));
            if (errno == EACCES || errno == EAGAIN) {
                LOGE(TAG, "continue travel thread");
                continue;
            } else {
                mListener->onTravelStatus(STATUS_EMPTY);
                return;
            }
        }
    }
    mListener->onTravelStatus(STATUS_FINISHED);
}

void BFSTravel::wait() {
    if(mTravelThread){
        if(mTravelThread->joinable()){
            mTravelThread->join();
        }
    }
}
