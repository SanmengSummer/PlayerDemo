//
// Created by 高成佳 on 2021/7/29.
//

#define TAG "CONFIG"
#include <Log.h>


#define pathJson "/Users/gaby/Project/CLionProjects/scanner/example/config.json"
#include <MediaScanConfig.h>


int main(){
    Utils::Logger::getInstance()
            .setLogTarget(Utils::LOG_TARGET_CONSOLE)
            .build();

    MediaScanConfig* config = MediaScanConfig::createFromFile(pathJson);

    LOGD("%s",config->dump().c_str());
    return 0;
};

