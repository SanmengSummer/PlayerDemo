//
// Created by 高成佳 on 2021/8/2.
//
#define TAG "MediaRetrieverManager"

#include <Log.h>
#include <iterator>

#include "MediaRetrieverManager.h"


static const int RETRIEVER_NUM_MAX_FOR_ONCE = 300;
static const int RETRIEVER_THREAD_MAX       = 20;
static const int RETRIEVER_THREAD_DEFAULT   = 4;

MediaRetrieverManager::MediaRetrieverManager()
    :mRetrieverListener(nullptr),
    mThreads(nullptr){

}

MediaRetrieverManager::~MediaRetrieverManager() {
    if(mThreads){
        delete mThreads;
    }
}

void MediaRetrieverManager::setScanThreadNum(int threadNum) {
    int num;
    if(threadNum < 0){
        num = RETRIEVER_THREAD_DEFAULT;
    } else if(threadNum > 20){
        num = RETRIEVER_THREAD_MAX;
    } else{
        num = threadNum;
    }
    if (mThreads == nullptr){
        mThreads = new ThreadPool(num);
    }
//    mThreads->setThreadNum(num);
}

void MediaRetrieverManager::setFilesToRetriever(std::list<MediaInfo::SharePtr> infos) {
    std::list<MediaInfo::SharePtr> list;
    for (auto item: infos) {
        if (item->mFileType == MediaInfo::F_TYPE_AUDIO) {
            const char *path = item->mFilePath.c_str();
            if (path != nullptr) {
                list.push_back(item);
            }
        }
    }
    if (list.empty()) {
        LOGE("d'not have media file.");
        return;
    } else {
        if (list.size() > RETRIEVER_NUM_MAX_FOR_ONCE) {
            //list to large, split
            int mod = list.size() / RETRIEVER_NUM_MAX_FOR_ONCE;
            int remainder = list.size() % RETRIEVER_NUM_MAX_FOR_ONCE;
            if (remainder > 0)
                mod += 1;
            for (int i = 0; i < mod; ++i) {
                auto start = list.begin();
                auto end   = list.begin();
                std::list<MediaInfo::SharePtr> sublist;
                std::advance(start,i * RETRIEVER_NUM_MAX_FOR_ONCE);
                if (i == mod - 1) {
                    sublist.assign(start, list.end());
                } else {
                    std::advance(end,(i+1) * RETRIEVER_NUM_MAX_FOR_ONCE);
                    sublist.assign(start, end);
                }
                auto ret = mThreads->enqueue(&MediaRetrieverManager::parseMetadata, this, sublist);
                ret.wait();
            }
        } else {
            auto ret = mThreads->enqueue(&MediaRetrieverManager::parseMetadata, this, list);
            ret.wait();
        }
    }

}

void MediaRetrieverManager::parseMetadata(std::list<MediaInfo::SharePtr> list) {
    MediaMetadataRetriever::SharePtr retriever = std::make_shared<MediaMetadataRetriever>();
    std::list<MediaInfo::SharePtr> result;
    while (!list.empty()){
        auto  item = list.front();
        list.pop_front();
        int ret = retriever->setDataSource(item->mFilePath.c_str());
        if (ret < 0) {
            LOGE("setDataSource error.");
        } else {
            item->mTitle = retriever->extractMetadata(META_KEY_TITLE);
            item->mArtist = retriever->extractMetadata(META_KEY_ARTIST);
            item->mAlbum = retriever->extractMetadata(META_KEY_ALBUM);
            item->mComposer = retriever->extractMetadata(META_KEY_COMPOSER);
            item->mGenre = retriever->extractMetadata(META_KEY_GENRE);
            item->mYear = retriever->extractMetadata(META_KEY_YEAR);
            item->mDuration = retriever->getDuration();
        }
        result.push_back(item);
    }
    if(mRetrieverListener){
        mRetrieverListener->onRetrieverList(result);
    }
}

void MediaRetrieverManager::setRetrieverManagerListener(RetrieverManagerListener *listener) {
    mRetrieverListener = listener;
}



