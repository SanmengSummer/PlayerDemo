//
// Created by 高成佳 on 8/13/21.
//
#include <jni.h>
#include <string>

#include <MediaStatus.h>
#include <sstream>
#include <MediaScannerManager.h>

#define TAG "mediascanner"

#include <Log.h>
#include <iostream>

typedef struct java_callback {
    jclass cls;//回调需要的java bean
} java_callback;

java_callback javaCallback;


static JavaVM *ms2_vm = NULL;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    ms2_vm = vm;
    return JNI_VERSION_1_6;
}

bool attachThreadToJVM(JNIEnv **env) {

    bool ret = false;

    if (ms2_vm != NULL) {
        JavaVMAttachArgs vmAttachArgs;
        vmAttachArgs.version = JNI_VERSION_1_6;
        vmAttachArgs.name = NULL;
        vmAttachArgs.group = NULL;
        jint attachRet = ms2_vm->AttachCurrentThread(env, &vmAttachArgs);

        if (attachRet == JNI_OK) {
            ret = true;
        }
    }

    return ret;
}


std::string listToString(std::list<MediaInfo::SharePtr> infos);

class ListenerImpl : public MediaScannerManagerListener {
public:
    ListenerImpl() = default;

    ~ListenerImpl() override = default;

    void onEvent(std::string deviceId, int event) override {
//        LOGD("deviceId:%s,event:%d", deviceId.c_str(), event);

        JNIEnv *env;
        jboolean jboolean1 = attachThreadToJVM(&env);

        jclass cls = javaCallback.cls;

        //2.获取方法id
        //clazz 类的字节码  name java方法名称  sig java方法签名
        jmethodID mid = env->GetMethodID(cls, "JNICallJavaStatus", "(Ljava/lang/String;)V");

        //3.实例化该类
        jobject jobject = env->AllocObject(cls);

        //5.设置java层参数的值

        std::string str1 = deviceId;
        std::string str2 = std::to_string(event);
        std::string result = str1.append("#").append(str2);

        jstring str = env->NewStringUTF(result.c_str());
        env->CallVoidMethod(jobject, mid, str);

        //删除引用
        env->DeleteLocalRef(str);

        env->DeleteLocalRef(jobject);
//         env->DeleteGlobalRef(cls);
    }

    void
    onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type) override {

        JNIEnv *env;
        jboolean jboolean1 = attachThreadToJVM(&env);

        jclass cls = javaCallback.cls;

        //2.获取方法id
        //clazz 类的字节码  name java方法名称  sig java方法签名
        jmethodID mid = env->GetMethodID(cls, "JNICallJava", "(Ljava/lang/String;)V");

        //3.实例化该类
        jobject jobject = env->AllocObject(cls);

        //5.设置java层参数的值
        jstring str = env->NewStringUTF(listToString(infos).c_str());
        env->CallVoidMethod(jobject, mid, str);

        //删除引用
        env->DeleteLocalRef(str);

        env->DeleteLocalRef(jobject);
//         env->DeleteGlobalRef(cls);
    }
};

std::string listToString(std::list<MediaInfo::SharePtr> infos) {
    std::stringstream result;
    for (auto item : infos) {
        if (item->mFileType == MediaInfo::F_TYPE_AUDIO) {
            std::shared_ptr<MediaInfo> info = item;
            result << *info;
            result << "@@";
        } else {
            std::shared_ptr<FileInfo> info = item;
            result << *info;
            result << "@@";
        }

    }
    return result.str();
}

#define pathJson "C:/Users/yiwan/IdeaProjects/scanner/ScannerNative/src/main/cpp/example/config.json"
#define pathDir1 "sdcard/scannerdb/test1/test1"
#define pathDir2 "/Users/gaby/Downloads/test2"
#define deviceId1 "device1"
#define deviceId2 "device2"

extern "C"
JNIEXPORT void JNICALL
Java_com_landmark_scannernative_Test_native_1init(JNIEnv *env, jobject thiz) {

    jclass cls = env->FindClass("com/landmark/scannernative/Test");
    javaCallback.cls = static_cast<jclass>(env->NewGlobalRef(cls));


    MediaScannerManager *manager = new MediaScannerManager;
    ListenerImpl *listener = new ListenerImpl;
    manager->setMediaScannerManagerListener(listener);
    manager->setConfigPath(pathJson);


    std::string device = manager->setScanPath(deviceId1, pathDir1);
    manager->start(device);
//    if(err == STATUS_OK)
//        manager->wait(device);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_landmark_scannernative_Test_native_1setup(JNIEnv *env, jobject thiz) {
    // TODO: implement native_setup()
}
extern "C" JNIEXPORT jstring
JNICALL
Java_com_landmark_scannernative_Test_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "nihao9527";
    return env->NewStringUTF(hello.c_str());
}

