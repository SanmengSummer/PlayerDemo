package com.app.scanner.vo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "table_device")
public class UsbDeviceVo {
    @Id(autoincrement = true)
    private Long id;
    
    private String path;

    @Generated(hash = 825548272)
    public UsbDeviceVo(Long id, String path) {
        this.id = id;
        this.path = path;
    }

    @Generated(hash = 2042788528)
    public UsbDeviceVo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
