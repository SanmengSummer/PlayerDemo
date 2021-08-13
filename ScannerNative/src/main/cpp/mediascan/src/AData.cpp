//
// Created by 高成佳 on 2021/7/28.
//

#include "AData.h"

AData::AData() {

}

AData::~AData() {
    clear();
}


#define BASIC_TYPE(NAME, FIELDNAME, TYPENAME)                   \
void AData::set##NAME(const char *name, TYPENAME value) {   \
    auto index = mDataMap.find(name);                           \
    if (index != mDataMap.end()) {                              \
        index->second->type = kType##NAME;                      \
        index->second->data.FIELDNAME = value;                  \
    } else {                                                    \
        Item *item = new Item;                                  \
        item->type = kType##NAME;                               \
        item->data.FIELDNAME = value;                           \
        mDataMap.insert({ name, item });                            \
    }                                                           \
}                                                               \
                                                                \
bool AData::find##NAME(const char *name, TYPENAME *value) { \
    auto index = mDataMap.find(name);                           \
    if (index != mDataMap.end()) {                              \
        *value = index->second->data.FIELDNAME;                 \
        return true;                                            \
    }                                                           \
    return false;                                               \
}

BASIC_TYPE(Int32, int32Value, int32_t)
BASIC_TYPE(Int64, int64Value, int64_t)
BASIC_TYPE(Size, sizeValue, size_t)
BASIC_TYPE(Float, floatValue, float)
BASIC_TYPE(Double, doubleValue, double)
BASIC_TYPE(Pointer, ptrValue, void *)

#undef BASIC_TYPE

void AData::setString(const char *name, const char *value) {
    if(value == nullptr){
        return;
    }
    char* buff = strdup(value);
    auto index = mDataMap.find(name);
    if(index != mDataMap.end()){
        auto old = *index;
        delete old.second;

        index->second->type = kTypeString;
        index->second->data.strValue = buff;
    } else{
        Item* item = new Item;
        item->type = kTypeString;
        item->data.strValue = buff;
        mDataMap.insert({name,item});
    }
}

bool AData::findString(const char *name, char **value) {
    auto index = mDataMap.find(name);
    if(index != mDataMap.end()){
        *value = index->second->data.strValue;
        return true;
    }
    return false;
}

int AData::size() {
    return mDataMap.size();
}

void AData::clear() {
    while (!mDataMap.empty()){
        auto it = mDataMap.begin();
        if(it->second){
            delete it->second;
        }
        mDataMap.erase(it);
    }
}

AData::Item::~Item() {
    if(type == kTypeString){
        if(data.strValue){
            free(data.strValue);
        }
    }
}
