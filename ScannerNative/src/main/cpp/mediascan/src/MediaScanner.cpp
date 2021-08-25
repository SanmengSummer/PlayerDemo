//
// Created by 高成佳 on 2021/8/2.
//
#define TAG "MediaScanner"
#include <Log.h>

#include <MediaStatus.h>
#include "MediaScanner.h"

typedef std::unique_lock<std::mutex> AutoLock;

void MediaScanner::onTravel(std::list<MediaInfo::SharePtr> infos) {
    //1.上报
    if (mScannerListener) {
        mScannerListener->onFileInfoList(mDeviceId, infos, MediaInfo::F_TYPE_MEDIA);
    }
    //2.retriever
    if (mRetrieverManager) {
        mRetrieverManager->setFilesToRetriever(infos);
    }
}

void MediaScanner::onTravelStatus(int status) {
    LOGD("device id: %s----------------- status:%d",mDeviceId.c_str(),status);
    if(status == STATUS_FINISHED){
        if (mRetrieverManager){
            mRetrieverManager->finish();
        }
    }
    updateStatus(status);
}

void MediaScanner::onRetrieverList(std::list<MediaInfo::SharePtr> infos) {
//    AutoLock lock(mCallbackMutex);
    if (mScannerListener) {
        mScannerListener->onFileInfoList(mDeviceId, infos, FileInfo::F_TYPE_AUDIO);
    }
}

MediaScanner::MediaScanner(std::string deviceId)
        : mConfig(nullptr),
          mScannerListener(nullptr),
          mBFSTravel(nullptr),
          mRetrieverManager(nullptr),
          mState(STATE_IDLE) {
    mDeviceId = deviceId;
}

MediaScanner::~MediaScanner() {
    stop();
}

void MediaScanner::setMediaScanConfig(MediaScanConfig *config) {
    mConfig = config;
}

void MediaScanner::setScanPath(std::string path) {
    mScanPath = path;
}

void MediaScanner::setMediaScannerListener(MediaScannerListener *listener) {
    mScannerListener = listener;
}

int MediaScanner::start() {
    AutoLock lock(mMutex);
    if (mState == STATE_SCAN) {
        return STATUS_BUSY;
    }
    if (!mConfig) {
        LOGE("plz set config.");
        return STATUS_INVALID;
    }
    if (!mBFSTravel) {
        mBFSTravel = new BFSTravel;
        mBFSTravel->setBFSTravelListener(this);
        mBFSTravel->setScanConfig(mConfig);
    }
    if (!mRetrieverManager) {
        mRetrieverManager = new MediaRetrieverManager;
        mRetrieverManager->setRetrieverManagerListener(this);
        int num = mConfig->getLimit(MediaScanConfig::NUM_LIMIT_RETRIEVER_THREADS);
        mRetrieverManager->setScanThreadNum(num);
    }
    mBFSTravel->startTravelAsync(mScanPath.c_str());
    mState = STATE_SCAN;
    return STATUS_OK;
}

int MediaScanner::stop() {
    AutoLock lock(mMutex);
    if (mRetrieverManager) {
        delete mRetrieverManager;
        mRetrieverManager = nullptr;
    }
    if (mBFSTravel) {
        delete mBFSTravel;
        mBFSTravel = nullptr;
    }
    mState = STATE_STOP;
    return STATUS_OK;
}

int MediaScanner::wait() {
    if (mBFSTravel) {
        mBFSTravel->wait();
    }
    return STATUS_OK;
}

std::string &MediaScanner::getScanPath() {
    return mScanPath;
}

std::string &MediaScanner::getDeviceId() {
    return mDeviceId;
}

void MediaScanner::updateStatus(int status) {
    static int state = STATUS_UNKNOW;
    if (state != status) {
        state = status;
        int evt;
        switch (state) {
            case STATUS_START:
                evt = EVT_SCAN_START;
                break;
            case STATUS_SCANNING:
                evt = EVT_SCAN_SCANNING;
                break;
            case STATUS_EMPTY:
                evt = EVT_SCAN_EMPTY;
                break;
            case STATUS_FINISHED:
                evt = EVT_SCAN_FINISHED;
                break;
        }
        if (mScannerListener) {
            mScannerListener->onEvent(mDeviceId, evt);
        }
    }
}
