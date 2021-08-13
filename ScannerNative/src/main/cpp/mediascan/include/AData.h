
//
// Created by 高成佳 on 2021/7/28.
//

#ifndef SCANNER_ADATA_H
#define SCANNER_ADATA_H

#include <string>
//#include <cstdio>
#include <unordered_map>

class AData {
public:
    AData();

    virtual ~AData();

    void setInt32(const char *name, int32_t value);

    void setInt64(const char *name, int64_t value);

    void setSize(const char *name, size_t value);

    void setFloat(const char *name, float value);

    void setDouble(const char *name, double value);

    void setPointer(const char *name, void *value);

    void setString(const char *name, const char *value);

    bool findInt32(const char *name, int32_t *value);

    bool findInt64(const char *name, int64_t *value);

    bool findSize(const char *name, size_t *value);

    bool findFloat(const char *name, float *value);

    bool findDouble(const char *name, double *value);

    bool findPointer(const char *name, void **value);

    bool findString(const char *name, char **value);

    int size();

    void clear();

private:
    enum Type {
        kTypeInt32,
        kTypeInt64,
        kTypeSize,
        kTypeFloat,
        kTypeDouble,
        kTypePointer,
        kTypeString,
    };

    struct Item {
        ~Item();
        union Data {
            int32_t int32Value;
            int64_t int64Value;
            size_t sizeValue;
            float floatValue;
            double doubleValue;
            void *ptrValue;
            char *strValue;
        };
        Data data;
        Type type;
    };

private:
    std::unordered_map<std::string, Item *> mDataMap;
};


#endif //SCANNER_ADATA_H
