//
// Created by 高成佳 on 8/13/21.
//
#include <jni.h>
#include <string>

#include <MediaStatus.h>
#include <sstream>
#include "native_lib.h"


#define TAG "mediascanner"

#include <Log.h>
#include <iostream>


static JavaVM *ms2_vm = NULL;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    ms2_vm = vm;
    return JNI_VERSION_1_6;
}

void
ListenerImpl::onFileInfoList(std::string deviceId, std::list<MediaInfo::SharePtr> infos, int type) {

    JNIEnv *env;
    jboolean jboolean1 = attachThreadToJVM(&env);

    jclass cls = javaCallback.cls;

//2.获取方法id
//clazz 类的字节码  name java方法名称  sig java方法签名
//    jmethodID mid = env->GetMethodID(cls, "JNICallJava", "(Ljava/lang/String;)V");
    jmethodID mid = javaCallback.midInfo;

//3.实例化该类
    jobject jobject = env->AllocObject(cls);

//5.设置java层参数的值
    jstring str = env->NewStringUTF(listToString(infos).str().c_str());
    env->CallVoidMethod(jobject, mid, str);

//删除引用
    env->DeleteLocalRef(str);

    env->DeleteLocalRef(jobject);
//         env->DeleteGlobalRef(cls);
}

void ListenerImpl::onEvent(std::string deviceId, int event) {
    LOGD("deviceId:%s,event:%d", deviceId.c_str(), event);
    JNIEnv *env;
    jboolean jboolean1 = attachThreadToJVM(&env);

    jclass cls = javaCallback.cls;

//2.获取方法id
//clazz 类的字节码  name java方法名称  sig java方法签名
    jmethodID mid = javaCallback.midStatus;

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

bool ListenerImpl::attachThreadToJVM(JNIEnv **env) {

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


stringstream ListenerImpl::listToString(std::list<MediaInfo::SharePtr> infos) {
    stringstream result;
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
    return result;
}

string ListenerImpl::jstring2str(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("UTF-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    std::string stemp(rtn);
    free(rtn);
    return stemp;
}

MediaScannerManager *manager = new MediaScannerManager;;
ListenerImpl *listener = new ListenerImpl();

extern "C"
JNIEXPORT void JNICALL
Java_com_landmark_scannernative_Test_native_1stop(JNIEnv *env, jobject thiz, jstring device_id) {
    manager->stop(listener->jstring2str(env, device_id));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_landmark_scannernative_Test_native_1init(JNIEnv *env, jobject thiz, jstring device_id,
                                                  jstring config_path, jstring scan_path) {
    jclass cls = env->FindClass("com/landmark/scannernative/Test");
    javaCallback.cls = static_cast<jclass>(env->NewGlobalRef(cls));
    javaCallback.midInfo = static_cast<jmethodID>(env->GetMethodID(cls, "JNICallJava", "(Ljava/lang/String;)V"));
    javaCallback.midStatus = static_cast<jmethodID>(env->GetMethodID(cls, "JNICallJavaStatus", "(Ljava/lang/String;)V"));


    manager->setMediaScannerManagerListener(listener);
    manager->setConfigPath(listener->jstring2str(env, config_path));

    std::string device = manager->setScanPath(listener->jstring2str(env, device_id),
                                              listener->jstring2str(env, scan_path));
    manager->start(device);

}