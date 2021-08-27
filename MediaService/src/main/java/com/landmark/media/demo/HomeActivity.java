package com.landmark.media.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.landmark.media.R;
import com.landmark.media.common.MetadataTypeValue;
import com.landmark.media.controller.utils.PlayerUtils;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.db.data.MediaIDHelper;
import com.landmark.media.demo.adapter.SearchAdapter;
import com.landmark.media.demo.common.Constants;
import com.landmark.media.interfaces.IDeviceListener;
import com.landmark.media.model.MediaData;
import com.landmark.media.model.MediaDataModel;
import com.landmark.media.model.TypeModel;
import com.landmark.media.utils.LogUtils;
import com.landmark.media.utils.SharedUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private RecyclerView mRecyclerView;
    private MediaDataHelper mInstance;
    private List<MediaDataModel> mSearch = new ArrayList<>();
    private SearchAdapter mSearchAdapter;
    private TextView mCurrent_pager;
    private TextView mTotal_pager;
    private TextView mTotal_num;
    private Button bt_next;

    String mType;
    String mValue;
    boolean isSearchAudio;
    //搜索的当前的页面
    int mSearchCurrentPage = 0;
    //数据展示条数
    int mSearchSize = Constants.mExSize;
    //搜索具体的数据的当前页
    int mSearchListCurrentPage = 0;
    //展示的条数
    int mSearchListSize = Constants.mMusicSize;
    private LinearLayout mllShowpager;
    private MyHandler mHandler;
    private RelativeLayout mRl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initRecycler();

        Intent intent = new Intent();
        ComponentName name = new ComponentName("com.app.carscanner","com.app.scanner.ScannerService");
        intent.setComponent(name);
        startService(intent);
    }

    private void initView() {
        mHandler = new MyHandler(this);
        mInstance = MediaDataHelper.getInstance(this);
        mCurrent_pager = findViewById(R.id.current_pager);
        mTotal_pager = findViewById(R.id.total_pager);
        mTotal_num = findViewById(R.id.total_num);
        bt_next = findViewById(R.id.bt_next);
        mllShowpager = findViewById(R.id.ll_showpager);
        mRl = findViewById(R.id.rl);
    }

    public void initRecycler() {
        mRecyclerView = findViewById(R.id.rcy_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchAdapter = new SearchAdapter(mSearch, this);
        mRecyclerView.setAdapter(mSearchAdapter);
        boolean devices = (boolean) SharedUtils.getInstance(this).getParam(
                com.landmark.media.common.Constants.SHARE_DEVICE_STATUS, false);
        if (devices) {
            name_search(null);
        }
//        androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
//        supportActionBar.setTitle("首页");
        mInstance.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDeviceStatus(boolean status, int actionUsbExtraStatusValue) {
                if (!status) {
                    mSearch.clear();
                    mSearchAdapter.notifyDataSetChanged();
                } else {
                    if (mSearch.size() == 0) {
                        name_search(null);
                    }
                }
            }
        });
    }

    private void saveStatus(String type, String id, boolean b) {
        mType = type;
        mValue = id;
        isSearchAudio = b;
    }

    /**
     * 歌手
     *
     * @param view
     */
    public void artist_search(View view) {
        //搜索专辑
        mSearchCurrentPage = 0;
        MediaData search = mInstance.getMusicDataList(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST,
                        false, MediaIDHelper.TYPE_1));
        Log.d(TAG, "onClick button10: " + search.getData().size());

        search.getData().forEach(new Consumer<MediaDataModel>() {
            @Override
            public void accept(MediaDataModel dataModel) {
                Log.d(TAG, "歌手 ->  : " + dataModel);
            }
        });
        mSearch.clear();
        mSearch.addAll(search.getData());
        LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
        mSearchAdapter.notifyDataSetChanged();
        saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST, "", false);
        setPager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {
                mSearchListCurrentPage = 0;
                LogUtils.debug(TAG, " id: " + id);
                MediaData searchList = mInstance.getMusicDataList(mSearchListCurrentPage, mSearchListSize,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST,
                                false, MediaIDHelper.TYPE_1, id));
                mSearch.clear();
                mSearch.addAll(searchList.getData());
                LogUtils.debug(TAG, " page: " + searchList.getCurrentPage() + "size: " + searchList.getPageSize()
                        + " totalNum: " + searchList.getTotalNum() + " totalPage: " + searchList.getTotalPage());
                searchList.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "歌手 ->数据  : " + dataModel);
                    }
                });
                mSearchAdapter.notifyDataSetChanged();
                saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST, id, true);
                setPager(searchList);
            }

            @Override
            public void onClickMusicListener(List<MediaDataModel> models, int model) {
                listener(models, model);
            }
        });
    }

    /**
     * 流派
     *
     * @param view
     */
    public void genre_search(View view) {
        //搜索专辑
        mSearchCurrentPage = 0;
        MediaData search = mInstance.getMusicDataList(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE,
                        false, MediaIDHelper.TYPE_1));
        Log.d(TAG, "onClick button10: " + search.getData().size());

        search.getData().forEach(new Consumer<MediaDataModel>() {
            @Override
            public void accept(MediaDataModel dataModel) {
                Log.d(TAG, "文件夹 ->  : " + dataModel);
            }
        });
        mSearch.clear();
        mSearch.addAll(search.getData());
        LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
        mSearchAdapter.notifyDataSetChanged();
        saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE, "", false);
        setPager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {
                mSearchListCurrentPage = 0;
                LogUtils.debug(TAG, " id: " + id);
                MediaData searchList = mInstance.getMusicDataList(mSearchListCurrentPage, mSearchListSize,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE,
                                false, MediaIDHelper.TYPE_1, id));
                mSearch.clear();
                mSearch.addAll(searchList.getData());
                LogUtils.debug(TAG, " page: " + searchList.getCurrentPage() + "size: " + searchList.getPageSize()
                        + " totalNum: " + searchList.getTotalNum() + " totalPage: " + searchList.getTotalPage());
                searchList.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "文件夹 ->数据  : " + dataModel);
                    }
                });
                mSearchAdapter.notifyDataSetChanged();
                saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE, id, true);
                setPager(searchList);
            }

            @Override
            public void onClickMusicListener(List<MediaDataModel> models, int model) {
                listener(models, model);
            }
        });
    }

    /**
     * 专辑
     *
     * @param view
     */
    public void album_search(View view) {
        //搜索专辑
        mSearchCurrentPage = 0;
        MediaData search = mInstance.getMusicDataList(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM,
                        false, MediaIDHelper.TYPE_1));
        Log.d(TAG, "onClick button10: " + search.getData().size());

        search.getData().forEach(new Consumer<MediaDataModel>() {
            @Override
            public void accept(MediaDataModel dataModel) {
                Log.d(TAG, "文件夹 ->  : " + dataModel);
            }
        });
        mSearch.clear();
        mSearch.addAll(search.getData());
        LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
        mSearchAdapter.notifyDataSetChanged();
        saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM, "", false);
        setPager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {
                mSearchListCurrentPage = 0;
                LogUtils.debug(TAG, " id: " + id);
                MediaData searchList = mInstance.getMusicDataList(mSearchListCurrentPage, mSearchListSize,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM,
                                false, MediaIDHelper.TYPE_1, id));
                mSearch.clear();
                mSearch.addAll(searchList.getData());
                LogUtils.debug(TAG, " page: " + searchList.getCurrentPage() + "size: " + searchList.getPageSize()
                        + " totalNum: " + searchList.getTotalNum() + " totalPage: " + searchList.getTotalPage());
                searchList.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "文件夹 ->数据  : " + dataModel);
                    }
                });
                mSearchAdapter.notifyDataSetChanged();
                saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM, id, true);
                setPager(searchList);
            }

            @Override
            public void onClickMusicListener(List<MediaDataModel> models, int model) {
                listener(models, model);
            }
        });
    }


    /**
     * 按歌名搜索
     *
     * @param view
     */
    public void name_search(View view) {
        mSearchCurrentPage = 0;
        MediaData search = mInstance.getMusicDataList(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_ROOT,
                        false, MediaIDHelper.TYPE_1));

        Log.d(TAG, "onClick button10: " + search.getData().size());

        search.getData().forEach(new Consumer<MediaDataModel>() {
            @Override
            public void accept(MediaDataModel dataModel) {
                Log.d(TAG, "标题 ->  : " + dataModel);
            }
        });
        mSearch.clear();
        mSearch.addAll(search.getData());
        LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
        mSearchAdapter.notifyDataSetChanged();
        saveStatus(MediaIDHelper.MEDIA_ID_ROOT, "", true);
        setPager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {

            }

            @Override
            public void onClickMusicListener(List<MediaDataModel> models, int model) {
                listener(models, model);
            }
        });
    }

    /**
     * 显示页数
     *
     * @param data
     */
    public void setPager(MediaData data) {
        List<MediaDataModel> data1 = data.getData();
        LogUtils.debug(TAG, " setpager size: " + data1.size());
        if (data1.size() != 0) {
            mllShowpager.setVisibility(View.VISIBLE);
            mRl.setVisibility(View.INVISIBLE);
        } else {
            mllShowpager.setVisibility(View.INVISIBLE);
            mRl.setVisibility(View.VISIBLE);
        }
        LogUtils.debug(TAG, " setpager: " + mCurrent_pager);
        mCurrent_pager.setText(" 当前页：" + data.getCurrentPage() + " ");
        mTotal_pager.setText("总页数： " + data.getTotalPage() + " ");
        mTotal_num.setText("总条数: " + data.getTotalNum());
        if (data.getCurrentPage() == data.getTotalPage()) {
            bt_next.setEnabled(false);
            mSearchListCurrentPage = 0;
            mSearchCurrentPage = 0;
        } else {
            bt_next.setEnabled(true);
        }
    }

    /**
     * 下一页
     *
     * @param view
     */
    public void next(View view) {
        MediaData search;
        if (isSearchAudio) {
            String str;
            if (mType.contains(MediaIDHelper.MEDIA_ID_ROOT)) {
                boolean contains = mType.contains(MediaIDHelper.TYPE_2);
                if (contains) {
                    str = MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_ROOT,
                            false, MediaIDHelper.TYPE_2);
                } else {
                    str = MediaIDHelper.getType(mType,
                            false, MediaIDHelper.TYPE_1);
                }

            } else {
                str = MediaIDHelper.getType(mType,
                        false, MediaIDHelper.TYPE_1, mValue);
            }
            mSearchListCurrentPage = mSearchListCurrentPage + 1;
            search = mInstance.getMusicDataList(mSearchListCurrentPage, mSearchListSize,
                    str);
            mSearch.addAll(search.getData());
            LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                    + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
            mSearchAdapter.notifyDataSetChanged();
        } else {
            String str;
            str = MediaIDHelper.getType(mType,
                    false, MediaIDHelper.TYPE_1);
            mSearchCurrentPage = mSearchCurrentPage + 1;
            search = mInstance.getMusicDataList(mSearchCurrentPage, mSearchSize,
                    str);
            mSearch.addAll(search.getData());
            LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                    + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
            mSearchAdapter.notifyDataSetChanged();
        }
        setPager(search);
    }

    public void listener(List<MediaDataModel> models, int model) {
        List<MediaDataModel> collect = models.stream().filter(new Predicate<MediaDataModel>() {
            @Override
            public boolean test(MediaDataModel model) {
                if (model.getItemType().equals(MetadataTypeValue.TYPE_MUSIC.getType())
                        || model.getItemType().equals(MetadataTypeValue.TYPE_VIDEO.getType())) {
                    return true;
                }
                return false;
            }
        }).collect(Collectors.toList());
        MediaData mediaData = new MediaData();
        mediaData.setData(collect);
        //点击数据
        LogUtils.debug(TAG, "onClickMusicListener: " + models.size());
        Message message = mHandler.obtainMessage();
        message.obj = mediaData;
        message.what = 0;
        message.arg1 = model;
        mHandler.sendMessage(message);
    }

    /**
     * @param model    数据对象
     * @param position 点击的当前位置
     */
    public void setData(MediaData model, int position) {
        //todo home点击后的事件

        PlayerUtils.startPlayer(this, model, position);
        LogUtils.debug(TAG, " MODEL: " + model.toString());
        model.getData().forEach(new Consumer<MediaDataModel>() {
            @Override
            public void accept(MediaDataModel model) {
                LogUtils.debug(TAG, " accept MediaData: " + model.getName());
            }
        });
        Toast.makeText(this, "" + model.getData().size() + " position: " + position, Toast.LENGTH_SHORT).show();
    }

    public void bt_skip_search(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void bt_skip_history(View view) {
        startActivity(new Intent(this, CollectHistoryActivity.class));
    }

    public void video(View view) {
        mSearchListCurrentPage = 0;
        MediaData search = mInstance.getMusicDataList(mSearchListCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_ROOT,
                        false, MediaIDHelper.TYPE_2));
        mSearch.clear();
        mSearch.addAll(search.getData());
        LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
        mSearchAdapter.notifyDataSetChanged();
        saveStatus(MediaIDHelper.MEDIA_ID_ROOT +
                MediaIDHelper.SEARCH_SEPARATOR + MediaIDHelper.TYPE_2, "", true);
        setPager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {

            }

            @Override
            public void onClickMusicListener(List<MediaDataModel> models, int model) {
                listener(models, model);
            }
        });

    }


    static class MyHandler extends Handler {
        WeakReference<HomeActivity> weakReference;

        public MyHandler(HomeActivity searchActivity) {
            weakReference = new WeakReference<>(searchActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                MediaData obj = (MediaData) msg.obj;
                int arg1 = msg.arg1;
                weakReference.get().setData(obj, arg1);
            }
        }
    }

}