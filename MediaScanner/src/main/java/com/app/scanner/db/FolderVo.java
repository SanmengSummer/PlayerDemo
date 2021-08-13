package com.app.scanner.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
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
@Entity(nameInDb = "table_folder")
public class FolderVo {
    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String symbolName;
    private String path;
    private String parentPath;
    private String parentId;
    @Generated(hash = 404621743)
    public FolderVo(Long id, String name, String symbolName, String path,
            String parentPath, String parentId) {
        this.id = id;
        this.name = name;
        this.symbolName = symbolName;
        this.path = path;
        this.parentPath = parentPath;
        this.parentId = parentId;
    }
    @Generated(hash = 243982118)
    public FolderVo() {
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
    public String getParentPath() {
        return this.parentPath;
    }
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
    public String getParentId() {
        return this.parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

}
