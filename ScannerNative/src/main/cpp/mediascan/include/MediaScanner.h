//
// Created by 高成佳 on 2021/8/2.
//

#ifndef SCANNER_MEDIASCANNER_H
#define SCANNER_MEDIASCANNER_H

#include <MediaRetrieverManager.h>
#include <BFSTravel.h>


class MediaScannerListener{
public:
    virtual ~MediaScannerListener() = default;
    virtual void onEvent(std::string deviceId, int status) = 0;
    virtual void onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type) = 0;
};

class MediaScanner : public BFSTravelListener, public RetrieverManagerListener{
public:
    using SharePtr = std::shared_ptr<MediaScanner>;

    enum State{
        STATE_IDLE        = 0,
        STATE_SCAN        = 1,
        STATE_FINISHED    = 2,
        STATE_STOP        = 3,
    };
public:
    MediaScanner(std::string deviceId);
    ~MediaScanner() override;

    void setMediaScanConfig(MediaScanConfig* config);
    void setScanPath(std::string path);
    void setMediaScannerListener(MediaScannerListener* listener);

    int start();
    int stop();
    int wait();

    std::string & getScanPath();
    std::string & getDeviceId();

public:
    void onTravel(std::list<MediaInfo::SharePtr > infos) override;
    void onTravelStatus(int status) override;
    void onRetrieverList(std::list<MediaInfo::SharePtr> infos) override;

private:
    void updateStatus(int status);

private:
    MediaRetrieverManager*      mRetrieverManager;
    MediaScanConfig*            mConfig;
    std::string                 mScanPath;
    MediaScannerListener*       mScannerListener;
    std::string                 mDeviceId;
    BFSTravel*                  mBFSTravel;
    State                       mState;
    std::mutex                  mMutex;
    std::mutex                  mCallbackMutex;
};


#endif //SCANNER_MEDIASCANNER_H
