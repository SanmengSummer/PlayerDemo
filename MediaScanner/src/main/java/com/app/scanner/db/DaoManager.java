package com.app.scanner.db;

import com.app.scanner.CarApp;
import com.app.scanner.util.Constants;
import com.app.scanner.vo.DaoMaster;
import com.app.scanner.vo.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import static com.app.scanner.util.Constants.DB_FOLDER;

public class DaoManager {

    private volatile static DaoManager manager = new DaoManager();
    private DaoMaster mDaoMaster;
    private DaoMaster.DevOpenHelper mHelper;
    private DaoSession mDaoSession;

    public static DaoManager getInstance() {
        return manager;
    }

    private String dbPath;

    public String getDbPath() {
        return dbPath;
    }

    private DaoManager() {
        setDebug();
    }

    private DaoMaster getDaoMaster() {
        if (mDaoMaster == null) {
            boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
            if (!sdExist) {
                throw new RuntimeException("sd not exit");
            }
            String dbDir = android.os.Environment.getExternalStorageDirectory().toString() + "/" + DB_FOLDER;
            dbPath = dbDir;
            DatabaseContext context = new DatabaseContext(CarApp.contextApp, dbDir);
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Constants.DB_NAME, null);
            mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     */
    public DaoSession getDaoSession() {
        if (mDaoSession == null) {
            if (mDaoMaster == null) {
                mDaoMaster = getDaoMaster();
                setDebug();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug() {
        QueryBuilder.LOG_SQL = false;
        QueryBuilder.LOG_VALUES = false;
    }

    /**
     * 关闭所有的操作，数据库开启后，使完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    private void closeHelper() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }

    private void closeDaoSession() {
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

    public void deleteAllData() {
        DaoManager.getInstance().getDaoSession().getFolderVoDao().deleteAll();
        DaoManager.getInstance().getDaoSession().getAlbumVoDao().deleteAll();
        DaoManager.getInstance().getDaoSession().getAudioVoDao().deleteAll();
        DaoManager.getInstance().getDaoSession().getGenreVoDao().deleteAll();
        DaoManager.getInstance().getDaoSession().getRecordVoDao().deleteAll();
        DaoManager.getInstance().getDaoSession().getSingerVoDao().deleteAll();
        DaoManager.getInstance().getDaoSession().getVideoVoDao().deleteAll();
        DaoManager.getInstance().getDaoSession().getUsbDeviceVoDao().deleteAll();
    }
}
