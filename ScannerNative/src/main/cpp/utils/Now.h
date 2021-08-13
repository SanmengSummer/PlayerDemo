//
// Created by 高成佳 on 2021/2/25.
//

#ifndef TEST_EVENT_HANDLER_NOW_H
#define TEST_EVENT_HANDLER_NOW_H
#include <chrono>

namespace Utils{

    enum CLOCK_TYPE{
        SYSTEM_CLOCK = 0,
        STEADY_CLOCK
    };

    static inline int64_t nowInUSec(CLOCK_TYPE type = SYSTEM_CLOCK){
        if(type == SYSTEM_CLOCK){
            auto now = std::chrono::system_clock::now();
            auto now_tp = std::chrono::time_point_cast<std::chrono::microseconds>(now);
            auto us = now_tp.time_since_epoch().count();
            return us;
        } else if(type == STEADY_CLOCK){
            auto now = std::chrono::steady_clock::now();
            auto now_tp = std::chrono::time_point_cast<std::chrono::microseconds>(now);
            auto us = now_tp.time_since_epoch().count();
            return us;
        }
        return -1;
    }

    static inline int64_t nowInMSec(CLOCK_TYPE type = SYSTEM_CLOCK){
        if(type == SYSTEM_CLOCK){
            auto now = std::chrono::system_clock::now();
            auto nowTP = std::chrono::time_point_cast<std::chrono::milliseconds>(now);
            auto ms = nowTP.time_since_epoch().count();
            return ms;
        } else if(type == STEADY_CLOCK){
            auto now = std::chrono::steady_clock::now();
            auto nowTP = std::chrono::time_point_cast<std::chrono::milliseconds>(now);
            auto ms = nowTP.time_since_epoch().count();
            return ms;
        }

        return -1;
    }


}
#endif //TEST_EVENT_HANDLER_NOW_H
