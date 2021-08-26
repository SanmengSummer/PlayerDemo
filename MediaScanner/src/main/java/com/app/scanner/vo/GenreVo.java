package com.app.scanner.vo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

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
@Entity(nameInDb = "table_genre")
public class GenreVo {
    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String symbolName;
    @Generated(hash = 903553696)
    public GenreVo(Long id, String name, String symbolName) {
        this.id = id;
        this.name = name;
        this.symbolName = symbolName;
    }
    @Generated(hash = 1632986011)
    public GenreVo() {
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

}
