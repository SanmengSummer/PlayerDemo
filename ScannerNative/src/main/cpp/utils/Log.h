//
// Created by 高成佳 on 2021/3/2.
//

#ifndef TEST_EVENT_HANDLER_LOG_H
#define TEST_EVENT_HANDLER_LOG_H

#define LOG_COMPLEX      1
#define LOG_VERBOSE      1
#define LOG_TRACE        1

#include "Logger.h"

#ifdef LOGF
#undef LOGF
#endif

#ifdef LOGE
#undef LOGE
#endif

#ifdef LOGW
#undef LOGW
#endif

#ifdef LOGI
#undef LOGI
#endif

#ifdef LOGD
#undef LOGD
#endif

#ifdef LOGV
#undef LOGV
#endif

#ifdef TAG
/**
 *      LOG_LEVEL_FATAL = 0,
        LOG_LEVEL_ERROR,
        LOG_LEVEL_WARNING,
        LOG_LEVEL_INFO,
        LOG_LEVEL_DEBUG,
        LOG_LEVEL_VERBOSE,
 */

#if LOG_COMPLEX
#define LOG(level, fmt, ...) do{ Utils::Logger::getInstance().logWrite(TAG,level,"[%s:%d] " fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__); }while(0)
#define LOGF(fmt, ...) do{ Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_FATAL,"[%s:%d] " fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__); }while(0)
#define LOGE(fmt, ...) do{ Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_ERROR,"[%s:%d] " fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__); }while(0)
#define LOGW(fmt, ...) do{ Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_WARNING,"[%s:%d] " fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__); }while(0)
#define LOGI(fmt, ...) do{ Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_INFO,"[%s:%d] " fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__); }while(0)
#define LOGD(fmt, ...) do{ Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_DEBUG,"[%s:%d] " fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__); }while(0)
//        #define LOGV(fmt,...) do{ Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_VERBOSE,"[%s:%d] " fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__); }while(0)
#else
#define LOG(level,fmt,...) do{Utils::Logger::getInstance().logWrite(TAG,level,fmt,##__VA_ARGS__);}while(0)
#define LOGF(fmt,...) do{Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_FATAL,fmt,##__VA_ARGS__);}while(0)
#define LOGE(fmt,...) do{Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_ERROR,fmt,##__VA_ARGS__);}while(0)
#define LOGW(fmt,...) do{Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_WARNING,fmt,##__VA_ARGS__);}while(0)
#define LOGI(fmt,...) do{Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_INFO,fmt,##__VA_ARGS__);}while(0)
#define LOGD(fmt,...) do{Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_DEBUG,fmt,##__VA_ARGS__);}while(0)
//        #define LOGV(fmt,...) do{Utils::Logger::getInstance().logWrite(TAG,Utils::LOG_LEVEL_VERBOSE,fmt,##__VA_ARGS__);}while(0)

#endif
#else
#define LOG(level,fmt,...)
#define LOGF(fmt,...)
#define LOGE(fmt,...)
#define LOGW(fmt,...)
#define LOGI(fmt,...)
#define LOGD(fmt,...)
//    #define LOGV(fmt,...)
#endif

#if LOG_VERBOSE
#define LOGV(tag, fmt, ...) do{ Utils::Logger::getInstance().logWrite(tag,Utils::LOG_LEVEL_VERBOSE,"[%s:%d] " fmt, __FUNCTION__, __LINE__, ##__VA_ARGS__); }while(0)
#else
#define LOGV(tag, fmt, ...)
#endif

#if LOG_TRACE
#define TRACE() LOGV("TRACE", "%s:%d:%s", __FILE__, __LINE__, __FUNCTION__)
#else
#define TRACE()
#endif


#endif //TEST_EVENT_HANDLER_LOG_H
