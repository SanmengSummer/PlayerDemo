/**********************************************
 * Filename：
 * Author:   qiang.chen@landmark-phb.com
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.xx  2021/8/9 10  chenqiang   1) …
 ***********************************************/
package com.landmark.media.db.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.landmark.media.common.MetadataTypeValue;
import com.landmark.media.db.DaoManager;
import com.landmark.media.db.DbOperationHelper;
import com.landmark.media.db.dao.AlbumVoDao;
import com.landmark.media.db.dao.AudioVoDao;
import com.landmark.media.db.dao.DaoSession;
import com.landmark.media.db.dao.FolderVoDao;
import com.landmark.media.db.dao.GenreVoDao;
import com.landmark.media.db.dao.RecordVoDao;
import com.landmark.media.db.dao.SingerVoDao;
import com.landmark.media.db.dao.VideoVoDao;
import com.landmark.media.db.table.AlbumVo;
import com.landmark.media.db.table.AudioVo;
import com.landmark.media.db.table.FolderVo;
import com.landmark.media.db.table.GenreVo;
import com.landmark.media.db.table.RecordVo;
import com.landmark.media.db.table.SingerVo;
import com.landmark.media.db.table.VideoVo;
import com.landmark.media.model.MediaData;
import com.landmark.media.model.MediaDataModel;
import com.landmark.media.model.TypeModel;
import com.landmark.media.utils.LogUtils;
import com.landmark.media.utils.SortUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MusicProvider {
    private static MusicProvider sInstance;
    private final String TAG = "MusicProvider";
    private SQLiteDatabase mSqLiteDatabase;
    private Context mContext;
    private DaoSession mDaoSession;
    private DbOperationHelper<AlbumVo> mAlbumHelper;
    private DbOperationHelper<AudioVo> mAudioHelper;
    private DbOperationHelper<FolderVo> mFolderHelper;
    private DbOperationHelper<RecordVo> mRecordHelper;
    private DbOperationHelper<SingerVo> mSingerHelper;
    private DbOperationHelper<VideoVo> mVideoHelper;
    private DbOperationHelper<GenreVo> mGenreVoHelper;

    private List<MediaDataModel> mSearchAllData = new ArrayList<>();

    public static synchronized MusicProvider getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MusicProvider(context);
        }
        return sInstance;
    }

    public MusicProvider(Context context) {
        mContext = context;
    }

    /**
     * load database
     */
    public void load() {
        mDaoSession = DaoManager.getInstance().getDaoSession();
        mAlbumHelper = new DbOperationHelper<>(AlbumVo.class, mDaoSession.getAlbumVoDao());
        mAudioHelper = new DbOperationHelper<>(AudioVo.class, mDaoSession.getAudioVoDao());
        mFolderHelper = new DbOperationHelper<>(FolderVo.class, mDaoSession.getFolderVoDao());
        mRecordHelper = new DbOperationHelper<>(RecordVo.class, mDaoSession.getRecordVoDao());
        mSingerHelper = new DbOperationHelper<>(SingerVo.class, mDaoSession.getSingerVoDao());
        mVideoHelper = new DbOperationHelper<>(VideoVo.class, mDaoSession.getVideoVoDao());
        mGenreVoHelper = new DbOperationHelper<>(GenreVo.class, mDaoSession.getGenreVoDao());
    }

    public List<MediaDataModel> getAllDataList(TypeModel typeModel, MediaData mediaData) {
        LogUtils.debug(TAG, " getAllDataList :" + typeModel.getCategory());
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getAllDataList page: " + page + " size: " + size);
        int offset = page * size;
        List<MediaDataModel> collect = new ArrayList<>();
        long count = 0l;
        if (typeModel.getFormat().equals(MediaIDHelper.TYPE_1)) {
            List<AudioVo> limit = mAudioHelper.queryBuilder().offset(offset).limit(size)
                    .orderRaw(getRawOrderSql()).list();
            collect.addAll(convertData(limit));
            count = mAudioHelper.queryBuilder().count();
        } else {
            MediaDataModel[] value = new MediaDataModel[1];
            List<VideoVo> limit = mVideoHelper.queryBuilder().offset(offset).limit(size)
                    .orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> collect1 = limit.stream().map(new Function<VideoVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(VideoVo videoVo) {
                    value[0] = new MediaDataModel();
                    value[0].setName(videoVo.getName());
                    value[0].setVideoId(videoVo.getId());
                    value[0].setVideoVo(videoVo);
                    value[0].setItemType(MetadataTypeValue.TYPE_VIDEO.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());
            collect.addAll(collect1);
            count = mVideoHelper.queryBuilder().count();
        }
        setTotalData(mediaData, size, count);
        return collect;
    }

    public List<MediaDataModel> getAlbumDataList(TypeModel typeModel, MediaData mediaData) {
        List<MediaDataModel> list = new ArrayList<>();
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getAllDataList page: " + page + " size: " + size);
        int offset = page * size;
        LogUtils.debug(TAG, " getAlbumDataList: " + typeModel.getCategory() + " mediaType: "
                + typeModel.getFormat());
        final MediaDataModel[] value = new MediaDataModel[1];
        if (typeModel.getCategory_name().equals(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM)) {
            LogUtils.debug(TAG, " getAlbumDataList All Album");
            List<AlbumVo> albumVos = mAlbumHelper.queryBuilder().offset(offset).limit(size)
                    .orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> collect = albumVos.stream().map(albumVo -> {
                value[0] = new MediaDataModel();
                value[0].setAlbumVo(albumVo);
                value[0].setAlbumId(albumVo.getId());
                value[0].setName(albumVo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_ALBUM.getType());
                return value[0];
            }).collect(Collectors.toList());
            list.addAll(collect);
            long count = mAlbumHelper.queryBuilder().offset(offset).limit(size).count();
            setTotalData(mediaData, size, count);
        } else {
            LogUtils.debug(TAG, " getAlbumDataList All Album-> name");
            List<AudioVo> limit = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.AlbumId.eq(typeModel.getCategory_name()))
                    .orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> collect = convertData(limit);
            list.addAll(collect);
            long count = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.AlbumId.eq(typeModel.getCategory_name())).count();
            setTotalData(mediaData, size, count);
        }
        return list;
    }

    private void setTotalData(MediaData mediaData, int size, long count) {
        if (count > 0) {
            long l = count / size;
            long l1 = count % size;
            if (l1 > 0) {
                l = l + 1;
            }
            mediaData.setTotalNum((int) count);
            mediaData.setTotalPage((int) l);
        }
    }

    public List<MediaDataModel> getArtistDataList(TypeModel typeModel, MediaData mediaData) {
        List<MediaDataModel> list = new ArrayList<>();
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getAllDataList page: " + page + " size: " + size);
        int offset = page * size;

        LogUtils.debug(TAG, " getArtistDataList: " + typeModel.getCategory() + " mediaType: "
                + typeModel.getFormat());
        final MediaDataModel[] value = new MediaDataModel[1];
        if (typeModel.getCategory_name().equals(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST)) {
            LogUtils.debug(TAG, " getArtistDataList All Artist");
            List<SingerVo> singerVos = mSingerHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> collect = singerVos.stream().map(singervo -> {
                value[0] = new MediaDataModel();
                value[0].setSingerVo(singervo);
                value[0].setSingerId(singervo.getId());
                value[0].setName(singervo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_ARTIST.getType());
                return value[0];
            }).collect(Collectors.toList());
            list.addAll(collect);
            long count = mSingerHelper.queryBuilder().offset(offset).limit(size).count();
            setTotalData(mediaData, size, count);
        } else {
            LogUtils.debug(TAG, " getArtistDataList All Artist-> name");
            List<AudioVo> limit = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.SingerId.eq(typeModel.getCategory_name())).orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> collect = convertData(limit);
            list.addAll(collect);
            long count = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.SingerId.eq(typeModel.getCategory_name())).count();
            setTotalData(mediaData, size, count);
        }
        return list;
    }

    private List<MediaDataModel> convertData(List<AudioVo> data) {
        MediaDataModel[] value = new MediaDataModel[1];
        return data.stream().map(result -> {
            value[0] = new MediaDataModel();
            value[0].setId(result.getId());
            value[0].setName(result.getName());
            value[0].setPath(result.getPath());
            value[0].setSize(result.getSize());
            value[0].setYear(result.getYear());
            value[0].setGenreId(result.getGenreId());
            value[0].setGenreVo(result.getGenreVo());
            value[0].setFavFlag(result.getFavFlag());
            value[0].setAlbumId(result.getAlbumId());
            value[0].setAlbumVo(result.getAlbumVo());
            value[0].setFolderId(result.getFolderId());
            value[0].setFolderVo(result.getFolderVo());
            value[0].setSingerVo(result.getSingerVo());
            value[0].setSingerId(result.getSingerId());
            if (result.getId() == null) {
                if (result.getFolderVo() != null) {
                    value[0].setItemType(MetadataTypeValue.TYPE_FOLDER.getType());
                } else if (result.getAlbumVo() != null) {
                    value[0].setItemType(MetadataTypeValue.TYPE_ALBUM.getType());
                } else if (result.getGenreVo() != null) {
                    value[0].setItemType(MetadataTypeValue.TYPE_GENRE.getType());
                } else if (result.getSingerVo() != null) {
                    value[0].setItemType(MetadataTypeValue.TYPE_ARTIST.getType());
                }
            } else {
                value[0].setItemType(MetadataTypeValue.TYPE_MUSIC.getType());
            }
            return value[0];
        }).collect(Collectors.toList());
    }

    public List<MediaDataModel> getGenreDataList(TypeModel typeModel, MediaData mediaData) {
        List<MediaDataModel> list = new ArrayList<>();
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getGenreDataList page: " + page + " size: " + size);
        int offset = page * size;
        final MediaDataModel[] value = new MediaDataModel[1];
        if (typeModel.getCategory_name().equals(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE)) {
            LogUtils.debug(TAG, " getGenreDataList All ");
            List<GenreVo> singerVos = mGenreVoHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> collect = singerVos.stream().map(singervo -> {
                value[0] = new MediaDataModel();
                value[0].setGenreVo(singervo);
                value[0].setGenreId(singervo.getId());
                value[0].setName(singervo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_GENRE.getType());
                return value[0];
            }).collect(Collectors.toList());
            list.addAll(collect);
            long count = mGenreVoHelper.queryBuilder().offset(offset).limit(size).count();
            setTotalData(mediaData, size, count);
        } else {
            LogUtils.debug(TAG, " getGenreDataList All Album-> name");
            List<AudioVo> limit = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.GenreId.eq(typeModel.getCategory_name())).orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> collect = convertData(limit);
            list.addAll(collect);
            long count = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.GenreId.eq(typeModel.getCategory_name())).count();
            setTotalData(mediaData, size, count);
        }
        return list;
    }

    public List<MediaDataModel> getFolderDataList(TypeModel typeModel, MediaData mediaData) {
        List<MediaDataModel> list = new ArrayList<>();
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getFolderDataList page: " + page + " size: " + size);
        int offset = page * size;
        MediaDataModel[] value = new MediaDataModel[1];
        if (typeModel.getCategory_name().equals(MediaIDHelper.MEDIA_ID_MUSICS_BY_FOLDER)) {
            List<FolderVo> folderVo = mFolderHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> collect = folderVo.stream().map(new Function<FolderVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(FolderVo folderVo) {
                    value[0] = new MediaDataModel();
                    value[0].setFolderVo(folderVo);
                    value[0].setFolderId(folderVo.getId());
                    value[0].setName(folderVo.getName());
                    value[0].setItemType(MetadataTypeValue.TYPE_FOLDER.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());
            list.addAll(collect);
            long count = mFolderHelper.queryBuilder().offset(offset).limit(size).count();
            setTotalData(mediaData, size, count);
        } else {
            LogUtils.debug(TAG, " getFolderDataList getCategory_name: " + typeModel.getCategory_name());
            List<AudioVo> data = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.FolderId.eq(typeModel.getCategory_name())).orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> mediaDataModels = convertData(data);
            list.addAll(mediaDataModels);
            List<FolderVo> list1 = mFolderHelper.queryBuilder().offset(offset).limit(size).where(
                    FolderVoDao.Properties.ParentId.eq(data.get(0).getFolderVo().getId())).orderRaw(getRawOrderSql()).list();
            List<MediaDataModel> folderVo = list1.stream().map(new Function<FolderVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(FolderVo folderVo) {
                    value[0] = new MediaDataModel();
                    value[0].setFolderVo(folderVo);
                    value[0].setFolderId(folderVo.getId());
                    value[0].setName(folderVo.getName());
                    value[0].setItemType(MetadataTypeValue.TYPE_FOLDER.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());
            list.addAll(0, folderVo);
            long count = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.FolderId.eq(typeModel.getCategory_name())).count();
            if (count > 0) {
                long l = count / size;
                long l1 = count % size;
                if (l1 > 0) {
                    l = l + 1;
                }
                mediaData.setTotalNum((int) count + list1.size());
                mediaData.setTotalPage((int) l);
            }
        }
        return list;
    }

    TypeModel mTypeModel;

    public List<MediaDataModel> getSearchAll(TypeModel typeModel, MediaData mediaData) {
        LogUtils.debug(TAG, " getSearchAll mSearchAllData: " + mSearchAllData.size() + " name: " + typeModel.getCategory_name());
//        if (mSearchAllData.size() == 0 && (
//                ((mTypeModel == null) || !mTypeModel.getCategory_name().equals(typeModel.getCategory_name()))
//        )) {
        mTypeModel = typeModel;
        mSearchAllData.clear();
        MediaDataModel[] value = new MediaDataModel[1];
        List<FolderVo> list5 = mFolderHelper.queryBuilder()
                .whereOr(FolderVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%")
                        , FolderVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%")).list();
        List<MediaDataModel> collect3 = list5.stream().map(new Function<FolderVo, MediaDataModel>() {
            @Override
            public MediaDataModel apply(FolderVo foldervo) {
                value[0] = new MediaDataModel();
                value[0].setFolderVo(foldervo);
                value[0].setFolderId(foldervo.getId());
                value[0].setName(foldervo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_FOLDER.getType());
                return value[0];
            }
        }).collect(Collectors.toList());
        mSearchAllData.addAll(collect3);

        List<AudioVo> list1 = mAudioHelper.queryBuilder()
                .whereOr(AudioVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%")
                        , AudioVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%")).list();
        List<MediaDataModel> mediaDataModels = convertData(list1);
        mSearchAllData.addAll(mediaDataModels);

        List<AlbumVo> list2 = mAlbumHelper.queryBuilder()
                .whereOr(AlbumVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%")
                        , AlbumVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%")).list();
        List<MediaDataModel> collect = list2.stream().map(new Function<AlbumVo, MediaDataModel>() {
            @Override
            public MediaDataModel apply(AlbumVo albumVo) {
                value[0] = new MediaDataModel();
                value[0].setAlbumVo(albumVo);
                value[0].setAlbumId(albumVo.getId());
                value[0].setName(albumVo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_ALBUM.getType());
                return value[0];
            }
        }).collect(Collectors.toList());
        mSearchAllData.addAll(collect);

        List<GenreVo> list3 = mGenreVoHelper.queryBuilder()
                .whereOr(GenreVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%")
                        , GenreVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%")).list();
        List<MediaDataModel> collect1 = list3.stream().map(new Function<GenreVo, MediaDataModel>() {
            @Override
            public MediaDataModel apply(GenreVo genreVo) {
                value[0] = new MediaDataModel();
                value[0].setGenreVo(genreVo);
                value[0].setGenreId(genreVo.getId());
                value[0].setName(genreVo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_GENRE.getType());
                return value[0];
            }
        }).collect(Collectors.toList());
        mSearchAllData.addAll(collect1);

        List<SingerVo> list4 = mSingerHelper.queryBuilder()
                .whereOr(SingerVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%")
                        , SingerVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%")).list();
        List<MediaDataModel> collect2 = list4.stream().map(new Function<SingerVo, MediaDataModel>() {
            @Override
            public MediaDataModel apply(SingerVo singerVo) {
                value[0] = new MediaDataModel();
                value[0].setSingerVo(singerVo);
                value[0].setSingerId(singerVo.getId());
                value[0].setName(singerVo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_ARTIST.getType());
                return value[0];
            }
        }).collect(Collectors.toList());
        mSearchAllData.addAll(collect2);


        List<VideoVo> list6 = mVideoHelper.queryBuilder()
                .whereOr(VideoVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%")
                        , VideoVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%")).list();
        List<MediaDataModel> collectVideo = list6.stream().map(new Function<VideoVo, MediaDataModel>() {
            @Override
            public MediaDataModel apply(VideoVo videovo) {
                value[0] = new MediaDataModel();
                value[0].setVideoVo(videovo);
                value[0].setVideoId(videovo.getId());
                value[0].setName(videovo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_VIDEO.getType());
                return value[0];
            }
        }).collect(Collectors.toList());
        mSearchAllData.addAll(collectVideo);
//        }
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        int offset = page * size;
        int count = mSearchAllData.size();
        LogUtils.debug(TAG, " getSearchAll page: " + page + " size: " + (offset + 1) + " count: " + count);
        setTotalData(mediaData, size, count);
        LogUtils.debug(TAG, " getSearchAll page: "
                + page + " offset: " + (offset + 1) + " count: " + count + " size: " + size);
        int fromIndex = offset;
        int toIndex = size + fromIndex;
        if (toIndex >= count) {
            toIndex = count;
        }
        String[][] str = SortUtils.SortByIndex(mSearchAllData, typeModel.getCategory_name());
        SortUtils.SortByLength(mSearchAllData, str, mSearchAllData.size());
        List<MediaDataModel> mediaDataModel = mSearchAllData.subList(fromIndex, toIndex);
        return mediaDataModel;
    }

    public Collection<? extends MediaDataModel> getSearchAlbum(TypeModel typeModel,
                                                               boolean isList, MediaData mediaData) {
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getFolderDataList page: " + page + " size: " + size);
        int offset = page * size;
        MediaDataModel[] value = new MediaDataModel[1];
        List<MediaDataModel> mSearchAlbumData = new ArrayList<>();
        String category_name = typeModel.getCategory_name();
        if (isList) {
            //根据查到的专辑 来查询具体专辑下的歌曲
            List<AudioVo> audioVos = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.AlbumId.eq(category_name)).orderRaw(getRawOrderSql()).list();
            mSearchAlbumData.addAll(convertData(audioVos));
            long count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.AlbumId.eq(category_name)).count();
            setTotalData(mediaData, size, count);
        } else {
            //搜专辑名称的 模糊查找
            List<AlbumVo> list2 = mAlbumHelper.queryBuilder()
                    .whereOr(AlbumVoDao.Properties.Name.like("%" + category_name + "%")
                            , AlbumVoDao.Properties.SymbolName.like("%" + category_name + "%"))
                    .orderRaw(getSearchRawOrderSql(category_name)).offset(offset).limit(size).list();
            List<MediaDataModel> collect = list2.stream().map(new Function<AlbumVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(AlbumVo albumVo) {
                    value[0] = new MediaDataModel();
                    value[0].setAlbumVo(albumVo);
                    value[0].setAlbumId(albumVo.getId());
                    value[0].setName(albumVo.getName());
                    value[0].setItemType(MetadataTypeValue.TYPE_ALBUM.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());
            mSearchAlbumData.addAll(collect);
            long count = mAlbumHelper.queryBuilder()
                    .whereOr(AlbumVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%")
                            , AlbumVoDao.Properties.SymbolName.like("%" + category_name + "%")).count();
            setTotalData(mediaData, size, count);
        }
        return mSearchAlbumData;
    }

    public Collection<? extends MediaDataModel> getSearchArtist(TypeModel typeModel,
                                                                boolean isList, MediaData mediaData) {
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getFolderDataList page: " + page + " size: " + size);
        int offset = page * size;
        MediaDataModel[] value = new MediaDataModel[1];
        List<MediaDataModel> mSearchAlbumData = new ArrayList<>();
        String category_name = typeModel.getCategory_name();
        if (isList) {
            //根据查到的专辑 来查询具体专辑下的歌曲
            List<AudioVo> audioVos = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.SingerId.eq(category_name)).orderRaw(getRawOrderSql()).list();
            mSearchAlbumData.addAll(convertData(audioVos));
            long count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.SingerId.eq(category_name)).count();
            setTotalData(mediaData, size, count);
        } else {
            //搜专辑名称的 模糊查找
            List<SingerVo> list2 = mSingerHelper.queryBuilder()
                    .whereOr(SingerVoDao.Properties.Name.like("%" + category_name + "%")
                            , SingerVoDao.Properties.SymbolName.like("%" + category_name + "%"))
                    .offset(offset).limit(size).orderRaw(getSearchRawOrderSql(category_name)).list();
            LogUtils.debug(TAG, "==LogUtils== " + list2.size());
            List<MediaDataModel> collect = list2.stream().map(new Function<SingerVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(SingerVo singervo) {
                    value[0] = new MediaDataModel();
                    value[0].setSingerVo(singervo);
                    value[0].setSingerId(singervo.getId());
                    value[0].setName(singervo.getName());
                    value[0].setItemType(MetadataTypeValue.TYPE_ARTIST.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());
            mSearchAlbumData.addAll(collect);
            long count = mSingerHelper.queryBuilder()
                    .whereOr(SingerVoDao.Properties.Name.like("%" + category_name + "%")
                            , SingerVoDao.Properties.SymbolName.like("%" + category_name + "%")).count();
            setTotalData(mediaData, size, count);
        }
        return mSearchAlbumData;
    }

    public Collection<? extends MediaDataModel> getSearchGenre(TypeModel typeModel,
                                                               boolean isList, MediaData mediaData) {
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getFolderDataList page: " + page + " size: " + size);
        int offset = page * size;
        MediaDataModel[] value = new MediaDataModel[1];
        List<MediaDataModel> mSearchAlbumData = new ArrayList<>();
        String category_name = typeModel.getCategory_name();
        if (isList) {
            //根据查到的专辑 来查询具体专辑下的歌曲
            List<AudioVo> audioVos = mAudioHelper.queryBuilder().offset(offset).limit(size).where(
                    AudioVoDao.Properties.GenreId.eq(category_name)).orderRaw(getRawOrderSql()).list();
            mSearchAlbumData.addAll(convertData(audioVos));
            long count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.GenreId.eq(category_name)).count();
            setTotalData(mediaData, size, count);
        } else {
            //搜专辑名称的 模糊查找
            List<GenreVo> list2 = mGenreVoHelper.queryBuilder()
                    .whereOr(GenreVoDao.Properties.Name.like("%" + category_name + "%")
                            , GenreVoDao.Properties.SymbolName.like("%" + category_name + "%"))
                    .orderRaw(getSearchRawOrderSql(category_name)).offset(offset).limit(size).list();
            List<MediaDataModel> collect = list2.stream().map(new Function<GenreVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(GenreVo genrevo) {
                    value[0] = new MediaDataModel();
                    value[0].setGenreVo(genrevo);
                    value[0].setGenreId(genrevo.getId());
                    value[0].setName(genrevo.getName());
                    value[0].setItemType(MetadataTypeValue.TYPE_GENRE.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());
            mSearchAlbumData.addAll(collect);
            long count = mGenreVoHelper.queryBuilder()
                    .whereOr(GenreVoDao.Properties.Name.like("%" + category_name + "%")
                            , GenreVoDao.Properties.SymbolName.like("%" + category_name + "%")).count();
            setTotalData(mediaData, size, count);
        }
        return mSearchAlbumData;
    }

    public Collection<? extends MediaDataModel> getSearchFolder(TypeModel typeModel,
                                                                boolean isList, MediaData mediaData) {
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getFolderDataList page: " + page + " size: " + size);
        int offset = page * size;
        MediaDataModel[] value = new MediaDataModel[1];
        List<MediaDataModel> mSearchAlbumData = new ArrayList<>();
        String category_name = typeModel.getCategory_name();
        if (isList) {
            //根据查到的文件夹 来查询具体文件夹下的歌曲
            List<AudioVo> audioVos = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.FolderId.eq(category_name)).orderRaw(getRawOrderSql()).list();
            mSearchAlbumData.addAll(convertData(audioVos));
            List<VideoVo> list1 = mVideoHelper.queryBuilder().orderRaw(getRawOrderSql()).where(
                    VideoVoDao.Properties.FolderId.eq(category_name)).list();
            LogUtils.debug(TAG, " VideoVo list1: " + list1);
            List<MediaDataModel> collect1 = list1.stream().map(new Function<VideoVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(VideoVo videoVo) {
                    value[0] = new MediaDataModel();
                    value[0].setName(videoVo.getName());
                    value[0].setVideoId(videoVo.getId());
                    value[0].setVideoVo(videoVo);
                    value[0].setItemType(MetadataTypeValue.TYPE_VIDEO.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());
            mSearchAlbumData.addAll(collect1);


            long count = (long) mSearchAlbumData.size();
            setTotalData(mediaData, size, count);
            /*if (size > count || offset > count) {
                size = (int) count;
            }*/
            LogUtils.debug(TAG, " getSearchAll page: "
                    + page + " offset: " + (offset + 1) + " count: " + count + " size: " + size);
            int fromIndex = offset; //0 2 4
            int toIndex = size + fromIndex; //2 4
            if (toIndex >= count) {
                toIndex = (int) count;
            }
            List<MediaDataModel> mediaDataModel = mSearchAlbumData.subList(fromIndex, toIndex);
            return mediaDataModel;
        } else {
            //搜专辑名称的 模糊查找
            List<FolderVo> list2 = mFolderHelper.queryBuilder()
                    .whereOr(FolderVoDao.Properties.Name.like("%" + category_name + "%"),
                            FolderVoDao.Properties.SymbolName.like("%" + category_name + "%"))
                    .orderRaw(getSearchRawOrderSql(category_name)).offset(offset).limit(size).list();
            List<MediaDataModel> collect = list2.stream().map(foldervo -> {
                value[0] = new MediaDataModel();
                value[0].setFolderVo(foldervo);
                value[0].setFolderId(foldervo.getId());
                value[0].setName(foldervo.getName());
                value[0].setItemType(MetadataTypeValue.TYPE_FOLDER.getType());
                return value[0];
            }).collect(Collectors.toList());
            mSearchAlbumData.addAll(collect);
            long count = mFolderHelper.queryBuilder()
                    .whereOr(FolderVoDao.Properties.Name.like("%" + category_name + "%"),
                            FolderVoDao.Properties.SymbolName.like("%" + category_name + "%")).count();
            setTotalData(mediaData, size, count);
        }
        return mSearchAlbumData;
    }

    public Collection<? extends MediaDataModel> getSearchTitle(TypeModel typeModel,
                                                               boolean isList, MediaData mediaData) {

        //根据查到的专辑 来查询具体专辑下的歌曲
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getFolderDataList page: " + page + " size: " + size);
        int offset = page * size;
        List<MediaDataModel> mSearchAlbumData = new ArrayList<>();
        String category_name = typeModel.getCategory_name();
        if (isList) {
           /* List<AudioVo> audioVos = mAudioHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).where(
                    AudioVoDao.Properties.Id.eq(category_name)).list();
            mSearchAlbumData.addAll(convertData(audioVos));
            long count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.Id.eq(category_name)).count();
            setTotalData(mediaData, size, count);*/
        } else {
            List<AudioVo> list1 = mAudioHelper.queryBuilder()
                    .whereOr(AudioVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%"),
                            AudioVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%"))
                    .orderRaw(getSearchRawOrderSql(category_name)).offset(offset).limit(size).list();
            List<MediaDataModel> mediaDataModels = convertData(list1);
            mSearchAlbumData.addAll(mediaDataModels);
            long count = mAudioHelper.queryBuilder()
                    .whereOr(AudioVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%"),
                            AudioVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%")).count();
            setTotalData(mediaData, size, count);
        }
        return mSearchAlbumData;
    }

    public Collection<? extends MediaDataModel> getSearchVideo(TypeModel typeModel,
                                                               boolean isList, MediaData mediaData) {

        //根据查到的专辑 来查询具体专辑下的歌曲
        int page = typeModel.getPage();
        int size = typeModel.getSize();
        LogUtils.debug(TAG, " getFolderDataList page: " + page + " size: " + size);
        int offset = page * size;
        List<MediaDataModel> mSearchAlbumData = new ArrayList<>();
        String category_name = typeModel.getCategory_name();
        if (isList) {
           /* List<AudioVo> audioVos = mAudioHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).where(
                    AudioVoDao.Properties.Id.eq(category_name)).list();
            mSearchAlbumData.addAll(convertData(audioVos));
            long count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.Id.eq(category_name)).count();
            setTotalData(mediaData, size, count);*/
        } else {
            List<VideoVo> list1 = mVideoHelper.queryBuilder()
                    .whereOr(VideoVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%"),
                            VideoVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%"))
                    .orderRaw(getSearchRawOrderSql(category_name)).offset(offset).limit(size).list();
            MediaDataModel[] value = new MediaDataModel[1];
            List<MediaDataModel> collect1 = list1.stream().map(new Function<VideoVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(VideoVo videoVo) {
                    value[0] = new MediaDataModel();
                    value[0].setName(videoVo.getName());
                    value[0].setVideoId(videoVo.getId());
                    value[0].setVideoVo(videoVo);
                    value[0].setItemType(MetadataTypeValue.TYPE_VIDEO.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());
            mSearchAlbumData.addAll(collect1);
            long count = mVideoHelper.queryBuilder()
                    .whereOr(VideoVoDao.Properties.Name.like("%" + typeModel.getCategory_name() + "%"),
                            VideoVoDao.Properties.SymbolName.like("%" + typeModel.getCategory_name() + "%")).count();
            setTotalData(mediaData, size, count);
        }
        return mSearchAlbumData;
    }

    public boolean addCollectList(String mediaId) {
        if (TextUtils.isEmpty(mediaId)) {
            LogUtils.debug(TAG, " addCollectList: mediaId is null");
            return false;
        }
        AudioVo data = mAudioHelper.queryBuilder().where(AudioVoDao.Properties.Id.eq(mediaId)).list().get(0);
        data.setFavFlag(true);
        boolean update = mAudioHelper.update(data);
        return update;
    }

    public boolean cancelCollectList(String mediaId) {
        if (TextUtils.isEmpty(mediaId)) {
            LogUtils.debug(TAG, " addCollectList: mediaId is null");
            return false;
        }
        AudioVo data = mAudioHelper.queryBuilder().where(AudioVoDao.Properties.Id.eq(mediaId)).list().get(0);
        data.setFavFlag(false);
        boolean update = mAudioHelper.update(data);
        return update;
    }

    public List<MediaDataModel> getCollectList(int page, int size, MediaData mediaData) {
        LogUtils.debug(TAG, " getFolderDataList page: " + page + " size: " + size);
        int offset = page * size;
        List<AudioVo> list = mAudioHelper.queryBuilder().offset(offset).limit(size).where(AudioVoDao.Properties.FavFlag.eq(true)).list();
        List<MediaDataModel> mediaDataModels = convertData(list);
        long count = mAudioHelper.queryBuilder().where(AudioVoDao.Properties.FavFlag.eq(true)).count();
        setTotalData(mediaData, size, count);
        return mediaDataModels;
    }

    public boolean clearCollectList() {
        List<AudioVo> list = mAudioHelper.queryBuilder().where(AudioVoDao.Properties.FavFlag.eq(true)).list();
        List<AudioVo> audioVoStream = list.stream().map(new Function<AudioVo, AudioVo>() {
            @Override
            public AudioVo apply(AudioVo audioVo) {
                audioVo.setFavFlag(false);
                return audioVo;
            }
        }).collect(Collectors.toList());
        long l = SystemClock.currentThreadTimeMillis();
        boolean status = mAudioHelper.updateMultiples(audioVoStream);
        LogUtils.debug(TAG, " clearCollectList time: " + (SystemClock.currentThreadTimeMillis() - l));
        return status;
    }

    public boolean addHistoryList(String mediaId, long currentTime, long endDuration) {
        List<RecordVo> list = mRecordHelper.queryBuilder().where(RecordVoDao.Properties.MediaId.eq(Long.valueOf(mediaId))).list();
        RecordVo recordVo = new RecordVo();
        recordVo.setMediaId(Long.parseLong(mediaId));
        recordVo.setPlayTime(String.valueOf(currentTime));
        recordVo.setEndDuration(String.valueOf(endDuration));
        Log.d(TAG, "addHistoryList: " + list.size());
        if (list.size() == 0) {
            mRecordHelper.insert(recordVo);
        } else {
            List<RecordVo> all = mRecordHelper.queryAll();
            all.add(0, recordVo);
            all.remove(list.get(0));
            mRecordHelper.deleteAll();
            mRecordHelper.insertMultiple(all);
        }
        return false;
    }

    public List<MediaDataModel> getHistoryList(int page, int size, MediaData mediaData) {
        int offset = page * size;
        List<RecordVo> list = mRecordHelper.queryBuilder().limit(size).offset(offset)
                .orderDesc(RecordVoDao.Properties.Id).list();
        final MediaDataModel[] value = new MediaDataModel[1];
        List<MediaDataModel> collect = list.stream().map(new Function<RecordVo, MediaDataModel>() {
            @Override
            public MediaDataModel apply(RecordVo recordvo) {
                value[0] = new MediaDataModel();
                value[0].setRecordId(recordvo.getId());
                value[0].setRecordVo(recordvo);
                value[0].setName(recordvo.getAudioVo().getName());
                value[0].setItemType(MetadataTypeValue.TYPE_RECORD.getType());
                return value[0];
            }
        }).collect(Collectors.toList());
        long count = mRecordHelper.queryBuilder().count();
        setTotalData(mediaData, size, count);
        return collect;
    }

    public boolean clearHistoryList() {
        boolean b = mRecordHelper.deleteAll();
        return b;
    }

    public MediaDataModel getLastHistory() {
        MediaDataModel value = new MediaDataModel();
        RecordVo list = mRecordHelper.queryBuilder().orderDesc(RecordVoDao.Properties.Id).limit(1).offset(0).unique();
        value = new MediaDataModel();
        value.setRecordId(list.getId());
        value.setRecordVo(list);
        value.setItemType(MetadataTypeValue.TYPE_RECORD.getType());
        return value;
    }

    public Collection<? extends MediaDataModel> getSearchListAll(TypeModel searchDataType, MediaData mediaData) {
        String category_name = searchDataType.getCategory_name();
        String[] split = category_name.split(MediaIDHelper.SEARCH_SEPARATOR);
        String type = split[0];
        String name = split[1];
        int page = searchDataType.getPage();
        int size = searchDataType.getSize();
        LogUtils.debug(TAG, " getAllDataList page: " + page + " size: " +
                size + " type: " + type + " name: " + name);
        int offset = page * size;
        List<AudioVo> list = null;
        long count = 0;
        if (type.equals(MetadataTypeValue.TYPE_ARTIST.getType())) {
            list = mAudioHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).where(
                    AudioVoDao.Properties.SingerId.eq(name)).list();
            count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.SingerId.eq(name)).count();
        } else if (type.equals(MetadataTypeValue.TYPE_ALBUM.getType())) {
            list = mAudioHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).where(
                    AudioVoDao.Properties.AlbumId.eq(name)).list();
            count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.AlbumId.eq(name)).count();
        } else if (type.equals(MetadataTypeValue.TYPE_GENRE.getType())) {
            list = mAudioHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).where(
                    AudioVoDao.Properties.GenreId.eq(name)).list();
            count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.GenreId.eq(name)).count();
        } else if (type.equals(MetadataTypeValue.TYPE_FOLDER.getType())) {
            LogUtils.debug(TAG, " DDDDD ");
            final AudioVo[] audioVo = new AudioVo[1];
            List<FolderVo> where = mFolderHelper.queryBuilder().where(FolderVoDao.Properties.ParentId.eq(name)).list();
            list = where.stream().map(new Function<FolderVo, AudioVo>() {
                @Override
                public AudioVo apply(FolderVo folderVo) {
                    audioVo[0] = new AudioVo();
                    audioVo[0].setFolderVo(folderVo);
                    audioVo[0].setFolderId(folderVo.getId());
                    audioVo[0].setName(folderVo.getName());
                    return audioVo[0];
                }
            }).collect(Collectors.toList());
            list.addAll(mAudioHelper.queryBuilder().orderRaw(getRawOrderSql()).where(
                    AudioVoDao.Properties.FolderId.eq(name)).list());

            List<VideoVo> list1 = mVideoHelper.queryBuilder().orderRaw(getRawOrderSql()).where(
                    VideoVoDao.Properties.FolderId.eq(name)).list();
            LogUtils.debug(TAG, " VideoVo list1: " + list1);
            MediaDataModel[] value = new MediaDataModel[1];
            List<MediaDataModel> collect1 = list1.stream().map(new Function<VideoVo, MediaDataModel>() {
                @Override
                public MediaDataModel apply(VideoVo videoVo) {
                    value[0] = new MediaDataModel();
                    value[0].setName(videoVo.getName());
                    value[0].setVideoId(videoVo.getId());
                    value[0].setVideoVo(videoVo);
                    value[0].setItemType(MetadataTypeValue.TYPE_VIDEO.getType());
                    return value[0];
                }
            }).collect(Collectors.toList());

            List<MediaDataModel> convertData = convertData(list);
            convertData.addAll(collect1);

            count = (long) convertData.size();
            setTotalData(mediaData, size, count);
            /*if (size > count || offset > count) {
                size = (int) count;
            }*/
            LogUtils.debug(TAG, " getSearchAll page: "
                    + page + " offset: " + (offset + 1) + " count: " + count + " size: " + size);
            int fromIndex = offset; //0 2 4
            int toIndex = size + fromIndex; //2 4
            if (toIndex >= count) {
                toIndex = (int) count;
            }
            List<MediaDataModel> mediaDataModel = convertData.subList(fromIndex, toIndex);
            return mediaDataModel;
        } else if (type.equals(MetadataTypeValue.TYPE_MUSIC.getType())) {
            list = mAudioHelper.queryBuilder().offset(offset).limit(size).orderRaw(getRawOrderSql()).where(
                    AudioVoDao.Properties.Id.eq(name)).list();
            count = mAudioHelper.queryBuilder().where(
                    AudioVoDao.Properties.Id.eq(name)).count();
        }
        List<MediaDataModel> mediaDataModels = new ArrayList<>();
        if (list != null) {
            mediaDataModels.addAll(convertData(list));
        }
        setTotalData(mediaData, size, count);
        return mediaDataModels;
    }

    /**
     * 根据unicode编码范围：
     * --汉字：[0x4e00,0x9fa5]（e79fa5e98193e59b9ee7ad9431333365643464或十进制[19968,40869]）
     * --数字：[0x30,0x39]（或十进制[48, 57]）
     * --小写字母：[0x61,0x7a]（或十进制[97, 122]）
     * --大写字母：[0x41,0x5a]（或十进制[65, 90]）
     *
     * @param filed 要排序的字段
     * @return
     */
    private String getRawOrderSql(String... filed) {
        String filedName;
        if (filed.length == 0) {
            filedName = "NAME";
        } else {
            filedName = filed[0];
        }
        return "case \n" +
                "when unicode(" + filedName + ") between 19968 And 40869 then 0 \n" +
                "when unicode(" + filedName + ") between 65 and 90 and unicode(" + filedName + ") between 97 and 122 then 1\n" +
                "when unicode(" + filedName + ") between 48 And 57 then 4\n" +
                "else 3 end ,\n" +
                " " + filedName + " COLLATE LOCALIZED";
    }

    /**
     * 按搜索匹配度查询
     *
     * @param filed
     * @return
     */
    private String getSearchRawOrderSql(String filed) {
        return "case when NAME='" + filed + "' then 0\n" +
                " when NAME  like '" + filed + "%' then 1\n" +
                " when NAME  like '%" + filed + "%' then 2\n" +
                "  when NAME  like '%" + filed + "' then 3\n" +
                " else 4 end,\n" +
                " NAME  COLLATE LOCALIZED";
    }


}
