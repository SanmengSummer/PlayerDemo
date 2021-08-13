package com.app.scanner.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

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
@Entity(nameInDb = "table_record")
public class RecordVo {
    @Id(autoincrement = true)
    private Long id;

    private Long mediaId;

    private String endDuration;

    private String playTime;

    @Override
    public String toString() {
        return "RecordVo{" +
                "id=" + id +
                ", mediaId=" + mediaId +
                ", endDuration='" + endDuration + '\'' +
                '}';
    }

    @Generated(hash = 694501553)
    public RecordVo(Long id, Long mediaId, String endDuration, String playTime) {
        this.id = id;
        this.mediaId = mediaId;
        this.endDuration = endDuration;
        this.playTime = playTime;
    }

    @Generated(hash = 75010410)
    public RecordVo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getEndDuration() {
        return this.endDuration;
    }

    public void setEndDuration(String endDuration) {
        this.endDuration = endDuration;
    }

    public String getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

}
