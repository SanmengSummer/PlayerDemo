//
// Created by 高成佳 on 2021/7/30.
//
#define TAG "StringUtils"
#include <Log.h>

#include "StringUtils.h"

std::string StringUtils::filePath(const std::string &file) {
    const char SEP1 = '/';
    int i = file.length() - 1;
    for (; i >= 0; i--) {
        char ch = file.at(i);
        if (ch == SEP1) {
            continue;
        } else {
            break;
        }
    }
    if (i < 0 || file.empty()) {
        return "";
    } else{
        return file.substr(0, i + 1);
    }

}

int StringUtils::strcnt(const char *str, const char *substr) {
    int cnt = 0;
    const char * pos = str;
    while ((pos = (strstr(pos, substr))) != nullptr){
        pos += strlen(substr);
        cnt++;
    }
    return cnt;
}

const char *StringUtils::suffix(const char *name) {
    const char *s = rstrstr(name, ".");
    if (s) {
        s++;
        if (s < name + strlen(name)) {
            return s;
        }
    }

    return nullptr;
}

const char *StringUtils::rstrstr(const char *src, const char *substr) {
    if (!src || !substr) {
        return nullptr;
    }

    size_t src_len = strlen(src);
    size_t substr_len = strlen(substr);
    int pos = src_len - substr_len;

    while (pos >= 0) {
        const char *target = &src[pos];
        if (strncmp(target, substr, substr_len) == 0) {
            return target;
        }

        pos--;
    }

    return nullptr;
}
