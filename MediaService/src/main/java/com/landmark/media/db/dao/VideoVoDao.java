package com.landmark.media.db.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.landmark.media.db.table.FolderVo;

import com.landmark.media.db.table.VideoVo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "table_video".
*/
public class VideoVoDao extends AbstractDao<VideoVo, Long> {

    public static final String TABLENAME = "table_video";

    /**
     * Properties of entity VideoVo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property SymbolName = new Property(2, String.class, "symbolName", false, "SYMBOL_NAME");
        public final static Property Path = new Property(3, String.class, "path", false, "PATH");
        public final static Property Width = new Property(4, String.class, "width", false, "WIDTH");
        public final static Property Height = new Property(5, String.class, "height", false, "HEIGHT");
        public final static Property Size = new Property(6, String.class, "size", false, "SIZE");
        public final static Property Duration = new Property(7, String.class, "duration", false, "DURATION");
        public final static Property Des = new Property(8, String.class, "des", false, "DES");
        public final static Property FavFlag = new Property(9, boolean.class, "favFlag", false, "FAV_FLAG");
        public final static Property FolderId = new Property(10, Long.class, "folderId", false, "FOLDER_ID");
        public final static Property Suffix = new Property(11, String.class, "suffix", false, "SUFFIX");
    }

    private DaoSession daoSession;


    public VideoVoDao(DaoConfig config) {
        super(config);
    }
    
    public VideoVoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"table_video\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"SYMBOL_NAME\" TEXT," + // 2: symbolName
                "\"PATH\" TEXT," + // 3: path
                "\"WIDTH\" TEXT," + // 4: width
                "\"HEIGHT\" TEXT," + // 5: height
                "\"SIZE\" TEXT," + // 6: size
                "\"DURATION\" TEXT," + // 7: duration
                "\"DES\" TEXT," + // 8: des
                "\"FAV_FLAG\" INTEGER NOT NULL ," + // 9: favFlag
                "\"FOLDER_ID\" INTEGER," + // 10: folderId
                "\"SUFFIX\" TEXT);"); // 11: suffix
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"table_video\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, VideoVo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String symbolName = entity.getSymbolName();
        if (symbolName != null) {
            stmt.bindString(3, symbolName);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(4, path);
        }
 
        String width = entity.getWidth();
        if (width != null) {
            stmt.bindString(5, width);
        }
 
        String height = entity.getHeight();
        if (height != null) {
            stmt.bindString(6, height);
        }
 
        String size = entity.getSize();
        if (size != null) {
            stmt.bindString(7, size);
        }
 
        String duration = entity.getDuration();
        if (duration != null) {
            stmt.bindString(8, duration);
        }
 
        String des = entity.getDes();
        if (des != null) {
            stmt.bindString(9, des);
        }
        stmt.bindLong(10, entity.getFavFlag() ? 1L: 0L);
 
        Long folderId = entity.getFolderId();
        if (folderId != null) {
            stmt.bindLong(11, folderId);
        }
 
        String suffix = entity.getSuffix();
        if (suffix != null) {
            stmt.bindString(12, suffix);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, VideoVo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String symbolName = entity.getSymbolName();
        if (symbolName != null) {
            stmt.bindString(3, symbolName);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(4, path);
        }
 
        String width = entity.getWidth();
        if (width != null) {
            stmt.bindString(5, width);
        }
 
        String height = entity.getHeight();
        if (height != null) {
            stmt.bindString(6, height);
        }
 
        String size = entity.getSize();
        if (size != null) {
            stmt.bindString(7, size);
        }
 
        String duration = entity.getDuration();
        if (duration != null) {
            stmt.bindString(8, duration);
        }
 
        String des = entity.getDes();
        if (des != null) {
            stmt.bindString(9, des);
        }
        stmt.bindLong(10, entity.getFavFlag() ? 1L: 0L);
 
        Long folderId = entity.getFolderId();
        if (folderId != null) {
            stmt.bindLong(11, folderId);
        }
 
        String suffix = entity.getSuffix();
        if (suffix != null) {
            stmt.bindString(12, suffix);
        }
    }

    @Override
    protected final void attachEntity(VideoVo entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public VideoVo readEntity(Cursor cursor, int offset) {
        VideoVo entity = new VideoVo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // symbolName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // path
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // width
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // height
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // size
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // duration
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // des
            cursor.getShort(offset + 9) != 0, // favFlag
            cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10), // folderId
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11) // suffix
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, VideoVo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSymbolName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setWidth(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setHeight(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSize(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDuration(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDes(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setFavFlag(cursor.getShort(offset + 9) != 0);
        entity.setFolderId(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
        entity.setSuffix(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(VideoVo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(VideoVo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(VideoVo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getFolderVoDao().getAllColumns());
            builder.append(" FROM table_video T");
            builder.append(" LEFT JOIN table_folder T0 ON T.\"FOLDER_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected VideoVo loadCurrentDeep(Cursor cursor, boolean lock) {
        VideoVo entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        FolderVo folderVo = loadCurrentOther(daoSession.getFolderVoDao(), cursor, offset);
        entity.setFolderVo(folderVo);

        return entity;    
    }

    public VideoVo loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<VideoVo> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<VideoVo> list = new ArrayList<VideoVo>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<VideoVo> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<VideoVo> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
