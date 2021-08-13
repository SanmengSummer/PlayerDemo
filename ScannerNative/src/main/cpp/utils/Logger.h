//
// Created by 高成佳 on 2021/2/26.
//

#ifndef TEST_EVENT_HANDLER_LOGGER_H
#define TEST_EVENT_HANDLER_LOGGER_H

#include <mutex>
#include <string>

#include "SingleTon.h"

namespace Utils {

    enum LogLevel{
        LOG_LEVEL_FATAL = 0,
        LOG_LEVEL_ERROR,
        LOG_LEVEL_WARNING,
        LOG_LEVEL_INFO,
        LOG_LEVEL_DEBUG,
        LOG_LEVEL_VERBOSE,
        LOG_LEVEL_MAX
    };

    enum LogTarget{
        LOG_TARGET_FILE = 0x01,
        LOG_TARGET_CONSOLE = 0x10
    };

    class Logger : public SingleTon<Logger>{
    private:
        static const int MB;
        static const int MAX_LOG_FILE_SIZE;
        static const int MAX_LOG_LENGTH;
        static const std::string DEFAULT_FILE_NAME;
        static const std::string DEFAULT_FILE_PATH;
        static const std::string LEVEL_STRING[LOG_LEVEL_MAX];
        static std::once_flag sOnceFlag;
        friend class SingleTon<Logger>;
    public:
        inline Logger& setLogLevel(LogLevel level){
            mLogLevel = level;
            return *this;
        }
        inline LogLevel getLogLevel(){
            return mLogLevel;
        }
        inline Logger& setLogTarget(LogTarget target){
            mLogTarget = target;
            return *this;
        }
        inline LogTarget getLogTarget(){
            return mLogTarget;
        }
        inline Logger& setFileName(std::string fileName){
            mFileName = fileName;
            return *this;
        }
        inline std::string getFileName(){
            return mFileName;
        }

        inline Logger& setFilePath(std::string filePath){
            mFilePath = filePath;
            return *this;
        }

        inline std::string getFilePath(){
            return mFilePath;
        }

        void build();

        void logWrite(const char* tag,const LogLevel level, const char* fmt, ...);

    private:
        Logger();
        virtual ~Logger() override;
        void initCallOnce();
        static const std::string timeDateNow();
        static void loggerAndroidPrint(const char *tag, int level, const char *msg);
        void logWrite(const char* tag,const LogLevel level, const char* fmt, va_list args);
        void rotateFile();
    private:
        std::mutex mLogMutex;
        LogLevel mLogLevel;
        LogTarget mLogTarget;
        std::string mFileName;
        std::string mFilePath;
        int mFileSize;
        FILE* mFile;


    };
}

#endif //TEST_EVENT_HANDLER_LOGGER_H
