package com.app.scanner.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

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
    private String size;
    private String duration;
    private String year;
    private String favFlag;


    private Long albumId; //该authId就是Author的主键id，注意类型一定要相同，这里都为long类型


    private Long folderId; //该authId就是Author的主键id，注意类型一定要相同，这里都为long类型

    private Long singerId; //该authId就是Author的主键id，注意类型一定要相同，这里都为long类型

    private String suffix;

    @Generated(hash = 71962206)
    public AudioVo(Long id, String name, String symbolName, String path,
            String size, String duration, String year, String favFlag, Long albumId,
            Long folderId, Long singerId, String suffix) {
        this.id = id;
        this.name = name;
        this.symbolName = symbolName;
        this.path = path;
        this.size = size;
        this.duration = duration;
        this.year = year;
        this.favFlag = favFlag;
        this.albumId = albumId;
        this.folderId = folderId;
        this.singerId = singerId;
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

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }


}
