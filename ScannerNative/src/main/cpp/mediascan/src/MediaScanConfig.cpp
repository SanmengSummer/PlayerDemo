//
// Created by 高成佳 on 2021/7/29.
//
#define TAG "MediaScanConfig"
#include <Log.h>

#include <FileInfo.h>
#include <cJSON.h>
#include <sstream>
#include "MediaScanConfig.h"

#define ELEM(e) (sizeof(e)/sizeof(e[0]))

static const int DEFAULT_VALUE_NUM_LIMIT_FAST_PLAY = 1;
static const int DEFAULT_VALUE_NUM_LIMIT_BROWSE = 10;
static const int DEFAULT_VALUE_NUM_LIMIT_DIR_DEPTH = 8;
static const int DEFAULT_VALUE_NUM_LIMIT_RETRIEVER_THREADS = 1;
static const std::string DEFAULT_VALUE_AUDIO_SUPPORT_SUFFIX[] = { "mp3", "ogg" };
static const std::string DEFAULT_VALUE_VIDEO_SUPPORT_SUFFIX[] = { "mp4", "mkv" };
static const std::string DEFAULT_VALUE_IMAGE_SUPPORT_SUFFIX[] = { "jpg", "png" };

static const std::string KEY_NAME[] = {"fast_play","browser","dir_depth","num_thread"};


MediaScanConfig *MediaScanConfig::createFromFile(const char *filepath) {

    MediaScanConfig *config = new MediaScanConfig;
    if (config->parseFile(filepath)) {
        return config;
    } else {
        LOGE("create config from file error.");
        delete config;
    }
    return nullptr;
}

MediaScanConfig *MediaScanConfig::createFromData(const char *data) {

    MediaScanConfig *config = new MediaScanConfig;
    if (config->parseData(data)) {
        return config;
    } else {
        LOGE("create config from file error.");
        delete config;
    }
    return nullptr;
}

bool MediaScanConfig::isSupportVideo(const char *suffix) {

    if (!suffix) {
        LOGE("suffix is null");
        return false;
    } else {
        std::string key = suffix;
        std::transform(key.begin(), key.end(), key.begin(), ::tolower);
        auto it = mVideoSuffix.find(key);
        return it != mVideoSuffix.end();
    }
}

bool MediaScanConfig::isSupportAudio(const char *suffix) {
    if (!suffix) {
        LOGE("suffix is null");
        return false;
    } else {
        std::string key = suffix;
        std::transform(key.begin(), key.end(), key.begin(), ::tolower);
        auto it = mAudioSuffix.find(key);
        return it != mAudioSuffix.end();
    }
}

bool MediaScanConfig::isSupportImage(const char *suffix) {
    if (!suffix) {
        LOGE("suffix is null");
        return false;
    } else {
        std::string key = suffix;
        std::transform(key.begin(), key.end(), key.begin(), ::tolower);
        auto it = mImageSuffix.find(key);
        return it != mImageSuffix.end();
    }
}

bool MediaScanConfig::isSupportMedia(const char *suffix) {
    return getFileType(suffix) != FileInfo::F_TYPE_UNKNOWN;
}

int MediaScanConfig::getFileType(const char *suffix) {

    if (isSupportVideo(suffix)) {
        return FileInfo::F_TYPE_VIDEO;
    }
    if (isSupportAudio(suffix)) {
        return FileInfo::F_TYPE_AUDIO;
    }
    if (isSupportImage(suffix)) {
        return FileInfo::F_TYPE_IMAGE;
    }
    return FileInfo::F_TYPE_UNKNOWN;
}

int MediaScanConfig::getLimit(int index) {
    if(index < NUM_LIMIT_FAST_PLAY || index > NUM_LIMIT_RETRIEVER_THREADS){
        return -1;
    }
    auto it = mConfigLimit.find(index);
    if(it != mConfigLimit.end()){
        return it->second;
    }
    return -1;
}

bool MediaScanConfig::parseFile(const char *filepath) {
    bool success = false;
    char *buf = nullptr;
    FILE *fp;
    if (filepath && (fp = fopen(filepath, "rb")) != nullptr) {
        fseek(fp, 0, SEEK_END);
        long length = ftell(fp);
        fseek(fp, 0, SEEK_SET);

        buf = static_cast<char *>(malloc(length + 1));
        fread(buf, length, 1, fp);
        buf[length] = '\0';

        fclose(fp);
    } else {
        LOGE("file open error.");
    }
    success = parseData(buf);
    if (buf) {
        free(buf);
    }
    return success;
}

