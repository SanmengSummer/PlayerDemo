package com.landmark.media.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

import com.landmark.media.R;
import com.landmark.media.common.MetadataTypeValue;
import com.landmark.media.controller.utils.PlayerUtils;
import com.landmark.media.db.data.MediaDataHelper;
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

public class CollectHistoryActivity extends AppCompatActivity {
    private static final String TAG = "CollectHistoryActivity";

    private RecyclerView mRecyclerView;
    private MediaDataHelper mInstance;
    private List<MediaDataModel> mSearch = new ArrayList<>();
    private SearchAdapter mSearchAdapter;
    //搜索的当前的页面
    int mCurrentPage = 0;
    //数据展示条数
    int mSize = Constants.mMusicSize;

    private TextView mCurrent_pager;
    private TextView mTotal_pager;
    private Button bt_next;
    private LinearLayout mllShowpager;
    private TextView mTotal_num;
    private RelativeLayout mRl;
    private MyHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_history);
        mHandler = new MyHandler(this);
        mInstance = MediaDataHelper.getInstance(this);
        mCurrent_pager = findViewById(R.id.current_pager);
        mTotal_pager = findViewById(R.id.total_pager);
        bt_next = findViewById(R.id.bt_next);
        mllShowpager = findViewById(R.id.ll_showpager);
        mTotal_num = findViewById(R.id.total_num);
        mRl = findViewById(R.id.rl);
        initRecycler();
    }

    public void initRecycler() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchAdapter = new SearchAdapter(mSearch, this);
        mRecyclerView.setAdapter(mSearchAdapter);

        /*androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("收藏及历史记录");
        supportActionBar.setDisplayHomeAsUpEnabled(true);*/

        //u盘 状态
        mInstance.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDeviceStatus(boolean status, int actionUsbExtraStatusValue) {
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

    public void bt_collect(View view) {
        mCurrentPage = 0;
        MediaData collectList = mInstance.getCollectList(mCurrentPage, mSize);
        collectList.getData().forEach(new Consumer<MediaDataModel>() {
            @Override
            public void accept(MediaDataModel dataModel) {
                Log.d(TAG, "收藏列表 ->数据  : " + dataModel);
            }
        });
        LogUtils.debug(TAG, " page: " + collectList.getCurrentPage() + "size: " + collectList.getPageSize()
                + " totalNum: " + collectList.getTotalNum() + " totalPage: " + collectList.getTotalPage());
        mSearch.clear();
        mSearch.addAll(collectList.getData());
        mSearchAdapter.notifyDataSetChanged();
        setPager(collectList);
        isCollect = true;
        mSearchAdapter.setOnClickListener(new SearchAdapter.IOnClicklistener() {
            @Override
            public void onClickListener(MetadataTypeValue value, String id) {

            }

            @Override
            public void onClickMusicListener(List<MediaDataModel> models, int model) {
                listener(models, model);
            }

            @Override
            public void onLongClickMusicListener(MetadataTypeValue value, MediaDataModel id) {
                boolean remove = mSearch.remove(id);
                boolean b = mInstance.cancelCollectList(id.getId().toString());
                LogUtils.debug(TAG, " 更新成功 ");
                if (remove) {
                    mSearchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    boolean isCollect;

    @SuppressLint("NotifyDataSetChanged")
    public void bt_history(View view) {
        mCurrentPage = 0;
        MediaData historyList = mInstance.getHistoryList(mCurrentPage, mSize);
        historyList.getData().forEach(new Consumer<MediaDataModel>() {
            @Override
            public void accept(MediaDataModel dataModel) {
                Log.d(TAG, "历史记录列表 ->数据  : " + dataModel);
            }
        });
        mSearch.clear();
        mSearch.addAll(historyList.getData());
        mSearchAdapter.notifyDataSetChanged();
        setPager(historyList);
        isCollect = false;
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
     * @param models 当前页面的数据
     * @param model  当前点击的item数据
     */
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

    public void bt_history_clear(View view) {
        mInstance.clearHistoryList();
        bt_history(view);
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
        LogUtils.debug(TAG, " setpager: " + data.getCurrentPage());
        mCurrent_pager.setText(" 当前页：" + data.getCurrentPage() + " ");
        mTotal_pager.setText("总页数： " + data.getTotalPage() + " ");
        mTotal_num.setText("总条数: " + data.getTotalNum());
        if (data.getCurrentPage() == data.getTotalPage()) {
            bt_next.setEnabled(false);
            mCurrentPage = 0;
        } else {
            bt_next.setEnabled(true);
        }
    }

    public void next(View view) {
        mCurrentPage = mCurrentPage + 1;
        if (isCollect) {
            MediaData collectList = mInstance.getCollectList(mCurrentPage, mSize);
            mSearch.addAll(collectList.getData());
            mSearchAdapter.notifyDataSetChanged();
            setPager(collectList);
        } else {
            MediaData historyList = mInstance.getHistoryList(mCurrentPage, mSize);
            mSearch.addAll(historyList.getData());
            mSearchAdapter.notifyDataSetChanged();
            setPager(historyList);
        }
    }

    /**
     * @param model    数据
     * @param position 点击的位置
     */
    public void setData(MediaData model, int position) {
        //todo 收藏历史点击后的事件

        PlayerUtils.startPlayer(this,model,position);
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


    static class MyHandler extends Handler {
        WeakReference<CollectHistoryActivity> weakReference;

        public MyHandler(CollectHistoryActivity searchActivity) {
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