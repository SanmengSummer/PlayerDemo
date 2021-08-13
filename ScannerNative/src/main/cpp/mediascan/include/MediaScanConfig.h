//
// Created by 高成佳 on 2021/7/29.
//

#ifndef SCANNER_MEDIASCANCONFIG_H
#define SCANNER_MEDIASCANCONFIG_H

#include <string>
#include <unordered_set>
#include <unordered_map>

#define KEY_NUM_LIMIT_FAST_PLAY          "NUM_LIMIT_FAST_PLAY"
#define KEY_NUM_LIMIT_BROWSE             "NUM_LIMIT_BROWSE"
#define KEY_NUM_LIMIT_DIR_DEPTH          "NUM_LIMIT_DIR_DEPTH"
#define KEY_NUM_LIMIT_RETRIEVER_THREADS  "NUM_LIMIT_RETRIEVER_THREADS"
#define KEY_AUDIO_SUFFIX_SUPPORT         "AUDIO_SUFFIX_SUPPORT"
#define KEY_VIDEO_SUFFIX_SUPPORT         "VIDEO_SUFFIX_SUPPORT"
#define KEY_IMAGE_SUFFIX_SUPPORT         "IMAGE_SUFFIX_SUPPORT"

class MediaScanConfig {
public:
    ~MediaScanConfig() = default;
    static MediaScanConfig * createFromFile(const char *filepath);
    static MediaScanConfig * createFromData(const char *data);

    bool isSupportVideo(const char *suffix);
    bool isSupportAudio(const char *suffix);
    bool isSupportImage(const char *suffix);
    bool isSupportMedia(const char *suffix);

    int getFileType(const char *suffix);

    std::string dump();

    enum {
        NUM_LIMIT_FAST_PLAY = 0,
        NUM_LIMIT_BROWSE,
        NUM_LIMIT_DIR_DEPTH,
        NUM_LIMIT_RETRIEVER_THREADS,
    };

    int getLimit(int index);

private:
    MediaScanConfig() = default;
    bool parseFile(const char *filepath);
    bool parseData(const char *data);

    std::unordered_set<std::string> mVideoSuffix;
    std::unordered_set<std::string> mAudioSuffix;
    std::unordered_set<std::string> mImageSuffix;
    std::unordered_map<int, int>    mConfigLimit;
};


#endif //SCANNER_MEDIASCANCONFIG_H
