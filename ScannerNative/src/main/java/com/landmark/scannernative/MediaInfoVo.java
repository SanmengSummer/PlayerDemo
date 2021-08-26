package com.landmark.scannernative;

public class MediaInfoVo  {
    public String filePath;
    public String fileName;
    public String parentPath;
    public long fileSize;
    public long fileModTime;
    public long fileChangeTime;
    public int fileType;


    public String title;
    public String artist;
    public String album;
    public String composer;
    public String genre;
    public String year;
    public long duration;

    public MediaInfoVo() {
    }

    @Override
    public String toString() {
        return "MediaInfoVo{" +
                "filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", parentPath='" + parentPath + '\'' +
                ", fileSize=" + fileSize +
                ", fileModTime=" + fileModTime +
                ", fileChangeTime=" + fileChangeTime +
                ", fileType=" + fileType +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", composer='" + composer + '\'' +
                ", genre='" + genre + '\'' +
                ", year='" + year + '\'' +
                ", duration=" + duration +
                '}';
    }
}
