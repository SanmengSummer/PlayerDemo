package com.app.scanner.db;

import android.text.TextUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;

import java.util.List;

import static com.app.scanner.util.LogUtils.getSymbolName;

public class VideoDbHelper extends DbOperationHelper<VideoVo> {
    AbstractDao dao;
    AbstractDao folderDao;

    public VideoDbHelper(Class<VideoVo> pEntityClass, AbstractDao<VideoVo, Long> pEntityDao) {
        super(pEntityClass, pEntityDao);
        dao = pEntityDao;
        folderDao = DaoManager.getInstance().getDaoSession().getFolderVoDao();
    }

    @Override
    public boolean insert(VideoVo pEntity) {

        if (!TextUtils.isEmpty(pEntity.getFolder())) {
            List<FolderVo> list = queryByKey(folderDao, FolderVoDao.Properties.Path, pEntity.getFolder());
            if (list != null && list.size() > 0) {
                FolderVo folderVo = list.get(0);
                pEntity.setFolderId(folderVo.getId());
            } else {
                FolderVo folderVo = new FolderVo();
                folderVo.setName("root");
                folderVo.setSymbolName(getSymbolName("root"));
                folderVo.setPath(pEntity.getFolder());
                long folderId = folderDao.insert(folderVo);
                pEntity.setFolderId(folderId);
            }
        }

        VideoVo tempVo = queryByKey(pEntity.getPath());
        if (tempVo != null) {
            pEntity.setId(tempVo.getId());
            return super.update(pEntity);
        } else {
            return super.insert(pEntity);
        }

    }

    @Override
    public VideoVo queryByKey(String key) {
        List<VideoVo> list = dao.queryBuilder().where(VideoVoDao.Properties.Path.eq(key)).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
