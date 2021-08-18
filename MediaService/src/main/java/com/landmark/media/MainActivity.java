package com.landmark.media;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.landmark.media.common.MetadataTypeValue;
import com.landmark.media.db.data.MediaIDHelper;
import com.landmark.media.model.MediaData;
import com.landmark.media.model.MediaDataModel;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.utils.LogUtils;

import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private MediaDataHelper instance;
    private MediaData search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = MediaDataHelper.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        //test用
        int id = v.getId();
        switch (id) {
            case R.id.button0:
                //所有的数据
                long l = SystemClock.currentThreadTimeMillis();
                MediaData musicDataList = instance.getMusicDataList(0, 50,
                        MediaIDHelper.getRootType(MediaIDHelper.TYPE_1));
                long l1 = SystemClock.currentThreadTimeMillis() - l;
                Log.d(TAG, "onCreate: " + l1);
                musicDataList.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->所有的数据 : " + dataModel.toString());
                    }
                });

                LogUtils.debug(TAG, " page: " + musicDataList.getCurrentPage() + " size: " + musicDataList.getPageSize()
                        + " totalNum: " + musicDataList.getTotalNum() + " totalPage: " + musicDataList.getTotalPage());
                break;
            case R.id.button1:
                //专辑
                MediaData musicDataList1 = instance.getMusicDataList(0, 30,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM, false, MediaIDHelper.TYPE_1));
                musicDataList1.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->专辑列表 : " + dataModel.toString());
                    }
                });
                LogUtils.debug(TAG, " page: " + musicDataList1.getCurrentPage() + "size: " + musicDataList1.getPageSize()
                        + "totalNum: " + musicDataList1.getTotalNum() + " totalPage: " + musicDataList1.getTotalPage());

                break;
            case R.id.button3:
                //专辑
                //根据某一个专辑查询这个专辑下的歌曲
                MediaData musicDataList2 = instance.getMusicDataList(0, 30,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM,
                                false, MediaIDHelper.TYPE_1, "1"));
                musicDataList2.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->专辑列表 ->具体专辑下数据 : " + dataModel.toString());
                    }
                });
                LogUtils.debug(TAG, " page: " + musicDataList2.getCurrentPage() + "size: " + musicDataList2.getPageSize()
                        + "totalNum: " + musicDataList2.getTotalNum() + " totalPage: " + musicDataList2.getTotalPage());

                break;
            case R.id.button2:
                //歌手
                MediaData musicDataList3 = instance.getMusicDataList(0, 30,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST, false, MediaIDHelper.TYPE_1));
                musicDataList3.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->歌手列表 : " + dataModel.toString());
                    }
                });
                LogUtils.debug(TAG, " page: " + musicDataList3.getCurrentPage() + "size: " + musicDataList3.getPageSize()
                        + "totalNum: " + musicDataList3.getTotalNum() + " totalPage: " + musicDataList3.getTotalPage());

                break;

            case R.id.button4:
                MediaData musicDataList4 = instance.getMusicDataList(0, 30,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST, false, MediaIDHelper.TYPE_1, "1"));
                musicDataList4.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->歌手列表 ->歌手列表数据 : " + dataModel.toString());
                    }
                });
                LogUtils.debug(TAG, " page: " + musicDataList4.getCurrentPage() + "size: " + musicDataList4.getPageSize()
                        + "totalNum: " + musicDataList4.getTotalNum() + " totalPage: " + musicDataList4.getTotalPage());
                break;

            case R.id.button5:
                MediaData musicDataList5 = instance.getMusicDataList(0, 30,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE, false, MediaIDHelper.TYPE_1));
                musicDataList5.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->流派列表 : " + dataModel.toString());
                    }
                });
                LogUtils.debug(TAG, " page: " + musicDataList5.getCurrentPage() + "size: " + musicDataList5.getPageSize()
                        + "totalNum: " + musicDataList5.getTotalNum() + " totalPage: " + musicDataList5.getTotalPage());

                break;
            case R.id.button6:
                MediaData musicDataList6 = instance.getMusicDataList(0, 30,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE, false, MediaIDHelper.TYPE_1, "1"));
                musicDataList6.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->流派列表 ->歌手列表数据 : " + dataModel.toString());
                    }
                });
                LogUtils.debug(TAG, " page: " + musicDataList6.getCurrentPage() + "size: " + musicDataList6.getPageSize()
                        + "totalNum: " + musicDataList6.getTotalNum() + " totalPage: " + musicDataList6.getTotalPage());

                break;
            case R.id.button7:

                MediaData musicDataList7 = instance.getMusicDataList(0, 30,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_FOLDER, false, MediaIDHelper.TYPE_1));
                musicDataList7.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->文件夹  : " + dataModel.toString());
                    }
                });
                LogUtils.debug(TAG, " page: " + musicDataList7.getCurrentPage() + "size: " + musicDataList7.getPageSize()
                        + "totalNum: " + musicDataList7.getTotalNum() + " totalPage: " + musicDataList7.getTotalPage());

                break;
            case R.id.button8:
                MediaData musicDataList8 = instance.getMusicDataList(0, 8,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_FOLDER, false, MediaIDHelper.TYPE_1, "3"));
                musicDataList8.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "->文件夹 ->数据  : " + dataModel.toString());
                    }
                });
                LogUtils.debug(TAG, " page: " + musicDataList8.getCurrentPage() + "size: " + musicDataList8.getPageSize()
                        + "totalNum: " + musicDataList8.getTotalNum() + " totalPage: " + musicDataList8.getTotalPage());

                break;


            case R.id.button9:
                search = instance.getSearch(0, 15,
                        MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1, "第", null));
                Log.d(TAG, "onClick button9: " + search.getData().size());
                search.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "搜索 ->  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                        + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
                break;
            case R.id.button10:
                //专辑
                search = instance.getSearch(0, 15,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM, true, MediaIDHelper.TYPE_1, "个"));
                Log.d(TAG, "onClick button10: " + search.getData().size());
                search.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "专辑 ->  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                        + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
                break;
            case R.id.button11:
                //歌手
                search = instance.getSearch(0, 15,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST, true, MediaIDHelper.TYPE_1, "周"));
                Log.d(TAG, "onClick button10: " + search.getData().size());
                search.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "歌手 ->  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                        + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
                break;
            case R.id.button12:
                //标题
                search = instance.getSearch(0, 15,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_TITLE,
                                true, MediaIDHelper.TYPE_1, "香"));
                Log.d(TAG, "onClick button10: " + search.getData().size());
                search.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "标题 ->  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                        + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
                break;
            case R.id.button13:
                //流派
                search = instance.getSearch(0, 15, MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE,
                        true, MediaIDHelper.TYPE_1, "第"));
                Log.d(TAG, "onClick button13: " + search.getData().size());
                search.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "流派 ->  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                        + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
                break;


            case R.id.button14:
                MediaDataModel mediaDataModel = search.getData().get(10);
                String itemType = mediaDataModel.getItemType();
                MediaData searchList = new MediaData();
                if (itemType.equals(MetadataTypeValue.TYPE_ARTIST.getType())) {

                    searchList = instance.getSearchList(0, 15,
                            MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                                    mediaDataModel.getSingerVo().getId().toString(), itemType));
                } else if (itemType.equals(MetadataTypeValue.TYPE_ALBUM.getType())) {
                    searchList = instance.getSearchList(0, 15,
                            MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                                    mediaDataModel.getAlbumVo().getId().toString(), itemType));

                } else if (itemType.equals(MetadataTypeValue.TYPE_GENRE.getType())) {
                    searchList = instance.getSearchList(0, 15,
                            MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                                    mediaDataModel.getGenreVo().getId().toString(), itemType));

                } else if (itemType.equals(MetadataTypeValue.TYPE_FOLDER.getType())) {
                    searchList = instance.getSearchList(0, 15,
                            MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                                    mediaDataModel.getFolderVo().getId().toString(), itemType));
                } else if (itemType.equals(MetadataTypeValue.TYPE_MUSIC.getType())) {
                    searchList = instance.getSearchList(0, 15,
                            MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                                    mediaDataModel.getId().toString(), itemType));
                }
                searchList.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "搜索 ->数据  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                        + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
                break;
            case R.id.button15:
                //专辑
                MediaData searchList2 = new MediaData();
                searchList2 = instance.getSearchList(0, 15,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM,
                                true, MediaIDHelper.TYPE_1,
                                search.getData().get(2).getAlbumVo().getId().toString()));
                searchList2.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "专辑 ->数据  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + searchList2.getCurrentPage() + "size: " + searchList2.getPageSize()
                        + " totalNum: " + searchList2.getTotalNum() + " totalPage: " + searchList2.getTotalPage());
                break;
            case R.id.button16:
                //歌手
                MediaData searchList3 = new MediaData();
                searchList3 = instance.getSearchList(0, 15,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE,
                                true, MediaIDHelper.TYPE_1,
                                search.getData().get(0).getSingerVo().getId().toString()));
                searchList3.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "歌手 ->数据  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + searchList3.getCurrentPage() + "size: " + searchList3.getPageSize()
                        + " totalNum: " + searchList3.getTotalNum() + " totalPage: " + searchList3.getTotalPage());
                break;
            case R.id.button17:
                MediaData searchList4 = new MediaData();
                searchList4 = instance.getSearchList(0, 15,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_TITLE,
                                true, MediaIDHelper.TYPE_1,
                                search.getData().get(0).getId().toString()));
                searchList4.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "标题 ->数据  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + searchList4.getCurrentPage() + "size: " + searchList4.getPageSize()
                        + " totalNum: " + searchList4.getTotalNum() + " totalPage: " + searchList4.getTotalPage());
                break;
            case R.id.button18:
                MediaData searchList5 = new MediaData();
                searchList5 = instance.getSearchList(0, 15,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE,
                                true, MediaIDHelper.TYPE_1,
                                search.getData().get(0).getGenreVo().getId().toString()));
                searchList5.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "流派 ->数据  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + searchList5.getCurrentPage() + "size: " + searchList5.getPageSize()
                        + " totalNum: " + searchList5.getTotalNum() + " totalPage: " + searchList5.getTotalPage());
                break;

            case R.id.button20:
                MediaData collectList = instance.getCollectList(0, 30);
                collectList.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "收藏列表 ->数据  : " + dataModel);
                    }
                });
                LogUtils.debug(TAG, " page: " + collectList.getCurrentPage() + "size: " + collectList.getPageSize()
                        + " totalNum: " + collectList.getTotalNum() + " totalPage: " + collectList.getTotalPage());
                break;
            case R.id.button21:
                boolean b = instance.addCollectList("8");
                LogUtils.debug(TAG, " 添加状态 " + b);
                break;
            case R.id.button22:
                boolean status = instance.cancelCollectList("8");
                LogUtils.debug(TAG, " 取消状态 " + status);
                break;
            case R.id.button23:
                boolean b1 = instance.clearCollectList();
                LogUtils.debug(TAG, " 清空状态 " + b1);
                break;
        }
    }
}