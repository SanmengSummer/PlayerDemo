package com.app.scanner.db;

import com.app.scanner.util.Utils;
import com.app.scanner.vo.FolderVo;
import com.app.scanner.vo.FolderVoDao;

import org.greenrobot.greendao.AbstractDao;

import java.util.List;

public class FolderDbHelper extends DbOperationHelper<FolderVo> {

    public FolderDbHelper(Class<FolderVo> pEntityClass, AbstractDao<FolderVo, Long> pEntityDao) {
        super(pEntityClass, pEntityDao);
    }

    @Override
    public boolean insert(FolderVo pEntity) {
        if (!Utils.isEmpty(pEntity.getParentPath())) {
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
        List<FolderVo> list = entityDao.queryBuilder().where(FolderVoDao.Properties.Path.eq(key)).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
