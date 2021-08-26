package com.app.scanner.db;

import com.app.scanner.util.Utils;
import com.app.scanner.vo.FolderVo;
import com.app.scanner.vo.FolderVoDao;
import com.app.scanner.vo.VideoVo;
import com.app.scanner.vo.VideoVoDao;

import org.greenrobot.greendao.AbstractDao;

import java.util.List;

import static com.app.scanner.util.Utils.getSymbolName;

public class VideoDbHelper extends DbOperationHelper<VideoVo> {
    private AbstractDao folderDao;

    public VideoDbHelper(Class<VideoVo> pEntityClass, AbstractDao<VideoVo, Long> pEntityDao) {
        super(pEntityClass, pEntityDao);
        folderDao = DaoManager.getInstance().getDaoSession().getFolderVoDao();
    }

    @Override
    public boolean insert(VideoVo pEntity) {

        if (!Utils.isEmpty(pEntity.getFolder())) {
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
        List<VideoVo> list = entityDao.queryBuilder().where(VideoVoDao.Properties.Path.eq(key)).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
