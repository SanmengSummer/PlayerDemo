//
// Created by 高成佳 on 2021/8/5.
//

#ifndef SCANNER_MEDIASCANNERMANAGER_H
#define SCANNER_MEDIASCANNERMANAGER_H

#include <string>

#include <MediaScanConfig.h>
#include <MediaScanner.h>

class MediaScannerManagerListener{
public:
    virtual ~MediaScannerManagerListener() = default;
    virtual void onEvent(std::string deviceId, int event) = 0;
    virtual void onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type) = 0;
};

class MediaScannerManager  {
public:
    MediaScannerManager();
    virtual ~MediaScannerManager();
    void setConfigPath(const std::string& path);
    void setConfigData(const std::string& data);
    void setMediaScannerManagerListener(MediaScannerManagerListener* listener);
    std::string setScanPath(const std::string& deviceId, const std::string scanPath);

    int start(const std::string& deviceId);
    int stop(const std::string& deviceId);
    int wait(const std::string& deviceId);

    void onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type);
    void onEvent(std::string deviceId, int event);

private:
    MediaScannerManagerListener*                            mUpStreamListener;
    MediaScannerListener*                                   mScannerListener;
    std::unordered_map<std::string, MediaScanner::SharePtr> mMediaScannerMap;
    MediaScanConfig*                                        mMediaConfig;
    std::mutex                                              mMutex;
    std::mutex                                              mCallbackMutex;
};


#endif //SCANNER_MEDIASCANNERMANAGER_H
