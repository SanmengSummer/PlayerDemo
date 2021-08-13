//
// Created by 高成佳 on 8/13/21.
//
#include <jni.h>
#include <string>

extern "C" JNIEXPORT void JNICALL
Java_com_landmark_scannernative_Test_test(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

}
extern "C"
JNIEXPORT void JNICALL
Java_com_landmark_scannernative_Test_native_1init(JNIEnv *env, jobject thiz) {
    // TODO: implement native_init()
}extern "C"
JNIEXPORT void JNICALL
Java_com_landmark_scannernative_Test_native_1setup(JNIEnv *env, jobject thiz) {
    // TODO: implement native_setup()
}