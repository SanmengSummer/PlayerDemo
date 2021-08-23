package com.app.scanner.db;

import android.text.TextUtils;

import org.greenrobot.greendao.AbstractDao;

import java.util.List;

public class FolderDbHelper extends DbOperationHelper<FolderVo> {
    AbstractDao dao;

    public FolderDbHelper(Class<FolderVo> pEntityClass, AbstractDao<FolderVo, Long> pEntityDao) {
        super(pEntityClass, pEntityDao);
        dao = pEntityDao;
    }

    @Override
    public boolean insert(FolderVo pEntity) {
        if (!TextUtils.isEmpty(pEntity.getParentPath())) {
            FolderVo parentFolder = queryByKey(pEntity.getParentPath());
            if (parentFolder != null) {
                pEntity.setParentId(parentFolder.getId() + "");
            }
        }
        FolderVo tempVo = queryByKey(pEntity.getPath());
        if (tempVo != null) {
            pEntity.setId(tempVo.getId());
            return super.update(pEntity);
        } else {
            return super.insert(pEntity);
        }

    }

    @Override
    public FolderVo queryByKey(String key) {
        List<FolderVo> list = dao.queryBuilder().where(FolderVoDao.Properties.Path.eq(key)).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
