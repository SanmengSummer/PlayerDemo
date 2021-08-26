package com.landmark.media.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.landmark.media.R;
import com.landmark.media.application.MediaApplication;
import com.landmark.media.common.MetadataTypeValue;
import com.landmark.media.controller.utils.PlayerUtils;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.db.data.MediaIDHelper;
import com.landmark.media.demo.adapter.SearchAdapter;
import com.landmark.media.demo.common.Constants;
import com.landmark.media.interfaces.IDeviceListener;
import com.landmark.media.model.MediaData;
import com.landmark.media.model.MediaDataModel;
import com.landmark.media.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SearchActivity";

    private RecyclerView mRecyclerView;
    private MediaDataHelper mInstance;
    private EditText mEditText;
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

    String mediaId; //为当前歌曲的文件夹id
    public static final String MEDIAID_TAG = "mediaId";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        if (intent != null) {
            //todo 当前的歌曲文件夹id
            mediaId = intent.getStringExtra(MEDIAID_TAG);
            //当前歌曲的文件夹id
            if (mediaId == null) {
                mediaId = "1";
            }
        }
        initView();
        initRecycler();

        androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("搜索");
        supportActionBar.setDisplayHomeAsUpEnabled(true);


        mInstance.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDeviceStatus(boolean status, int actionUsbExtraStatusValue) {
                LogUtils.debug(TAG, " registerDeviceListener onDeviceStatus status: " + status + " status"
                        + actionUsbExtraStatusValue);
                if (!status) {
                    mSearch.clear();
                    mSearchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initView() {
        mHandler = new MyHandler(this);
        mInstance = MediaDataHelper.getInstance(this);
        mEditText = findViewById(R.id.et_search);
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
        defaultResult();
    }

    public void defaultResult() {
        LogUtils.debug(TAG, " id: " + mediaId);
        if (mediaId != null) {
            mRl.setVisibility(View.GONE);
            mSearchListCurrentPage = 0;
            LogUtils.debug(TAG, " id: " + mediaId);
            String itemType = MetadataTypeValue.TYPE_FOLDER.getType();
            MediaData searchList = new MediaData();
            searchList = mInstance.getSearchList(mSearchListCurrentPage, mSearchListSize,
                    MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                            mediaId, itemType));
            mSearch.clear();
            mSearch.addAll(searchList.getData());
            mSearchAdapter.notifyDataSetChanged();
            saveStatus(MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                    mediaId, itemType), mediaId, true);
            setpager(searchList);

            mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
                @Override
                public void onClickListener(MetadataTypeValue value, String id) {
                    mSearchListCurrentPage = 0;
                    LogUtils.debug(TAG, " id: " + id);
                    MediaData searchList = new MediaData();
                    searchList = mInstance.getSearchList(mSearchListCurrentPage, mSearchListSize,
                            MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                                    id, itemType));
                    mSearch.clear();
                    mSearch.addAll(searchList.getData());
                    mSearchAdapter.notifyDataSetChanged();
                    saveStatus(MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                            id, itemType), id, true);
                    setpager(searchList);
                }

                @Override
                public void onClickMusicListener(List<MediaDataModel> models, int model) {
                    listener(models, model);
                }
            });
        } else {
            mRl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    //
    public void key_search(View view) {
    }

    @SuppressLint("NotifyDataSetChanged")
    public void all_search(View view) {
        Editable text = mEditText.getText();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String searchText = text.toString();
        //搜索专辑
        mSearchCurrentPage = 0;
        MediaData mediaData = mInstance.getSearch(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1, searchText, null));

        Log.d(TAG, "onClick button9: " + mediaData.getData().size());
        mediaData.getData().forEach(new Consumer<MediaDataModel>() {
            @Override
            public void accept(MediaDataModel dataModel) {
                Log.d(TAG, "搜索 ->  : " + dataModel);
            }
        });
        mSearch.clear();
        mSearch.addAll(mediaData.getData());
        LogUtils.debug(TAG, " page: " + mediaData.getCurrentPage() + "size: " + mediaData.getPageSize()
                + " totalNum: " + mediaData.getTotalNum() + " totalPage: " + mediaData.getTotalPage());
        mSearchAdapter.notifyDataSetChanged();
        saveStatus(MediaIDHelper.MEDIA_ID_ROOT, searchText, false);
        setpager(mediaData);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {
                mSearchListCurrentPage = 0;
                LogUtils.debug(TAG, " id: " + id);
                String itemType = value.getType();

                MediaData searchList = new MediaData();
                searchList = mInstance.getSearchList(mSearchListCurrentPage, mSearchListSize,
                        MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                                id, itemType));
                mSearch.clear();
                mSearch.addAll(searchList.getData());
                mSearchAdapter.notifyDataSetChanged();
                saveStatus(MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1,
                        id, itemType), id, true);
                setpager(searchList);
            }

            @Override
            public void onClickMusicListener(List<MediaDataModel> models, int model) {
                listener(models, model);
            }
        });
    }

    /**
     * 文件夹搜索
     *
     * @param view
     */
    public void dir_search(View view) {
        Editable text = mEditText.getText();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String searchText = text.toString();
        //搜索专辑
        mSearchCurrentPage = 0;
        MediaData search = mInstance.getSearch(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_FOLDER,
                        true, MediaIDHelper.TYPE_1, searchText));
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
        saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_FOLDER, searchText, false);
        setpager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {
                mSearchListCurrentPage = 0;
                LogUtils.debug(TAG, " id: " + id);
                MediaData searchList = mInstance.getSearchList(mSearchListCurrentPage, mSearchListSize,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_FOLDER,
                                true, MediaIDHelper.TYPE_1,
                                id));
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
                saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_FOLDER, id, true);
                setpager(searchList);
            }

            @Override
            public void onClickMusicListener(List<MediaDataModel> models, int model) {
                listener(models, model);
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
        Editable text = mEditText.getText();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String searchText = text.toString();
        //搜索专辑
        mSearchCurrentPage = 0;
        MediaData search = mInstance.getSearch(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST,
                        true, MediaIDHelper.TYPE_1, searchText));
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
        saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST, searchText, false);
        setpager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {
                mSearchListCurrentPage = 0;
                LogUtils.debug(TAG, " id: " + id);
                MediaData searchList = mInstance.getSearchList(mSearchListCurrentPage, mSearchListSize,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST,
                                true, MediaIDHelper.TYPE_1,
                                id));
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
                saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_ARTIST, id, true);
                setpager(searchList);
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
        Editable text = mEditText.getText();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String searchText = text.toString();
        //搜索专辑
        mSearchCurrentPage = 0;
        MediaData search = mInstance.getSearch(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE,
                        true, MediaIDHelper.TYPE_1, searchText));
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
        saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE, searchText, false);
        setpager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {
                mSearchListCurrentPage = 0;
                LogUtils.debug(TAG, " id: " + id);
                MediaData searchList = mInstance.getSearchList(mSearchListCurrentPage, mSearchListSize,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE,
                                true, MediaIDHelper.TYPE_1,
                                id));
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
                setpager(searchList);
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
        Editable text = mEditText.getText();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String searchText = text.toString();
        //搜索专辑
        mSearchCurrentPage = 0;
        MediaData search = mInstance.getSearch(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM,
                        true, MediaIDHelper.TYPE_1, searchText));
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
        saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM, searchText, false);
        setpager(search);
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {
                mSearchListCurrentPage = 0;
                LogUtils.debug(TAG, " id: " + id);
                MediaData searchList = mInstance.getSearchList(mSearchListCurrentPage, mSearchListSize,
                        MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM,
                                true, MediaIDHelper.TYPE_1,
                                id));
                mSearch.clear();
                mSearch.addAll(searchList.getData());
                LogUtils.debug(TAG, " page: " + searchList.getCurrentPage() + "size: " + searchList.getPageSize()
                        + " totalNum: " + searchList.getTotalNum() + " totalPage: " + searchList.getTotalPage());
                searchList.getData().forEach(new Consumer<MediaDataModel>() {
                    @Override
                    public void accept(MediaDataModel dataModel) {
                        Log.d(TAG, "专辑 ->数据  : " + dataModel);
                    }
                });
                mSearchAdapter.notifyDataSetChanged();
                saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_ALBUM, id, true);
                setpager(searchList);
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
        Editable text = mEditText.getText();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String searchText = text.toString();
        MediaData search = mInstance.getSearch(mSearchCurrentPage, mSearchSize,
                MediaIDHelper.getType(MediaIDHelper.MEDIA_ID_MUSICS_BY_TITLE,
                        true, MediaIDHelper.TYPE_1, searchText));
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

        saveStatus(MediaIDHelper.MEDIA_ID_MUSICS_BY_TITLE, searchText, true);
        setpager(search);
    }

    /**
     * 显示页数
     *
     * @param data
     */
    public void setpager(MediaData data) {
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
                str = mType;
            } else {
                str = MediaIDHelper.getType(mType,
                        true, MediaIDHelper.TYPE_1, mValue);
            }
            mSearchListCurrentPage = mSearchListCurrentPage + 1;
            search = mInstance.getSearchList(mSearchListCurrentPage, mSearchListSize,
                    str);
            mSearch.addAll(search.getData());
            LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                    + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
            mSearchAdapter.notifyDataSetChanged();
        } else {
            String str;
            if (mType.equals(MediaIDHelper.MEDIA_ID_ROOT)) {
                str = MediaIDHelper.getSearchRootType(MediaIDHelper.TYPE_1, mValue, null);
            } else {
                str = MediaIDHelper.getType(mType,
                        true, MediaIDHelper.TYPE_1, mValue);
            }
            mSearchCurrentPage = mSearchCurrentPage + 1;
            search = mInstance.getSearch(mSearchCurrentPage, mSearchSize,
                    str);
            mSearch.addAll(search.getData());
            LogUtils.debug(TAG, " page: " + search.getCurrentPage() + "size: " + search.getPageSize()
                    + " totalNum: " + search.getTotalNum() + " totalPage: " + search.getTotalPage());
            mSearchAdapter.notifyDataSetChanged();
        }
        setpager(search);
    }

    public void listener(List<MediaDataModel> models, int model) {
        List<MediaDataModel> collect = models.stream().filter(new Predicate<MediaDataModel>() {
            @Override
            public boolean test(MediaDataModel model) {
                if (model.getItemType().equals(MetadataTypeValue.TYPE_MUSIC.getType())) {
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


    public void bt_collect(View view) {
        startActivity(new Intent(this, CollectHistoryActivity.class));
    }

    /**
     * @param model    数据集
     * @param position 当前点击的位置
     */
    public void setData(MediaData model, int position) {
        //todo 点击后的事件
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

    static class MyHandler extends Handler {
        WeakReference<SearchActivity> weakReference;

        public MyHandler(SearchActivity searchActivity) {
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