//
// Created by 高成佳 on 2021/7/30.
//

#ifndef SCANNER_BFSTRAVEL_H
#define SCANNER_BFSTRAVEL_H

#include <list>
#include <mutex>
#include <thread>

class MediaScanConfig;
class FileInfo;
class BFSTravelListener{
public:
    virtual ~BFSTravelListener() = default;
    virtual void onTravel(std::list<MediaInfo::SharePtr> infos) = 0;
    virtual void onTravelStatus(int status) = 0;
};

class BFSTravel {
public:
    BFSTravel();
    ~BFSTravel();

    void startTravelAsync(const char *rootDir);
    void stopTravel();
    void setBFSTravelListener(BFSTravelListener* listener);
    void setScanConfig(MediaScanConfig* config);
    void wait();
private:
    void startTravel(const char *rootDir);

private:
    BFSTravelListener*  mListener;
    std::mutex          mMutex;
    bool                mRunning;
    std::thread*        mTravelThread;
    MediaScanConfig*    mConfig;
};


#endif //SCANNER_BFSTRAVEL_H
