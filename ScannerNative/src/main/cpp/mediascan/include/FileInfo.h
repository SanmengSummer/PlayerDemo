//
// Created by 高成佳 on 2021/7/29.
//

#ifndef SCANNER_FILEINFO_H
#define SCANNER_FILEINFO_H

#include <string>
#include <list>
#include <ostream>

struct FileInfo {
    using SharePtr = std::shared_ptr<FileInfo>;
    FileInfo();
    friend std::ostream &operator<<(std::ostream &os, const FileInfo &info);
    enum {
        F_TYPE_UNKNOWN = -1,
        F_TYPE_DIR = 0,
        F_TYPE_AUDIO,
        F_TYPE_VIDEO,
        F_TYPE_IMAGE,
        F_TYPE_MEDIA
    };
    std::string mFilePath;
    std::string mFileName;
    std::string mParentPath;
    int64_t     mFileSize;
    int64_t     mFileModTime;
    int64_t     mFileChangeTime;
    int         mFileType;

};

struct MediaInfo : public FileInfo{
    MediaInfo();
    using SharePtr = std::shared_ptr<MediaInfo>;
    friend std::ostream &operator<<(std::ostream &os, const MediaInfo &info);
    std::string mTitle;
    std::string mArtist;
    std::string mAlbum;
    std::string mComposer;
    std::string mGenre;
    std::string mYear;
    int64_t     mDuration;
};

#endif //SCANNER_FILEINFO_H
