package com.app.scanner.db;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

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
public class DbOperationHelper<T> implements INormalOp<T>{
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

    @Override
    public boolean deleteById(long id) {
        try {
            //按照id删除
            mDaoSession.delete(id);
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

    @Override
    public T queryByKey(String key) {
        return null;
    }

    public List queryByKey(AbstractDao dao, Property property, String key) {
        List list = dao.queryBuilder().where(property.eq(key)).list();
        if (list != null && list.size() > 0) {
            return list;
        }
        return null;
    }
}
