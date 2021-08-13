//
// Created by 高成佳 on 2021/2/27.
//

#ifndef TEST_EVENT_HANDLER_SINGLETON_H
#define TEST_EVENT_HANDLER_SINGLETON_H

#include "Logger.h"

namespace Utils {

    template<class T>
    class SingleTon {
    public:
        static T& getInstance() {
            static T instance;
            return instance;
        }

        SingleTon(const SingleTon &other) = delete;
        SingleTon &operator=(const SingleTon &other) = delete;
        SingleTon(SingleTon&&) = delete ;

    protected:
        SingleTon() = default;
        virtual ~SingleTon() = default;

    };

}

#endif //TEST_EVENT_HANDLER_SINGLETON_H
