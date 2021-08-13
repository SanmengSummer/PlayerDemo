//
// Created by 高成佳 on 2021/7/30.
//
#define TAG "travel"
#include <Log.h>

#define pathJson "/Users/gaby/Project/CLionProjects/scanner/example/config.json"
#define pathDir "/Users/gaby/Downloads/test"
#include <FileInfo.h>
#include <MediaScanConfig.h>
#include <sstream>
#include <BFSTravel.h>
#include <MediaRetrieverManager.h>

class TravelListener : public BFSTravelListener{
public:
    TravelListener() = default;
    ~TravelListener() override = default;

    void onTravel(std::list<MediaInfo::SharePtr > infos) override {
        std::stringstream result;
        for (auto item : infos) {
            std::shared_ptr<FileInfo> info = item;
            result << *info;
            result << "\n";
        }
        LOGD("result: %s",result.str().c_str());
        manager->setFilesToRetriever(infos);
    }

    void onTravelStatus(int status) override {
        LOGD("status:" + status);
    }

    void setRetrieverManager(MediaRetrieverManager* m){
        manager = m;
    }

private:
    MediaRetrieverManager* manager = nullptr;
};

static int mediaCnt = 0;
class RetrieverManagerListenerImpl : public RetrieverManagerListener{
public:
    RetrieverManagerListenerImpl() = default;
    ~RetrieverManagerListenerImpl() override = default;

    void onRetrieverList(std::list<MediaInfo::SharePtr> infos) override {
        std::stringstream result;
        for (auto item : infos) {
            std::shared_ptr<MediaInfo> info = item;
            result << *info;
            result << "\n";
        }
        mediaCnt += infos.size();
        LOGD("result: %s  size:%d",result.str().c_str(),mediaCnt);
    }
};

int main(){

    Utils::Logger::getInstance()
            .setLogTarget(Utils::LOG_TARGET_CONSOLE)
            .build();

    MediaScanConfig* config = MediaScanConfig::createFromFile(pathJson);
    BFSTravel *travel = new BFSTravel;
    TravelListener *listener = new TravelListener;
    MediaRetrieverManager* manager = new MediaRetrieverManager;
    RetrieverManagerListener* impl = new RetrieverManagerListenerImpl;
    manager->setScanThreadNum(config->getLimit(MediaScanConfig::NUM_LIMIT_RETRIEVER_THREADS));
    manager->setRetrieverManagerListener(impl);
    listener->setRetrieverManager(manager);
    travel->setScanConfig(config);
    travel->setBFSTravelListener(listener);
    travel->startTravelAsync(pathDir);

//    while (1);
    travel->wait();
    delete listener;
    delete travel;
    delete impl;
    delete manager;


    return 0;
}