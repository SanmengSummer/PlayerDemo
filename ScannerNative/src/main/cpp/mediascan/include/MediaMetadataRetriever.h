//
// Created by 高成佳 on 2021/7/28.
//

#ifndef SCANNER_MEDIAMETADATARETRIEVER_H
#define SCANNER_MEDIAMETADATARETRIEVER_H

#include <ffmpeg.h>
#include <AData.h>

#define META_KEY_TITLE      "title"
#define META_KEY_ARTIST     "artist"
#define META_KEY_ALBUM      "album"
#define META_KEY_COMPOSER   "composer"
#define META_KEY_GENRE      "genre"
#define META_KEY_YEAR       "year"
#define META_KEY_DURATION   "duration"

class MediaMetadataRetriever {
public:
    using SharePtr = std::shared_ptr<MediaMetadataRetriever>;
    MediaMetadataRetriever();
    virtual ~MediaMetadataRetriever();

    int setDataSource(const char* path);
    void reset();

    const char* extractMetadata(const char *name);
    int64_t getDuration();

private:
    void parseMetadata();
    void clearMetadata();

private:
    AVFormatContext* mAvFormatContext;
    AData            mMetaData;
    bool             mParsed;
};


#endif //SCANNER_MEDIAMETADATARETRIEVER_H
