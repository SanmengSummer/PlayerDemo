package com.app.scanner.vo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Transient;

/**********************************************
 * Filename： FileVo
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1        wangyi   1) …
 ***********************************************/
@Entity(nameInDb = "table_audio")
public class AudioVo {
    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String symbolName;

    private String path;
    private String title;
    private String size;
    private String duration;
    private String year;
    private String favFlag;


    private Long albumId;

    @Transient
    private String album;

    private Long folderId;

    @Transient
    private String folder;

    private Long singerId;

    @Transient
    private String singer;

    private Long genreId;

    @Transient
    private String genre;

    private String suffix;

    @Generated(hash = 2072498362)
    public AudioVo(Long id, String name, String symbolName, String path,
            String title, String size, String duration, String year, String favFlag,
            Long albumId, Long folderId, Long singerId, Long genreId,
            String suffix) {
        this.id = id;
        this.name = name;
        this.symbolName = symbolName;
        this.path = path;
        this.title = title;
        this.size = size;
        this.duration = duration;
        this.year = year;
        this.favFlag = favFlag;
        this.albumId = albumId;
        this.folderId = folderId;
        this.singerId = singerId;
        this.genreId = genreId;
        this.suffix = suffix;
    }

    @Generated(hash = 1141950085)
    public AudioVo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbolName() {
        return this.symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFavFlag() {
        return this.favFlag;
    }

    public void setFavFlag(String favFlag) {
        this.favFlag = favFlag;
    }

    public Long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getFolderId() {
        return this.folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getSingerId() {
        return this.singerId;
    }

    public void setSingerId(Long singerId) {
        this.singerId = singerId;
    }

    public Long getGenreId() {
        return this.genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
