package com.landmark.media;

import static com.landmark.media.controller.MediaConfig.*;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.landmark.media.controller.MediaImpl.MediaPlayerManager;
import com.landmark.media.controller.utils.LogUtils;
import com.landmark.media.controller.utils.LrcProcess;
import com.landmark.media.controller.utils.PlayerUtils;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.db.data.MediaIDHelper;
import com.landmark.media.model.MediaData;
import com.landmark.media.model.MediaDataModel;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity {
    private MediaPlayerManager instance;
    private SeekBar mSeek;
    private TextView textTitle;
    private Button btnPlayMode;
    private ArrayList<MediaBrowserCompat.MediaItem> mediaItemList;
    private ImageView imageView;
    private TextView textView;
    private int initIndex;
    private static int playMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        textTitle = findViewById(R.id.text_title);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.textView);
        btnPlayMode = findViewById(R.id.play_mode);
        mSeek = findViewById(R.id.seek);
        mSeek.setOnSeekBarChangeListener(onSeekBarChangeListener);

        initPlayer();
        MediaDataHelper.getInstance(this).registerDeviceListener((status, actionUsbExtraStatusValue) -> {
            if (!status || actionUsbExtraStatusValue == -1) {
                Toast.makeText(this, "U盘加载失败！没有播放数据！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initView() {
        MediaData data = PlayerUtils.getData();
        MediaDataModel mediaDataModel = data.getData().get(initIndex);
        Bitmap iconBitmap = mediaDataModel.getIcon();
        CharSequence title = mediaDataModel.getName();
        CharSequence artist = mediaDataModel.getSingerVo().getName();
        CharSequence album = mediaDataModel.getAlbumVo().getName();
        CharSequence genre = mediaDataModel.getGenreVo().getName();
        imageView.setImageBitmap(iconBitmap);
        textView.setText("title: " + title +
                "\nartist: " + artist +
                "\nalbum: " + album +
                "\ngenre: " + genre);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initPlayer() {
        initIndex = getIntent().getIntExtra(PlayerUtils.PLAYER_FOR_INDEX, 0);
        requestPermissions(101);
        instance = MediaPlayerManager.getInstance();
        instance.setData(initIndex);
        initView();
        instance.connectMediaSession(this, MediaIDHelper.MEDIA_ID_ROOT);
        instance.setOnRegisterSessionCallback(list -> {
            mediaItemList = (ArrayList<MediaBrowserCompat.MediaItem>) list;
            SurfaceView surfaceView = findViewById(R.id.surfaceView);
            instance.setSurfaceView(surfaceView);
            instance.setOnMediaListDataChangeCallback(new MediaPlayerManager.MediaListDataChangeCallback() {
                @Override
                public void getMediaListDataChangeCallback(long currentPosition, LrcProcess.LrcContent mLrcContent) {
                    updateSeekBar();
                    if (mLrcContent != null && !mLrcContent.getLrc_time().equals(-1))
                        textTitle.setText(mLrcContent.getLrc());
                    else textTitle.setText("");
                }

                @Override
                public void updateCurrentMedia(List<MediaMetadataCompat> children, int index) {
                    MediaMetadataCompat mediaMetadataCompat = children.get(index);
                    Bitmap iconBitmap = mediaMetadataCompat.getDescription().getIconBitmap();
                    CharSequence title = mediaMetadataCompat.getDescription().getTitle();
                    CharSequence artist = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                    CharSequence album = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
                    CharSequence genre = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
                    imageView.setImageBitmap(iconBitmap);
                    textView.setText("title: " + title +
                            "\nartist: " + artist +
                            "\nalbum: " + album +
                            "\ngenre: " + genre);
                }
            });
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        instance.connect();
        setPlayMode(btnPlayMode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        instance.disconnect();
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            LogUtils.debug("MediaSessionCompat  onStopTrackingTouch: ");
            PlaybackStateCompat playbackState = instance.getPlaybackState();
            if (playbackState == null || playbackState.getState() == PlaybackStateCompat.STATE_STOPPED) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(MEDIA_PLAYER_LIST, mediaItemList);
                instance.getTransportControls().playFromSearch(MEDIA_PLAYER_LIST, bundle);
            }
            assert playbackState != null;
            Bundle extras = playbackState.getExtras();
            assert extras != null;
            long duration = extras.getLong(STATE_DURATION);
            long position = seekBar.getProgress() * duration / 100;
            instance.getTransportControls().seekTo(position);
        }
    };

    public void start(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MEDIA_PLAYER_LIST, mediaItemList);
        instance.getTransportControls().playFromSearch(MEDIA_PLAYER_LIST, bundle);
    }

    public void play_pause(View view) {
        if (instance.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)
            instance.getTransportControls().pause();
        else if (instance.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
            instance.getTransportControls().play();
    }

    public void stop(View view) {
        instance.getTransportControls().stop();
    }


    public void play_mode(View v) {
        playMode++;
        setPlayMode((Button) v);
    }

    private void setPlayMode(Button view) {
        switch (playMode) {
            case 1:
                view.setText("随机播放");
                instance.setPlayerMode(MediaPlayerManager.random, false);
                break;
            case 2:
                view.setText("单曲播放");
                instance.setPlayerMode(MediaPlayerManager.single, false);
                break;
            case 3:
                view.setText("顺序循环");
                instance.setPlayerMode(MediaPlayerManager.order, true);
                break;
            case 4:
                view.setText("随机循环");
                instance.setPlayerMode(MediaPlayerManager.random, true);
                break;
            case 5:
                view.setText("单曲循环");
                instance.setPlayerMode(MediaPlayerManager.single, true);
                break;
            case 6:
            case 0:
                playMode = 0;
                view.setText("顺序播放");
                instance.setPlayerMode(MediaPlayerManager.order, false);
                break;
        }
    }

    private void updateSeekBar() {
        PlaybackStateCompat playbackState = instance.getPlaybackState();
        long position = playbackState.getPosition();
        Bundle extras = playbackState.getExtras();
        long duration = 0;
        if (extras != null)
            duration = extras.getLong(STATE_DURATION);
        long p = duration == 0 ? 0 : position * 100 / duration;
        mSeek.setProgress((int) p);
    }

    public void skipNext(View view) {
        instance.getTransportControls().skipToNext();
    }

    public void skipPrevious(View view) {
        instance.getTransportControls().skipToPrevious();
    }

    public void rewind(View view) {
        instance.getTransportControls().rewind();
    }

    public void fastForward(View view) {
        instance.getTransportControls().fastForward();
    }

    public void collect(View view) {
        instance.addCollect();
    }

    public void cancel_collect(View view) {
        instance.cancelCollect();
    }

    public void requestPermissions(int requestCode) {
        try {
            ArrayList<String> requestPrecessionArr = new ArrayList<>();
            int hasSdcardWrite = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasSdcardWrite != PackageManager.PERMISSION_GRANTED)
                requestPrecessionArr.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasSystemAlertWindow = checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
            if (hasSystemAlertWindow != PackageManager.PERMISSION_GRANTED)
                requestPrecessionArr.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
            if (requestPrecessionArr.size() >= 1) {
                String[] requestArray = new String[requestPrecessionArr.size()];
                for (int i = 0; i < requestArray.length; i++) {
                    requestArray[i] = requestPrecessionArr.get(i);
                }
                requestPermissions(requestArray, requestCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
