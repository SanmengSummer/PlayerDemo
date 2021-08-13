//
// Created by 高成佳 on 2021/8/5.
//
#define TAG "MediaScannerManager"
#include <Log.h>

#include <MediaStatus.h>
#include "MediaScannerManager.h"

typedef std::unique_lock<std::mutex> AutoLock;

class MediaScannerListenerImpl : public MediaScannerListener {
public:
    explicit MediaScannerListenerImpl(MediaScannerManager *manager){
        mScanManager = manager;
    }

    ~MediaScannerListenerImpl() override {} ;

    void onEvent(std::string deviceId, int status) override{
        if (mScanManager) {
            mScanManager->onEvent(deviceId, status);
        }
    };

    void onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type) override{
        if (mScanManager) {
            mScanManager->onFileInfoList(deviceId, infos, type);
        }
    }

private:
    MediaScannerManager *mScanManager;
};



MediaScannerManager::MediaScannerManager()
        : mUpStreamListener(nullptr),
          mMediaConfig(nullptr) {

    mScannerListener = new MediaScannerListenerImpl(this);
}

MediaScannerManager::~MediaScannerManager() {
    if (mScannerListener) {
        delete mScannerListener;
        mScannerListener = nullptr;
    }
    if (mMediaConfig) {
        delete mMediaConfig;
        mMediaConfig = nullptr;
    }
}

void MediaScannerManager::setConfigPath(const std::string &path) {
    if (!mMediaConfig) {
        mMediaConfig = MediaScanConfig::createFromFile(path.c_str());
    }
}

void MediaScannerManager::setConfigData(const std::string &data) {
    if (!mMediaConfig) {
        mMediaConfig = MediaScanConfig::createFromData(data.c_str());
    }
}

std::string MediaScannerManager::setScanPath(const std::string &deviceId, std::string scanPath) {
    AutoLock lock(mMutex);
    auto it = mMediaScannerMap.find(deviceId);
    if (it != mMediaScannerMap.end()) {
        MediaScanner::SharePtr scan = it->second;
        if (strncmp(deviceId.c_str(), scan->getDeviceId().c_str(), deviceId.length())) {
            LOGE("target scanner is exist device id is %s", deviceId.c_str());
            return deviceId;
        }
    }
    if (!mMediaConfig) {
        LOGW("use default config.");
        mMediaConfig = MediaScanConfig::createFromData(nullptr);
    }
    MediaScanner::SharePtr scanner = std::make_shared<MediaScanner>(deviceId);
    scanner->setMediaScanConfig(mMediaConfig);
    scanner->setMediaScannerListener(mScannerListener);
    scanner->setScanPath(scanPath);
    mMediaScannerMap.insert({deviceId, scanner});
    return deviceId;
}

int MediaScannerManager::start(const std::string &deviceId) {
    AutoLock lock(mMutex);
    int status = STATUS_INVALID;
    auto it = mMediaScannerMap.find(deviceId);
    if (it != mMediaScannerMap.end()) {
        MediaScanner::SharePtr s = it->second;
        int err = STATUS_UNKNOW;
        err = s->start();
        if (err != STATUS_OK) {
            LOGE("call start err.");
            mMediaScannerMap.erase(it);
        } else {
            status = STATUS_OK;
            LOGD("start success.");
        }
    } else {
        LOGE("d'ont find this device. plz check.");
    }
    return status;
}

int MediaScannerManager::stop(const std::string &deviceId) {
    AutoLock lock(mMutex);
    int status = STATUS_INVALID;
    auto it = mMediaScannerMap.find(deviceId);
    if (it != mMediaScannerMap.end()) {
        MediaScanner::SharePtr s = it->second;
        int err = STATUS_UNKNOW;
        err = s->stop();
        if (err != STATUS_OK) {
            LOGE("call stop err.");
            mMediaScannerMap.erase(it);
        } else {
            LOGD("stop success.");
            status = STATUS_OK;
        }
    } else {
        LOGE("d'ont find this device. plz check.");
    }
    return status;
}

int MediaScannerManager::wait(const std::string &deviceId) {
    int status = STATUS_INVALID;
    auto it = mMediaScannerMap.find(deviceId);
    if (it != mMediaScannerMap.end()) {
        MediaScanner::SharePtr s = it->second;
        int err = STATUS_UNKNOW;
        err = s->wait();
        if (err != STATUS_OK) {
            LOGE("call wait err.");
            mMediaScannerMap.erase(it);
        } else {
            LOGD("wait success.");
            status = STATUS_OK;
        }
    } else {
        LOGE("d'ont find this device. plz check.");
    }
    return status;
}

void MediaScannerManager::onEvent(std::string deviceId, int event) {
    if(event == EVT_SCAN_FINISHED){
//        stop(deviceId);
    }
    if (mUpStreamListener) {
        mUpStreamListener->onEvent(deviceId, event);
    }
}

void MediaScannerManager::onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type) {
    AutoLock lock(mCallbackMutex);
    if (mUpStreamListener) {
        mUpStreamListener->onFileInfoList(deviceId, infos, type);
    }
}

void MediaScannerManager::setMediaScannerManagerListener(MediaScannerManagerListener *listener) {
    AutoLock lock(mCallbackMutex);
    mUpStreamListener = listener;
}

