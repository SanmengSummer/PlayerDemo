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

import com.landmark.media.db.table.AudioVo;

import com.landmark.media.db.table.RecordVo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "table_record".
*/
public class RecordVoDao extends AbstractDao<RecordVo, Long> {

    public static final String TABLENAME = "table_record";

    /**
     * Properties of entity RecordVo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MediaId = new Property(1, Long.class, "mediaId", false, "MEDIA_ID");
        public final static Property EndDuration = new Property(2, String.class, "endDuration", false, "END_DURATION");
        public final static Property PlayTime = new Property(3, String.class, "playTime", false, "PLAY_TIME");
    }

    private DaoSession daoSession;


    public RecordVoDao(DaoConfig config) {
        super(config);
    }
    
    public RecordVoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"table_record\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"MEDIA_ID\" INTEGER," + // 1: mediaId
                "\"END_DURATION\" TEXT," + // 2: endDuration
                "\"PLAY_TIME\" TEXT);"); // 3: playTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"table_record\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, RecordVo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long mediaId = entity.getMediaId();
        if (mediaId != null) {
            stmt.bindLong(2, mediaId);
        }
 
        String endDuration = entity.getEndDuration();
        if (endDuration != null) {
            stmt.bindString(3, endDuration);
        }
 
        String playTime = entity.getPlayTime();
        if (playTime != null) {
            stmt.bindString(4, playTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, RecordVo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long mediaId = entity.getMediaId();
        if (mediaId != null) {
            stmt.bindLong(2, mediaId);
        }
 
        String endDuration = entity.getEndDuration();
        if (endDuration != null) {
            stmt.bindString(3, endDuration);
        }
 
        String playTime = entity.getPlayTime();
        if (playTime != null) {
            stmt.bindString(4, playTime);
        }
    }

    @Override
    protected final void attachEntity(RecordVo entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public RecordVo readEntity(Cursor cursor, int offset) {
        RecordVo entity = new RecordVo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // mediaId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // endDuration
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // playTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, RecordVo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMediaId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setEndDuration(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPlayTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(RecordVo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(RecordVo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(RecordVo entity) {
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
            SqlUtils.appendColumns(builder, "T0", daoSession.getAudioVoDao().getAllColumns());
            builder.append(" FROM table_record T");
            builder.append(" LEFT JOIN table_audio T0 ON T.\"MEDIA_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected RecordVo loadCurrentDeep(Cursor cursor, boolean lock) {
        RecordVo entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        AudioVo audioVo = loadCurrentOther(daoSession.getAudioVoDao(), cursor, offset);
        entity.setAudioVo(audioVo);

        return entity;    
    }

    public RecordVo loadDeep(Long key) {
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
    public List<RecordVo> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<RecordVo> list = new ArrayList<RecordVo>(count);
        
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
    
    protected List<RecordVo> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<RecordVo> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}