package com.landmark.media.db;

import com.landmark.media.db.dao.AudioVoDao;
import com.landmark.media.db.dao.DaoSession;
import com.landmark.media.db.dao.RecordVoDao;
import com.landmark.media.db.table.AudioVo;
import com.landmark.media.db.table.RecordVo;
import com.landmark.media.interfaces.INormalOp;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**********************************************
 * Filename： CommonDaoUtils
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1        wangyi   1) …
 ***********************************************/
public class DbOperationHelper<T> implements INormalOp<T> {
    private DaoSession mDaoSession;
    private Class<T> entityClass;
    private AbstractDao<T, Long> entityDao;

    public DbOperationHelper(Class<T> pEntityClass, AbstractDao<T, Long> pEntityDao) {
        DaoManager mManager = DaoManager.getInstance();
        mDaoSession = mManager.getDaoSession();
        entityClass = pEntityClass;
        entityDao = pEntityDao;
    }

    /**
     * 插入记录，如果表未创建，先创建表
     */
    @Override
    public boolean insert(T pEntity) {
        return entityDao.insert(pEntity) != -1;
    }

    /**
     * 插入多条数据，开启事务
     */
    @Override
    public boolean insertMultiple(final List<T> pEntityList) {
        try {
            mDaoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T entity : pEntityList) {
                        mDaoSession.insertOrReplace(entity);
                    }
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMultiples(List<T> pEntityList) {
        try {
            if (pEntityList.get(0) instanceof AudioVo) {
                List<AudioVo> pEntityList1 = (List<AudioVo>) pEntityList;
                AudioVoDao audioVoDao = mDaoSession.getAudioVoDao();
                audioVoDao.updateInTx(pEntityList1);
            } else if (pEntityList.get(0) instanceof RecordVo) {
                List<RecordVo> pEntityList2 = (List<RecordVo>) pEntityList;
                RecordVoDao audioVoDao = mDaoSession.getRecordVoDao();
                audioVoDao.updateInTx(pEntityList2);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改一条数据
     */
    @Override
    public boolean update(T entity) {
        try {
            mDaoSession.update(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除单条记录
     */
    @Override
    public boolean delete(T entity) {
        try {
            //按照id删除
            mDaoSession.delete(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除所有记录
     */
    @Override
    public boolean deleteAll() {
        try {
            mDaoSession.deleteAll(entityClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询所有记录
     */
    @Override
    public List<T> queryAll() {
        return mDaoSession.loadAll(entityClass);
    }

    /**
     * 根据主键id查询记录
     */
    @Override
    public T queryById(long key) {
        return mDaoSession.load(entityClass, key);
    }

    public QueryBuilder<T> queryBuilder() {
        QueryBuilder<T> tQueryBuilder = mDaoSession.queryBuilder(entityClass);
        return tQueryBuilder;
    }

    public Database queryBuilders() {
        Database tQueryBuilder = mDaoSession.getDatabase();
        return tQueryBuilder;
    }


}
