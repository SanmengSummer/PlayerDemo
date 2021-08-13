
#define TAG "main"
#include <Log.h>

#include <MediaMetadataRetriever.h>
#include <FileInfo.h>
#define pathJson "/Users/gaby/work/media/测试/测试资源/音频/内置歌词文件/内置歌词/金南玲\ -\ 逆流成河.mp3"

#include <StringUtils.h>
int main() {

    Utils::Logger::getInstance()
            .setLogTarget(Utils::LOG_TARGET_CONSOLE)
            .build();

    MediaMetadataRetriever retriever;
    retriever.setDataSource(pathJson);

    LOGD("%s:%s",META_KEY_TITLE,retriever.extractMetadata(META_KEY_TITLE));
    LOGD("%s:%s",META_KEY_ALBUM,retriever.extractMetadata(META_KEY_ALBUM));
    LOGD("%s:%s",META_KEY_ARTIST,retriever.extractMetadata(META_KEY_ARTIST));
    LOGD("%s:%s",META_KEY_GENRE,retriever.extractMetadata(META_KEY_GENRE));
    LOGD("%s:%s",META_KEY_YEAR,retriever.extractMetadata(META_KEY_YEAR));

    FileInfo info;

//    av_log_set_level(AV_LOG_INFO);
//    AVFormatContext *context = nullptr;
//    AVDictionaryEntry *tag = nullptr;
//
//    avformat_open_input(&context,path, nullptr, nullptr);
//
//    while ((tag = av_dict_get(context->metadata, "", tag, AV_DICT_IGNORE_SUFFIX)))
//        LOGD("%s=%s",tag->key, tag->value);
//
//    LOGD("DURATION:%d",context->duration);
//    LOGD("---------------------");
//    avformat_close_input(&context);

}
