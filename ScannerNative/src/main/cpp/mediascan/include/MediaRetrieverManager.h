//
// Created by 高成佳 on 2021/8/2.
//

#ifndef SCANNER_MEDIARETRIEVERMANAGER_H
#define SCANNER_MEDIARETRIEVERMANAGER_H

#include <list>
#include <queue>

#include <ThreadPool.h>
#include <MediaScanConfig.h>
#include <FileInfo.h>
#include <MediaMetadataRetriever.h>

class RetrieverManagerListener{
public:
    virtual ~RetrieverManagerListener() = default;
    virtual void onRetrieverList(std::list<MediaInfo::SharePtr> infos) = 0;
};

class MediaRetrieverManager {
public:
    MediaRetrieverManager();
    ~MediaRetrieverManager();

    void setScanThreadNum(int threadNum);
    void setFilesToRetriever(std::list<MediaInfo::SharePtr> infos);
    void setRetrieverManagerListener(RetrieverManagerListener* listener);
private:
    void parseMetadata(std::list<MediaInfo::SharePtr> list);
private:
    ThreadPool*                             mThreads;
    RetrieverManagerListener*               mRetrieverListener;
};


#endif //SCANNER_MEDIARETRIEVERMANAGER_H
