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
@Entity(nameInDb = "table_video")
public class VideoVo {
    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String symbolName;


    private String path;
    private String width;
    private String height;
    private String size;
    private String duration;
    private String des;
    private String favFlag;

    private Long folderId;

    private String suffix;

    @Generated(hash = 1458415207)
    public VideoVo(Long id, String name, String symbolName, String path,
            String width, String height, String size, String duration, String des,
            String favFlag, Long folderId, String suffix) {
        this.id = id;
        this.name = name;
        this.symbolName = symbolName;
        this.path = path;
        this.width = width;
        this.height = height;
        this.size = size;
        this.duration = duration;
        this.des = des;
        this.favFlag = favFlag;
        this.folderId = folderId;
        this.suffix = suffix;
    }

    @Generated(hash = 724707836)
    public VideoVo() {
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

    public String getWidth() {
        return this.width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
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

    public String getDes() {
        return this.des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFavFlag() {
        return this.favFlag;
    }

    public void setFavFlag(String favFlag) {
        this.favFlag = favFlag;
    }

    public Long getFolderId() {
        return this.folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
}
