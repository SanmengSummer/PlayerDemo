//
// Created by 高成佳 on 2021/7/30.
//

#ifndef SCANNER_STRINGUTILS_H
#define SCANNER_STRINGUTILS_H

#include <string>

class StringUtils {
public:
    static std::string filePath(const std::string & file);
    static const char *rstrstr(const char *src, const char *substr);
    static int strcnt(const char *str, const char *substr);

    static const char *suffix(const char *name);
};


#endif //SCANNER_STRINGUTILS_H
