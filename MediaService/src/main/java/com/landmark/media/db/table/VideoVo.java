package com.landmark.media.db.table;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

import com.landmark.media.db.dao.DaoSession;
import com.landmark.media.db.dao.FolderVoDao;
import com.landmark.media.db.dao.VideoVoDao;

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
public class VideoVo implements Parcelable {
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
    private boolean favFlag;

    private Long folderId;
    @ToOne(joinProperty = "folderId") // 注意该参数的值
    private FolderVo folderVo;

    private String suffix;

    @Keep
    public VideoVo(Long id, String name, String symbolName, String path,
                   String width, String height, String size, String duration, String des,
                   boolean favFlag, Long folderId, String suffix) {
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


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1635686857)
    private transient VideoVoDao myDao;

    @Generated(hash = 6141150)
    private transient Long folderVo__resolvedKey;

    protected VideoVo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        symbolName = in.readString();
        path = in.readString();
        width = in.readString();
        height = in.readString();
        size = in.readString();
        duration = in.readString();
        des = in.readString();
        favFlag = in.readByte() != 0;
        if (in.readByte() == 0) {
            folderId = null;
        } else {
            folderId = in.readLong();
        }
        folderVo = in.readParcelable(FolderVo.class.getClassLoader());
        suffix = in.readString();
    }

    public static final Creator<VideoVo> CREATOR = new Creator<VideoVo>() {
        @Override
        public VideoVo createFromParcel(Parcel in) {
            return new VideoVo(in);
        }

        @Override
        public VideoVo[] newArray(int size) {
            return new VideoVo[size];
        }
    };

    public boolean isFavFlag() {
        return favFlag;
    }

    public void setFavFlag(boolean favFlag) {
        this.favFlag = favFlag;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeString(symbolName);
        dest.writeString(path);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(size);
        dest.writeString(duration);
        dest.writeString(des);
        dest.writeByte((byte) (favFlag ? 1 : 0));
        if (folderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(folderId);
        }
        dest.writeParcelable(folderVo, flags);
        dest.writeString(suffix);
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 2110994881)
    public FolderVo getFolderVo() {
        Long __key = this.folderId;
        if (folderVo__resolvedKey == null || !folderVo__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FolderVoDao targetDao = daoSession.getFolderVoDao();
            FolderVo folderVoNew = targetDao.load(__key);
            synchronized (this) {
                folderVo = folderVoNew;
                folderVo__resolvedKey = __key;
            }
        }
        return folderVo;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1009014164)
    public void setFolderVo(FolderVo folderVo) {
        synchronized (this) {
            this.folderVo = folderVo;
            folderId = folderVo == null ? null : folderVo.getId();
            folderVo__resolvedKey = folderId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1762629499)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getVideoVoDao() : null;
    }

    public boolean getFavFlag() {
        return this.favFlag;
    }
}
