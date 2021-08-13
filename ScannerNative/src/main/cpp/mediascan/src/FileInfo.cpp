//
// Created by 高成佳 on 2021/7/29.
//

#include <sstream>
#include "FileInfo.h"

FileInfo::FileInfo()
    :mFileSize(0),
    mFileType(F_TYPE_UNKNOWN),
    mFileChangeTime(0),
    mFileModTime(0){

}

std::ostream &operator<<(std::ostream &os, const FileInfo &info) {
    os << "mFilePath: " << info.mFilePath << " mFileName: " << info.mFileName << " mParentPath: " << info.mParentPath
       << " mFileSize: " << info.mFileSize << " mFileModTime: " << info.mFileModTime << " mFileChangeTime: "
       << info.mFileChangeTime << " mFileType: " << info.mFileType;
    return os;
}

MediaInfo::MediaInfo()
    :mDuration(0){

}

std::ostream &operator<<(std::ostream &os, const MediaInfo &info) {
    os << static_cast<const FileInfo &>(info) << " mTitle: " << info.mTitle << " mArtist: " << info.mArtist
       << " mAlbum: " << info.mAlbum << " mComposer: " << info.mComposer << " mGenre: " << info.mGenre << " mYear: "
       << info.mYear << " mDuration: " << info.mDuration;
    return os;
}

