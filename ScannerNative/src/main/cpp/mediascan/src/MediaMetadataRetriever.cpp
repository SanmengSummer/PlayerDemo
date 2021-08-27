//
// Created by 高成佳 on 2021/7/28.
//
#define TAG "MediaMetadataRetriever"
#include <Log.h>
#include "MediaMetadataRetriever.h"

#ifdef __ANDROID1__
#include <media/CharacterEncodingDetector.h>
#endif

#define BUFF_LEN 1024

#define CHECK(exp) do {                         \
	int ret = (exp);                            \
	if (ret < 0) {                              \
        char buff[BUFF_LEN] = { 0 };            \
        av_strerror(ret, buff, BUFF_LEN - 1);   \
        LOGW("MediaRetriever", "%s", buff);     \
        return ret;                             \
    }                                           \
} while(0)

MediaMetadataRetriever::MediaMetadataRetriever()
    :mAvFormatContext(nullptr),
    mParsed(false){
    av_log_set_level(AV_LOG_INFO);
}

MediaMetadataRetriever::~MediaMetadataRetriever() {
    if(mAvFormatContext){
        avformat_close_input(&mAvFormatContext);
    }
    clearMetadata();
}

int MediaMetadataRetriever::setDataSource(const char *path) {
    if(path == nullptr){
        LOGE("path is null.");
        return -1;
    }
    CHECK(avformat_open_input(&mAvFormatContext,path, nullptr, nullptr));
    return 0;
}

void MediaMetadataRetriever::reset() {
    if(mAvFormatContext){
        avformat_close_input(&mAvFormatContext);
        mAvFormatContext = nullptr;
    }
    clearMetadata();
}

const char *MediaMetadataRetriever::extractMetadata(const char *name) {
    if(name == nullptr){
        LOGE("name is null.");
        return nullptr;
    }
    if(!mParsed){
        parseMetadata();
    }
    char* value = "";
    bool ret = mMetaData.findString(name,&value);
    if(!ret){
        LOGE("not found name:%s",name);
    }
    return value;
}

int64_t MediaMetadataRetriever::getDuration() {
    if(!mParsed){
        parseMetadata();
    }
    int64_t value = 0;
    bool ret = mMetaData.findInt64(META_KEY_DURATION,&value);
    if(!ret){
        LOGE("not found metadata:%s ",META_KEY_DURATION);
    }
    return value;
}

void MediaMetadataRetriever::parseMetadata() {
    AVDictionaryEntry * entry = NULL;
    if (mAvFormatContext) {
#ifdef __ANDROID1__
        android::CharacterEncodingDetector* detector = new android::CharacterEncodingDetector();
        while ((entry = av_dict_get(fmt_ctx_->metadata, "", entry, AV_DICT_IGNORE_SUFFIX)) != NULL) {
            detector->addTag(entry->key, entry->value);
        }

        detector->detectAndConvert();
        int size = detector->size();
        if (size) {
            for (int i = 0; i < size; i++) {
                const char* name;
                const char* value;
                detector->getTag(i, &name, &value);
                metadata_.setString(name, value);
            }
        }

        delete detector;
#else
        while ((entry = av_dict_get(mAvFormatContext->metadata, "", entry, AV_DICT_IGNORE_SUFFIX)) != NULL) {
            mMetaData.setString(entry->key, entry->value);
            LOGD("%s,%s",entry->key,entry->value);
        }
#endif
        mMetaData.setInt64(META_KEY_DURATION, mAvFormatContext->duration);
    }
    else {
        LOGE(TAG, "FormatContext is NULL, may be un known file format");
    }
    mParsed = true;
}

void MediaMetadataRetriever::clearMetadata() {
    mParsed = false;
    mMetaData.clear();
}
