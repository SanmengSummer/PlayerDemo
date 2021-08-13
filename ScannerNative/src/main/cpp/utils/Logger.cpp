//
// Created by 高成佳 on 2021/2/26.
//

#include <iostream>

#include <sys/time.h>
#include <dirent.h>
#include <sys/stat.h>

#ifdef __ANDROID__
#include <android/log.h>
#endif

#include "Logger.h"

namespace Utils {
    typedef  std::unique_lock<std::mutex> AutoLock;
    std::once_flag Logger::sOnceFlag;
    const std::string Logger::DEFAULT_FILE_NAME = "tiny_log.log";
    const std::string Logger::DEFAULT_FILE_PATH = "./";
    const std::string Logger::LEVEL_STRING[LOG_LEVEL_MAX] = {"fatal", "error", "warn", "info", "debug", "verbose"};
    const int Logger::MB = 1024 * 1024;
    const int Logger::MAX_LOG_FILE_SIZE = MB * 50;
    const int Logger::MAX_LOG_LENGTH = 4096;

    Logger::Logger(){
        mFile = nullptr;
        mLogLevel = LOG_LEVEL_VERBOSE;
        mLogTarget = LOG_TARGET_CONSOLE;
        mFilePath = DEFAULT_FILE_PATH;
        mFileSize = 0;
        mFileName = DEFAULT_FILE_NAME;
    }

    Logger::~Logger() {
        if(mFile){
            fflush(mFile);
            fclose(mFile);
        }
    }

    void Logger::logWrite(const char *tag, LogLevel level, const char *fmt, ...) {
        va_list args;
        va_start(args,fmt);
        logWrite(tag,level,fmt,args);
        va_end(args);
    }

    void Logger::logWrite(const char *tag, LogLevel level, const char *fmt, va_list args) {
        if(level <= mLogLevel){
            AutoLock lock(mLogMutex);
            std::string logBuf;
            std::string now;
#ifndef __ANDROID__
            now = timeDateNow();
#endif
            if(mFileSize > MAX_LOG_FILE_SIZE){
                rotateFile();
            }
            char buf[MAX_LOG_LENGTH];
            int len = snprintf(buf,MAX_LOG_LENGTH,"%s %-8s %-20s %p\t",now.c_str(),LEVEL_STRING[level].c_str(),tag,pthread_self());
            vsnprintf(buf + len,MAX_LOG_LENGTH - len,fmt,args);

            if (mLogTarget == LOG_TARGET_FILE){
                if(mFile != nullptr){
                    mFileSize += fprintf(mFile,"%s\n",buf);
                    fflush(mFile);
                } else {
#ifdef __ANDROID__
                    loggerAndroidPrint(tag,level,"log target file  but  not file *  error");
#else
                    fprintf(stderr,"%s\n","log target file  but  not file *  error");
#endif
                }
            } else {
#ifdef __ANDROID__
                loggerAndroidPrint(tag,level,buf);
#else
                fprintf(stdout,"%s\n",buf);
#endif
            }
        }
    }

    void Logger::build() {
        AutoLock lock(mLogMutex);
        std::call_once(sOnceFlag,[&](){
            this->initCallOnce();
        });
    }

    void Logger::initCallOnce() {
        if(mLogTarget == LOG_TARGET_FILE){
            std::string buf;
            int a = mFilePath.find_last_of("/");
            int b = mFilePath.size();
            if(!opendir(mFilePath.c_str())){
                mkdir(mFilePath.c_str(), 0755);
            }
            if(mFilePath.find_last_of("/") != mFilePath.size() -1){
                buf = mFilePath + "/" + mFileName;
            } else{
                buf = mFilePath + mFileName;
            }

            mFile = fopen(buf.c_str(),"a");
            if(mFile){
                mFileSize = ftell(mFile);
            }
        } else if(mLogTarget == LOG_TARGET_CONSOLE){

        } else {
            std::cout << timeDateNow() + "please set target..." << std::endl;
        }

    }

    const std::string Logger::timeDateNow() {
        static char timeStr[32];
        char tmBuf[32];
        struct tm *nowtm;
        struct timeval tv;
        time_t now_time;

        gettimeofday(&tv, NULL);
        now_time = tv.tv_sec;
        nowtm = localtime(&now_time);
        strftime(tmBuf, sizeof(tmBuf)/sizeof(tmBuf[0]), "%Y-%m-%d %H:%M:%S", nowtm);
        snprintf(timeStr, sizeof(timeStr)/sizeof(timeStr[0]), "%s.%03ld", tmBuf, tv.tv_usec/1000);
        return timeStr;
    }

    void Logger::loggerAndroidPrint(const char *tag, int level, const char *msg)
    {
#ifdef __ANDROID__
        int android_level = ANDROID_LOG_DEBUG;
    switch (level) {
        case LOG_LEVEL_FATAL:
            android_level = ANDROID_LOG_FATAL;
            break;
        case LOG_LEVEL_ERROR:
            android_level = ANDROID_LOG_ERROR;
            break;
        case LOG_LEVEL_WARNING:
            android_level = ANDROID_LOG_WARN;
            break;
        case LOG_LEVEL_INFO:
            android_level = ANDROID_LOG_INFO;
            break;
        case LOG_LEVEL_DEBUG:
            android_level = ANDROID_LOG_DEBUG;
            break;
        case LOG_LEVEL_VERBOSE:
            android_level = ANDROID_LOG_VERBOSE;
            break;
        default:
            android_level = ANDROID_LOG_DEBUG;
            break;
    }

    __android_log_print(android_level, tag, "%s", msg);
#endif
    }

    void Logger::rotateFile() {
        struct dirent *de = nullptr;
        int count = 0;
        DIR *dir = opendir(mFilePath.c_str());
        if(dir){
            do {
                de = readdir(dir);
                if(de){
                    if(mFileName.find(de->d_name) != std::string::npos){
                        count++;
                    }
                }
            }while (de);
        }
        fclose(mFile);
        char buf[1024];
        sprintf(buf,"%s.%d",mFileName.c_str(),count);
        mFile = fopen(buf,"a");
        mFileSize = 0;
        mFileName = std::string(buf);
    }


}