bool MediaScanConfig::parseData(const char *data) {
    bool success = false;
    if(!data){
        //default config
        mConfigLimit.insert({NUM_LIMIT_FAST_PLAY,DEFAULT_VALUE_NUM_LIMIT_FAST_PLAY});
        mConfigLimit.insert({NUM_LIMIT_BROWSE,DEFAULT_VALUE_NUM_LIMIT_BROWSE});
        mConfigLimit.insert({NUM_LIMIT_DIR_DEPTH,DEFAULT_VALUE_NUM_LIMIT_DIR_DEPTH});
        mConfigLimit.insert({NUM_LIMIT_RETRIEVER_THREADS,DEFAULT_VALUE_NUM_LIMIT_RETRIEVER_THREADS});

        for (int i = 0; i < ELEM(DEFAULT_VALUE_VIDEO_SUPPORT_SUFFIX); ++i) {
            mVideoSuffix.insert(DEFAULT_VALUE_VIDEO_SUPPORT_SUFFIX[i]);
        }

        for (int i = 0; i < ELEM(DEFAULT_VALUE_AUDIO_SUPPORT_SUFFIX); ++i) {
            mAudioSuffix.insert(DEFAULT_VALUE_AUDIO_SUPPORT_SUFFIX[i]);
        }

        for (int i = 0; i < ELEM(DEFAULT_VALUE_IMAGE_SUPPORT_SUFFIX); ++i) {
            mImageSuffix.insert(DEFAULT_VALUE_IMAGE_SUPPORT_SUFFIX[i]);
        }
        success = true;
    } else{
        //parse json
        cJSON *root = cJSON_Parse(data);
        if(!root){
            success = false;
            LOGE("root parse error.");
        } else{
            cJSON *obj = cJSON_GetObjectItem(root,KEY_NUM_LIMIT_FAST_PLAY);
            if(obj && obj->type == cJSON_Number){
                int value = obj->valueint;
                mConfigLimit.insert({NUM_LIMIT_FAST_PLAY,value});
            } else{
                LOGW(TAG, "can't find %s, set as default value:%d",
                     KEY_NUM_LIMIT_FAST_PLAY, DEFAULT_VALUE_NUM_LIMIT_FAST_PLAY);
                mConfigLimit.insert({ NUM_LIMIT_FAST_PLAY, DEFAULT_VALUE_NUM_LIMIT_FAST_PLAY });
            }
            obj = cJSON_GetObjectItem(root,KEY_NUM_LIMIT_BROWSE);
            if(obj && obj->type == cJSON_Number){
                int value = obj->valueint;
                mConfigLimit.insert({NUM_LIMIT_BROWSE,value});
            } else{
                LOGW(TAG, "can't find %s, set as default value:%d",
                     KEY_NUM_LIMIT_BROWSE, DEFAULT_VALUE_NUM_LIMIT_BROWSE);
                mConfigLimit.insert({ NUM_LIMIT_BROWSE, DEFAULT_VALUE_NUM_LIMIT_FAST_PLAY });
            }
            obj = cJSON_GetObjectItem(root,KEY_NUM_LIMIT_DIR_DEPTH);
            if(obj && obj->type == cJSON_Number){
                int value = obj->valueint;
                mConfigLimit.insert({NUM_LIMIT_DIR_DEPTH,value});
            } else{
                LOGW(TAG, "can't find %s, set as default value:%d",
                     KEY_NUM_LIMIT_DIR_DEPTH, DEFAULT_VALUE_NUM_LIMIT_DIR_DEPTH);
                mConfigLimit.insert({ NUM_LIMIT_DIR_DEPTH, DEFAULT_VALUE_NUM_LIMIT_DIR_DEPTH });
            }
            obj = cJSON_GetObjectItem(root,KEY_NUM_LIMIT_RETRIEVER_THREADS);
            if(obj && obj->type == cJSON_Number){
                int value = obj->valueint;
                mConfigLimit.insert({NUM_LIMIT_RETRIEVER_THREADS,value});
            } else{
                LOGW(TAG, "can't find %s, set as default value:%d",
                     KEY_NUM_LIMIT_RETRIEVER_THREADS, DEFAULT_VALUE_NUM_LIMIT_RETRIEVER_THREADS);
                mConfigLimit.insert({ NUM_LIMIT_RETRIEVER_THREADS, DEFAULT_VALUE_NUM_LIMIT_RETRIEVER_THREADS });
            }
            obj = cJSON_GetObjectItem(root,KEY_VIDEO_SUFFIX_SUPPORT);
            if(obj && obj->type == cJSON_Array){
                int size = cJSON_GetArraySize(obj);
                for (int i = 0; i < size; ++i) {
                    cJSON *item = cJSON_GetArrayItem(obj,i);
                    if(item->type == cJSON_String){
                        std::string value = item->valuestring;
                        std::transform(value.begin(),value.end(),value.begin(),::tolower);
                        mVideoSuffix.insert(value);
                    }
                }
            } else{
                LOGW(TAG, "can't find %s, set as default",KEY_VIDEO_SUFFIX_SUPPORT);
                for (int i = 0; i < ELEM(DEFAULT_VALUE_VIDEO_SUPPORT_SUFFIX); ++i) {
                    mVideoSuffix.insert(DEFAULT_VALUE_VIDEO_SUPPORT_SUFFIX[i]);
                }
            }
            obj = cJSON_GetObjectItem(root,KEY_AUDIO_SUFFIX_SUPPORT);
            if(obj && obj->type == cJSON_Array){
                int size = cJSON_GetArraySize(obj);
                for (int i = 0; i < size; ++i) {
                    cJSON *item = cJSON_GetArrayItem(obj,i);
                    if(item->type == cJSON_String){
                        std::string value = item->valuestring;
                        std::transform(value.begin(),value.end(),value.begin(),::tolower);
                        mAudioSuffix.insert(value);
                    }
                }
            } else{
                LOGW(TAG, "can't find %s, set as default",KEY_AUDIO_SUFFIX_SUPPORT);
                for (int i = 0; i < ELEM(DEFAULT_VALUE_AUDIO_SUPPORT_SUFFIX); ++i) {
                    mAudioSuffix.insert(DEFAULT_VALUE_AUDIO_SUPPORT_SUFFIX[i]);
                }
            }
            obj = cJSON_GetObjectItem(root,KEY_IMAGE_SUFFIX_SUPPORT);
            if(obj && obj->type == cJSON_Array){
                int size = cJSON_GetArraySize(obj);
                for (int i = 0; i < size; ++i) {
                    cJSON *item = cJSON_GetArrayItem(obj,i);
                    if(item->type == cJSON_String){
                        std::string value = item->valuestring;
                        std::transform(value.begin(),value.end(),value.begin(),::tolower);
                        mImageSuffix.insert(value);
                    }
                }
            } else{
                LOGW(TAG, "can't find %s, set as default",KEY_IMAGE_SUFFIX_SUPPORT);
                for (int i = 0; i < ELEM(DEFAULT_VALUE_IMAGE_SUPPORT_SUFFIX); ++i) {
                    mImageSuffix.insert(DEFAULT_VALUE_IMAGE_SUPPORT_SUFFIX[i]);
                }
            }
            success = true;
        }
    }
    return success;
}

std::string MediaScanConfig::dump() {
    std::stringstream result;
    auto it = mConfigLimit.begin();
    while (it != mConfigLimit.end()){
        result << KEY_NAME[it->first];
        result << ":";
        result << it->second;
        result << " ";
        it++;
    }
    result << "\nvideo:";
    auto itV = mVideoSuffix.begin();
    while (itV != mVideoSuffix.end()){
        result << *itV;
        result << ",";
        itV++;
    }
    result << "\naudio:";
    auto itA = mAudioSuffix.begin();
    while (itA != mAudioSuffix.end()){
        result << *itA;
        result << ",";
        itA++;
    }
    result << "\nimage:";
    auto itI = mImageSuffix.begin();
    while (itI != mImageSuffix.end()){
        result << *itI;
        result << ",";
        itI++;
    }
    return result.str();
}

