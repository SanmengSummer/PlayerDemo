package com.app.scanner.db;

import com.app.scanner.util.Utils;
import com.app.scanner.vo.AlbumVo;
import com.app.scanner.vo.AlbumVoDao;
import com.app.scanner.vo.AudioVo;
import com.app.scanner.vo.AudioVoDao;
import com.app.scanner.vo.FolderVo;
import com.app.scanner.vo.FolderVoDao;
import com.app.scanner.vo.GenreVo;
import com.app.scanner.vo.GenreVoDao;
import com.app.scanner.vo.SingerVo;
import com.app.scanner.vo.SingerVoDao;

import org.greenrobot.greendao.AbstractDao;

import java.util.List;

import static com.app.scanner.util.Utils.getSymbolName;

public class AudioDbHelper extends DbOperationHelper<AudioVo> {
    private AbstractDao albumDao;
    private AbstractDao singerDao;
    private AbstractDao folderDao;
    private AbstractDao genreDao;

    public AudioDbHelper(Class<AudioVo> pEntityClass, AbstractDao<AudioVo, Long> pEntityDao) {
        super(pEntityClass, pEntityDao);
        albumDao = DaoManager.getInstance().getDaoSession().getAlbumVoDao();
        singerDao = DaoManager.getInstance().getDaoSession().getSingerVoDao();
        folderDao = DaoManager.getInstance().getDaoSession().getFolderVoDao();
        genreDao = DaoManager.getInstance().getDaoSession().getGenreVoDao();

    }

    @Override
    public boolean insert(AudioVo pEntity) {
        if (!Utils.isEmpty(pEntity.getAlbum())) {
            List<AlbumVo> list = queryByKey(albumDao, AlbumVoDao.Properties.Name, pEntity.getAlbum());
            if (list != null && list.size() > 0) {
                AlbumVo albumVo = list.get(0);
                pEntity.setAlbumId(albumVo.getId());
            } else {
                AlbumVo albumVo = new AlbumVo();
                albumVo.setName(pEntity.getAlbum());
                albumVo.setSymbolName(getSymbolName(pEntity.getAlbum()));
                long albumId = albumDao.insert(albumVo);
                pEntity.setAlbumId(albumId);
            }
        }
        if (!Utils.isEmpty(pEntity.getSinger())) {
            List<SingerVo> list = queryByKey(singerDao, SingerVoDao.Properties.Name, pEntity.getSinger());
            if (list != null && list.size() > 0) {
                SingerVo singerVo = list.get(0);
                pEntity.setSingerId(singerVo.getId());
            } else {
                SingerVo singerVo = new SingerVo();
                singerVo.setName(pEntity.getSinger());
                singerVo.setSymbolName(getSymbolName(pEntity.getSinger()));
                long singerId = singerDao.insert(singerVo);
                pEntity.setSingerId(singerId);
            }
        }

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
        if (!Utils.isEmpty(pEntity.getGenre())) {
            List<GenreVo> list = queryByKey(genreDao, GenreVoDao.Properties.Name, pEntity.getGenre());
            if (list != null && list.size() > 0) {
                GenreVo genreVo = list.get(0);
                pEntity.setGenreId(genreVo.getId());
            } else {
                GenreVo genreVo = new GenreVo();
                genreVo.setName(pEntity.getGenre());
                genreVo.setSymbolName(getSymbolName(pEntity.getGenre()));
                long genreId = genreDao.insert(genreVo);
                pEntity.setGenreId(genreId);
            }
        }
        AudioVo tempVo = queryByKey(pEntity.getPath());
        if (tempVo != null) {
            pEntity.setId(tempVo.getId());
            return super.update(pEntity);
        } else {
            return super.insert(pEntity);
        }

    }

    @Override
    public AudioVo queryByKey(String key) {
        List<AudioVo> list = entityDao.queryBuilder().where(AudioVoDao.Properties.Path.eq(key)).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
