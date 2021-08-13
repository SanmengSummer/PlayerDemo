//
// Created by 高成佳 on 2021/8/5.
//
#define TAG "example"
#include <Log.h>

#include <MediaStatus.h>
#include <sstream>
#include <MediaScannerManager.h>

std::string listToString(std::list<MediaInfo::SharePtr> infos){
    std::stringstream result;
    for (auto item : infos) {
        if(item->mFileType == MediaInfo::F_TYPE_AUDIO){
            std::shared_ptr<MediaInfo> info = item;
            result << *info;
            result << "\n";
        } else{
            std::shared_ptr<FileInfo> info = item;
            result << *info;
            result << "\n";
        }

    }
    return result.str();
}

class ListenerImpl: public MediaScannerManagerListener{
public:
    ListenerImpl() = default;
    ~ListenerImpl() override = default;

    void onEvent(std::string deviceId, int event) override {
        LOGD("deviceId:%s,event:%d",deviceId.c_str(),event);
    }

    void onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type) override {
        LOGD("deviceId:%s type:%d result:%s",deviceId.c_str(),type,listToString(infos).c_str());
    }
};



#define pathJson "/Users/gaby/Project/CLionProjects/scanner/example/config.json"
#define pathDir1 "/Users/gaby/Downloads/test1"
#define pathDir2 "/Users/gaby/Downloads/test2"
#define deviceId1 "device1"
#define deviceId2 "device2"

int main(){
    Utils::Logger::getInstance()
            .setLogTarget(Utils::LOG_TARGET_CONSOLE)
            .build();
    ListenerImpl* listener = new ListenerImpl;
    MediaScannerManager* manager = new MediaScannerManager;
    manager->setMediaScannerManagerListener(listener);
    manager->setConfigPath(pathJson);
    std::thread* t = new std::thread([&](){
        std::string device = manager->setScanPath(deviceId2,pathDir2);
        int err = manager->start(device);
        if(err == STATUS_OK)
            manager->wait(device);
        LOGD("thread 1 done.....");
    });

    std::string device = manager->setScanPath(deviceId1,pathDir1);
    int err = manager->start(device);
    if(err == STATUS_OK)
        manager->wait(device);
    t->join();
    delete manager;
    delete listener;
    return 0;
}
