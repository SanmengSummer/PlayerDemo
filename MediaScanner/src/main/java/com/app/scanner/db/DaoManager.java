package com.app.scanner.db;

import android.util.Log;
import com.app.scanner.BuildConfig;
import com.app.scanner.CarApp;
import org.greenrobot.greendao.query.QueryBuilder;

import static com.app.scanner.db.Constants.DB_NAME;

/**********************************************
 * Filename： DaoManager
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1        wangyi   1) …
 ***********************************************/
public class DaoManager {

    //多线程中要被共享的使用volatile关键字修饰
    private volatile static DaoManager manager = new DaoManager();
    private DaoMaster mDaoMaster;
    private DaoMaster.DevOpenHelper mHelper;
    private DaoSession mDaoSession;

    /**
     * 单例模式获得操作数据库对象
     */
    public static DaoManager getInstance() {
        return manager;
    }

    private DaoManager() {
        setDebug();
    }

    private DaoMaster getDaoMaster() {
        if (mDaoMaster == null) {
            boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
            if (!sdExist) {//如果不存在,
                Log.e(Constants.LOG_TAG, "SD卡不存在，请加载SD卡");
                throw new RuntimeException("SD卡不存在，请加载SD卡");
            }
            String dbDir = android.os.Environment.getExternalStorageDirectory().toString()+
                    "/scannerdb";
            DatabaseContext context = new DatabaseContext(CarApp.contextApp, dbDir);
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
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
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
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
}
