//
// Created by yiwan on 2021/8/24.
//
#include "MediaScannerManager.h";
#include "iosfwd"
using  namespace std;

typedef struct java_callback {
    jclass cls;
    jmethodID midStatus;
    jmethodID list;
    jclass media_info_cls;
} java_callback;

java_callback javaCallback;


#ifndef CARSCANNER_NATIVE_LIB_H
#define CARSCANNER_NATIVE_LIB_H
class ListenerImpl:public MediaScannerManagerListener {
public:

    ListenerImpl() = default;

    ~ListenerImpl() override = default;


    bool attachThreadToJVM(JNIEnv **env);

    jobject getResult(JNIEnv *env,std::list<MediaInfo::SharePtr> infos);

    string jstring2str(JNIEnv *env, jstring jstr);

    void onEvent(std::string deviceId, int event);

    void onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type);

};


#endif //CARSCANNER_NATIVE_LIB_H
