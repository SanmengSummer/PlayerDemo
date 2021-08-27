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
    jmethodID mid = javaCallback.list;


    jobject resultList = getResult(env, infos);
    jobject resultObj = env->AllocObject(cls);
    env->CallVoidMethod(resultObj, mid, resultList);

    env->DeleteLocalRef(resultList);
    env->DeleteLocalRef(resultObj);

}

void ListenerImpl::onEvent(std::string deviceId, int event) {
    JNIEnv *env;
    jboolean jboolean1 = attachThreadToJVM(&env);

    jclass cls = javaCallback.cls;

    jmethodID mid = javaCallback.midStatus;

    jobject jobject = env->AllocObject(cls);


    std::string str1 = deviceId;
    std::string str2 = std::to_string(event);
    std::string result = str1.append("#").append(str2);

    jstring str = env->NewStringUTF(result.c_str());
    env->CallVoidMethod(jobject, mid, str);

//删除引用
    env->DeleteLocalRef(str);

    env->DeleteLocalRef(jobject);
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


jobject ListenerImpl::getResult(JNIEnv *env, std::list<MediaInfo::SharePtr> infos) {
    jclass list_cls = env->FindClass("java/util/ArrayList");
    jmethodID list_costruct = env->GetMethodID(list_cls, "<init>", "()V");
    jobject list_obj = env->NewObject(list_cls, list_costruct);

    jmethodID list_add = env->GetMethodID(list_cls, "add", "(Ljava/lang/Object;)Z");

    jclass media_info_cls = javaCallback.media_info_cls;

    jmethodID media_info_construct = env->GetMethodID(media_info_cls, "<init>", "()V");

    jfieldID filePath = env->GetFieldID(media_info_cls, "filePath", "Ljava/lang/String;");
    jfieldID fileName = env->GetFieldID(media_info_cls, "fileName", "Ljava/lang/String;");
    jfieldID parentPath = env->GetFieldID(media_info_cls, "parentPath", "Ljava/lang/String;");
    jfieldID composer = env->GetFieldID(media_info_cls, "composer", "Ljava/lang/String;");
    jfieldID genre = env->GetFieldID(media_info_cls, "genre", "Ljava/lang/String;");
    jfieldID year = env->GetFieldID(media_info_cls, "year", "Ljava/lang/String;");
    jfieldID album = env->GetFieldID(media_info_cls, "album", "Ljava/lang/String;");
    jfieldID title = env->GetFieldID(media_info_cls, "title", "Ljava/lang/String;");
    jfieldID artist = env->GetFieldID(media_info_cls, "artist", "Ljava/lang/String;");
    jfieldID fileSize = env->GetFieldID(media_info_cls, "fileSize", "J");
    jfieldID fileModTime = env->GetFieldID(media_info_cls, "fileModTime", "J");
    jfieldID fileChangeTime = env->GetFieldID(media_info_cls, "fileChangeTime", "J");
    jfieldID fileType = env->GetFieldID(media_info_cls, "fileType", "I");
    jfieldID duration = env->GetFieldID(media_info_cls, "duration", "J");

    for (auto item : infos) {
        jobject stu_obj = env->NewObject(media_info_cls, media_info_construct);  //构造一个对象
        if (item->mFileType == MediaInfo::F_TYPE_AUDIO) {
            std::shared_ptr<MediaInfo> info = item;
            env->SetObjectField(stu_obj, title, env->NewStringUTF(info->mTitle.c_str()));
            env->SetObjectField(stu_obj, artist, env->NewStringUTF(info->mArtist.c_str()));
            env->SetObjectField(stu_obj, album, env->NewStringUTF(info->mAlbum.c_str()));
            env->SetObjectField(stu_obj, composer, env->NewStringUTF(info->mComposer.c_str()));
            env->SetObjectField(stu_obj, genre, env->NewStringUTF(info->mGenre.c_str()));
            env->SetObjectField(stu_obj, year, env->NewStringUTF(info->mYear.c_str()));
            env->SetLongField(stu_obj, duration, info->mDuration);
        }
        env->SetObjectField(stu_obj, filePath, env->NewStringUTF(item->mFilePath.c_str()));
        env->SetObjectField(stu_obj, fileName, env->NewStringUTF(item->mFileName.c_str()));
        env->SetObjectField(stu_obj, parentPath, env->NewStringUTF(item->mParentPath.c_str()));
        env->SetLongField(stu_obj, fileSize, item->mFileSize);
        env->SetLongField(stu_obj, fileModTime, item->mFileModTime);
        env->SetLongField(stu_obj, fileChangeTime, item->mFileChangeTime);
        env->SetIntField(stu_obj, fileType, item->mFileType);

        env->CallBooleanMethod(list_obj, list_add, stu_obj); //执行Arraylist类实例的add方法，添加一个对象
    }

    return list_obj;
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
Java_com_landmark_scannernative_ScannerJni_native_1stop(JNIEnv *env, jobject thiz,
                                                        jstring device_id) {
    manager->stop(listener->jstring2str(env, device_id));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_landmark_scannernative_ScannerJni_native_1init(JNIEnv *env, jobject thiz,
                                                        jstring device_id,
                                                        jstring config_path, jstring scan_path) {
    jclass cls = env->FindClass("com/landmark/scannernative/ScannerJni");
    javaCallback.cls = static_cast<jclass>(env->NewGlobalRef(cls));
    javaCallback.midStatus = static_cast<jmethodID>(env->GetMethodID(cls, "JNICallJavaStatus",
                                                                     "(Ljava/lang/String;)V"));

    javaCallback.list = static_cast<jmethodID>(env->GetMethodID(cls, "JNICallJavaMediaList",
                                                                "(Ljava/util/ArrayList;)V"));

    jclass mediaClass = env->FindClass("com/landmark/scannernative/MediaInfoVo");
    javaCallback.media_info_cls = static_cast<jclass>(env->NewGlobalRef(mediaClass));

    manager->setMediaScannerManagerListener(listener);
    manager->setConfigPath(listener->jstring2str(env, config_path));

    std::string device = manager->setScanPath(listener->jstring2str(env, device_id),
                                              listener->jstring2str(env, scan_path));
    manager->start(device);

}
