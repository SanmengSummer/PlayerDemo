package com.landmark.media.controller.MediaImpl.exo;

import static com.landmark.media.controller.MediaConfig.ACTION_RELEASE;
import static com.landmark.media.controller.MediaConfig.CUSTOMS_ACTION_RETURN_CURRENT_LRC;
import static com.landmark.media.controller.MediaConfig.CUSTOMS_ACTION_RETURN_CURRENT_POSITION;
import static com.landmark.media.controller.MediaConfig.CUSTOMS_ACTION_SET_SURFACE;
import static com.landmark.media.controller.MediaConfig.MEDIA_PLAYER_ASSETS;
import static com.landmark.media.controller.MediaConfig.MEDIA_PLAYER_LIST;
import static com.landmark.media.controller.MediaConfig.MEDIA_PLAYER_PATH;
import static com.landmark.media.controller.MediaConfig.STATE_DURATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.landmark.media.controller.QueueManager;
import com.landmark.media.controller.utils.LogUtils;
import com.landmark.media.controller.utils.LrcProcess;
import com.landmark.media.controller.utils.MP3ID3v2.MP3ReadID3v2;
import com.landmark.media.controller.utils.UriToPathUtil;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.db.data.MediaIDHelper;

import java.io.File;
import java.util.List;

/**
 * Author: chenhuaxia
 * Description: The Service of MediaPlayer, extend MediaBrowserServiceCompat compile MediaSession frame.
 * Date: 2021/8/12 15:55
 **/
@SuppressLint("NewApi")
public class MediaServiceExo extends MediaBrowserServiceCompat implements ExoPlayerStateImpl {
    private MediaSessionCompat mSession;
    private PlaybackStateCompat mPlaybackState;
    private ExoPlayerAdapter mPlayerAdapter;
    private static final float mPlaybackSpeed = 1.0f;
    private static final long distance = 5000;
    private static boolean hasGetLrcAction = false;

    private String currentMediaId;
    private Uri uri;


    @Override
    public void onCreate() {
        super.onCreate();
        mSession = new MediaSessionCompat(this, "MediaService2");
        mSession.setCallback(SessionCallback);
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());
        mSession.setActive(true);
        mPlayerAdapter = ExoPlayerAdapter.createPlayer(this);
        mPlayerAdapter.setOnPlayerStateImpl(this);
        initState();
    }

    private final MediaSessionCompat.Callback SessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            LogUtils.debug("MediaSessionCompat  onPlay: ");
            if (mPlaybackState.getState() != PlaybackStateCompat.STATE_PLAYING) {
                mPlayerAdapter.play();
                buildState(PlaybackStateCompat.STATE_PLAYING);
            }
        }

        @Override
        public void onPause() {
            LogUtils.debug("MediaSessionCompat  onPause: ");
            if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mPlayerAdapter.pause();
                buildState(PlaybackStateCompat.STATE_PAUSED);
            }
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            LogUtils.debug("MediaSessionCompat onPlayFromUri: " + uri);
            try {
                mPlayerAdapter.preparePlayForUri(MediaServiceExo.this, uri);
                buildState(PlaybackStateCompat.STATE_CONNECTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            try {
                if (query.equals(MEDIA_PLAYER_ASSETS)) {
                    AssetFileDescriptor assetFileDescriptor = extras.getParcelable(MEDIA_PLAYER_ASSETS);
                    mPlayerAdapter.preparePlayForAssets(assetFileDescriptor);
                } else if (query.equals(MEDIA_PLAYER_PATH)) {
                    String path = extras.getString(MEDIA_PLAYER_PATH);
                    mPlayerAdapter.preparePlayForPath(path);
                } else if (query.equals(MEDIA_PLAYER_LIST)) {
                    MediaMetadataCompat mediaMetadataCompat = QueueManager.getCurrentPlayList().get(QueueManager.currentPlayIndex);
                    currentMediaId = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    uri = UriToPathUtil.getUri(
                            mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
                    mPlayerAdapter.preparePlayForUri(MediaServiceExo.this,
                            uri);
                }
                buildState(PlaybackStateCompat.STATE_CONNECTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (hasGetLrcAction) {
                mHandler.removeMessages(HANDLER_CURRENT_INFO);
                mHandler.sendEmptyMessage(HANDLER_CURRENT_INFO);
            }
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            LogUtils.debug("MediaSessionCompat  onCustomAction: " + action);
            switch (action) {
                case CUSTOMS_ACTION_SET_SURFACE:
                    Surface surface = extras.getParcelable(CUSTOMS_ACTION_SET_SURFACE);
                    mPlayerAdapter.setSurface(surface);
                    break;
                case CUSTOMS_ACTION_RETURN_CURRENT_POSITION:
                    hasGetLrcAction = true;
                    mHandler.removeMessages(HANDLER_CURRENT_INFO);
                    mHandler.sendEmptyMessage(HANDLER_CURRENT_INFO);
                    break;
            }
        }

        @Override
        public void onCommand(@NonNull String command, @Nullable Bundle args, @Nullable ResultReceiver cb) {
            super.onCommand(command, args, cb);
            LogUtils.debug("MediaSessionCompat  onCommand: " + command);
            if (command.equals(ACTION_RELEASE)) {
                mPlayerAdapter.release();
                MediaServiceExo.this.stopSelf();
            }
        }

        @Override
        public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
            return super.onMediaButtonEvent(mediaButtonIntent);
        }

        @Override
        public void onPrepare() {
            super.onPrepare();
            LogUtils.debug("MediaSessionCompat  onPrepare: ");

        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            super.onPrepareFromMediaId(mediaId, extras);
            LogUtils.debug("MediaSessionCompat  onPrepareFromMediaId: ");
        }

        @Override
        public void onPrepareFromSearch(String query, Bundle extras) {
            super.onPrepareFromSearch(query, extras);
            LogUtils.debug("MediaSessionCompat  onPrepareFromSearch: ");
        }

        @Override
        public void onPrepareFromUri(Uri uri, Bundle extras) {
            super.onPrepareFromUri(uri, extras);
            LogUtils.debug("MediaSessionCompat  onPrepareFromUri: ");
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            LogUtils.debug("MediaSessionCompat  onPlayFromMediaId: ");
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
            LogUtils.debug("MediaSessionCompat  onSkipToQueueItem: ");
            if (id < QueueManager.getCurrentPlayList().size() && id >= 0) {
                MediaDataHelper.getInstance(MediaServiceExo.this).addHistoryList(currentMediaId, mPlayerAdapter.getCurrentPosition(), mPlayerAdapter.getDuration());
                QueueManager.setCurrentPlayIndex((int) id);
                getCurrentUri();
                onPlayFromSearch(MEDIA_PLAYER_LIST, null);
            } else Toast.makeText(MediaServiceExo.this, "不存在该歌曲", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            LogUtils.debug("MediaSessionCompat  onSkipToNext: ");
            playNext();
        }


        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            LogUtils.debug("MediaSessionCompat  onSkipToPrevious: ");
            playPrevious();
        }

        private void playNext() {
            if (mPlaybackState.getState() == PlaybackStateCompat.STATE_ERROR) {
                Toast.makeText(MediaServiceExo.this, "播放错误", Toast.LENGTH_SHORT).show();
                return;
            }
            if (QueueManager.currentPlayIndex < QueueManager.getCurrentPlayList().size() - 1) {
                MediaDataHelper.getInstance(MediaServiceExo.this).addHistoryList(currentMediaId, mPlayerAdapter.getCurrentPosition(), mPlayerAdapter.getDuration());
                QueueManager.currentPlayIndex++;
                onPlayFromSearch(MEDIA_PLAYER_LIST, null);
            } else {
                if (QueueManager.isLoop) {
                    MediaDataHelper.getInstance(MediaServiceExo.this).addHistoryList(currentMediaId, mPlayerAdapter.getCurrentPosition(), mPlayerAdapter.getDuration());
                    QueueManager.setCurrentPlayIndex(0);
                    onPlayFromSearch(MEDIA_PLAYER_LIST, null);
                } else
                    Toast.makeText(MediaServiceExo.this, "到底了", Toast.LENGTH_SHORT).show();
            }
            getCurrentUri();
        }

        private void playPrevious() {
            if (QueueManager.currentPlayIndex > 0) {
                MediaDataHelper.getInstance(MediaServiceExo.this).addHistoryList(currentMediaId, mPlayerAdapter.getCurrentPosition(), mPlayerAdapter.getDuration());
                QueueManager.currentPlayIndex--;
                onPlayFromSearch(MEDIA_PLAYER_LIST, null);
            } else {
                if (QueueManager.isLoop) {
                    MediaDataHelper.getInstance(MediaServiceExo.this).addHistoryList(currentMediaId, mPlayerAdapter.getCurrentPosition(), mPlayerAdapter.getDuration());
                    QueueManager.setCurrentPlayIndex(QueueManager.getCurrentPlayList().size() - 1);
                    onPlayFromSearch(MEDIA_PLAYER_LIST, null);
                } else
                    Toast.makeText(MediaServiceExo.this, "到头了", Toast.LENGTH_SHORT).show();
            }
            getCurrentUri();
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            LogUtils.debug("MediaSessionCompat  onFastForward: ");
            long position = mPlayerAdapter.getCurrentPosition();
            long duration = mPlayerAdapter.getDuration();
            long fastForwardPosition = Math.min(position + distance, duration);
            onSeekTo(fastForwardPosition);
        }

        @Override
        public void onRewind() {
            super.onRewind();
            LogUtils.debug("MediaSessionCompat  onRewind: " + mPlaybackState.getPosition());
            long position = mPlaybackState.getPosition();
            long rewindPosition = position - distance <= 0 ? 0 : position - distance;
            onSeekTo(rewindPosition);
        }

        @Override
        public void onStop() {
            super.onStop();
            LogUtils.debug("MediaSessionCompat  onStop: ");
            mPlayerAdapter.stop();
            buildStateStop();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            LogUtils.debug("MediaSessionCompat  onSeekTo: ");
            mPlayerAdapter.seekTo((int) pos);
            buildStatePosition(pos);
        }

        @Override
        public void onSetRating(RatingCompat rating) {
            super.onSetRating(rating);
            LogUtils.debug("MediaSessionCompat  onSetRating: " + rating);
        }

        @Override
        public void onSetRating(RatingCompat rating, Bundle extras) {
            super.onSetRating(rating, extras);
            LogUtils.debug("MediaSessionCompat  onSetRating: " + rating + "  extras:" + extras);
        }

        @Override
        public void onSetCaptioningEnabled(boolean enabled) {
            super.onSetCaptioningEnabled(enabled);
            LogUtils.debug("MediaSessionCompat  onSetCaptioningEnabled: " + enabled);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            super.onSetRepeatMode(repeatMode);
            LogUtils.debug("MediaSessionCompat  onSetRepeatMode: " + repeatMode);
            switch (repeatMode) {
                case PlaybackStateCompat.REPEAT_MODE_ONE:
                    break;
                case PlaybackStateCompat.REPEAT_MODE_ALL:
                case PlaybackStateCompat.REPEAT_MODE_GROUP:
                    QueueManager.isLoop = true;
                    break;
                case PlaybackStateCompat.REPEAT_MODE_NONE:
                case PlaybackStateCompat.REPEAT_MODE_INVALID:
                    QueueManager.isLoop = false;
                    break;
            }
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            super.onSetShuffleMode(shuffleMode);
            switch (shuffleMode) {
                case PlaybackStateCompat.SHUFFLE_MODE_NONE://随机
                    QueueManager.getRandomPlayList();
                    break;
                case PlaybackStateCompat.SHUFFLE_MODE_ALL://单曲
                    QueueManager.getSinglePlayList(QueueManager.currentPlayIndex);
                    break;
                case PlaybackStateCompat.SHUFFLE_MODE_GROUP://顺序
                case PlaybackStateCompat.SHUFFLE_MODE_INVALID:
                    QueueManager.getOrderPlayList();
                    break;
            }
            matchCurrentIndex();
            getCurrentUri();
        }


        @Override
        public void onSetPlaybackSpeed(float speed) {
            super.onSetPlaybackSpeed(speed);
//            mPlayerAdapter.setPlaybackParams(speed);
            LogUtils.debug("MediaSessionCompat  onSetPlaybackSpeed: " + speed);
        }
    };

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot(MediaIDHelper.MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentMediaId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.detach();
        List<MediaBrowserCompat.MediaItem> mediaItems = QueueManager.getData();
        currentMediaId = mediaItems.get(QueueManager.currentPlayIndex).getMediaId();
        result.sendResult(mediaItems);
    }


    private void getCurrentUri() {
        uri = UriToPathUtil.getUri(QueueManager.getCurrentPlayList().get(QueueManager.currentPlayIndex).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
    }

    /**
     * Match current index ,only use for under change of ShuffleMode.
     *
     * @DATE 2021/9/8 @Time 11:45
     */
    private void matchCurrentIndex() {
        if (QueueManager.getCurrentPlayList() != null && !QueueManager.getCurrentPlayList().isEmpty() && QueueManager.getCurrentPlayList().size() > 1)
            QueueManager.getCurrentPlayList().forEach(mediaDataModel -> {
                if (mediaDataModel.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).equals(currentMediaId)) {
                    QueueManager.setCurrentPlayIndex(QueueManager.getCurrentPlayList().indexOf(mediaDataModel));
                }
            });
    }

    private void initState() {
        Bundle bundle = new Bundle();
        bundle.putLong(STATE_DURATION, 0);
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, mPlaybackSpeed)
                .setExtras(bundle)
                .build();
        mSession.setPlaybackState(mPlaybackState);
    }

    private void buildState(int state) {
        buildState(0, state);
    }

    private void buildState(int buffer, int state) {
        long position = 0;
        long duration = 0;
        if (state != PlaybackStateCompat.STATE_CONNECTING) {
            position = mPlayerAdapter == null ? 0 : mPlayerAdapter.getCurrentPosition();
            duration = mPlayerAdapter == null ? 0 : mPlayerAdapter.getDuration();
        }
        Bundle bundle = new Bundle();
        bundle.putLong(STATE_DURATION, duration);
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(state, position, mPlaybackSpeed)
                .setBufferedPosition(buffer)
                .setExtras(bundle)
                .build();
        mSession.setPlaybackState(mPlaybackState);
    }

    private void buildStateStop() {
        Bundle bundle = new Bundle();
        bundle.putLong(STATE_DURATION, 0);
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_STOPPED, 0, mPlaybackSpeed)
                .setExtras(bundle)
                .build();
        mSession.setPlaybackState(mPlaybackState);
    }

    private void buildStatePosition(long position) {
        long duration = mPlayerAdapter == null ? 0 : mPlayerAdapter.getDuration();
        Bundle bundle = new Bundle();
        bundle.putLong(STATE_DURATION, duration);
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, position, mPlaybackSpeed)
                .setExtras(bundle)
                .build();
        mSession.setPlaybackState(mPlaybackState);
    }

    private void buildStateError(int what, int extra) {
        long position = mPlaybackState == null ? 0 : mPlayerAdapter.getCurrentPosition();
        long duration = mPlayerAdapter == null ? 0 : mPlayerAdapter.getDuration();
        Bundle bundle = new Bundle();
        bundle.putLong(STATE_DURATION, duration);
        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_ERROR, position, mPlaybackSpeed)
                .setExtras(bundle)
                .setErrorMessage(what, "message: " + extra)
                .build();
        mSession.setPlaybackState(mPlaybackState);
    }

    private LrcProcess.LrcContent getLrcContent() {
        if (uri == null || !uri.toString().contains(".mp3")) return null;
        Bundle bundle = new Bundle();
        LrcProcess lrcProcess = new LrcProcess();
        LrcProcess.LrcContent mLrcContent = new LrcProcess.LrcContent();
        long currentPosition;
        if (mSession == null
                || mSession.getController() == null
                || mSession.getController().getPlaybackState() == null)
            currentPosition = mPlayerAdapter.getCurrentPosition();
        else currentPosition = mSession.getController().getPlaybackState().getPosition();
        String lrc = null;
        try {
//            MetaInfoParser_MP3 metaInfoParser_mp3 = new MetaInfoParser_MP3();
//            metaInfoParser_mp3.parse(UriToPathUtil.getRealFile(this, uri));
            MP3ReadID3v2 metaInfoParser_mp3 = new MP3ReadID3v2(UriToPathUtil.getRealFile(this, uri));
            lrc = metaInfoParser_mp3.getLrc();
        } catch (Exception ignored) {
        }
        if (lrc != null && !lrc.isEmpty() && !lrc.equals("Unknown")) {
            lrcProcess.readLRCFormString(lrc, "UTF-8");
        } else {
            String realFilePath = UriToPathUtil.getRealFilePath(this, uri);
            String lrcFilePath = realFilePath.substring(0, realFilePath.length() - 4) + ".lrc";
            File file = new File(lrcFilePath);
            if (file.exists()) lrcProcess.readLRC(file);
        }

        List<LrcProcess.LrcContent> lrcContentList = lrcProcess.getLrcContent();
        if (lrcContentList != null && !lrcContentList.isEmpty()) {
            if (lrcContentList.size() == 1) {
                mLrcContent = lrcContentList.get(0);
            } else
                for (int i = 0; i < lrcContentList.size(); i++) {
                    Integer lastLrcTime = 0;
                    if (lrcContentList.get(i) == null) {
                        continue;
                    }
                    Integer lrcTime = lrcContentList.get(i).getLrc_time();
                    if (i < lrcContentList.size() - 1)
                        lastLrcTime = lrcContentList.get(i + 1).getLrc_time();
                    if (lrcTime <= currentPosition && currentPosition < lastLrcTime) {
                        mLrcContent = lrcContentList.get(i);
                    }
                }
        }
        if (mLrcContent == null || mLrcContent.getLrc() == null)
            mLrcContent = new LrcProcess.LrcContent("", 0);
        bundle.putLong(CUSTOMS_ACTION_RETURN_CURRENT_POSITION, currentPosition);
        bundle.putParcelable(CUSTOMS_ACTION_RETURN_CURRENT_LRC, mLrcContent);
        mSession.setExtras(bundle);
        return mLrcContent;
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(100);
        super.onDestroy();
    }

    private final static int HANDLER_CURRENT_INFO = 100;
    private final static long handlerDelayMills = 1000;
    @SuppressLint({"HandlerLeak", "SetTextI18n"})
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == HANDLER_CURRENT_INFO) {
                if (hasGetLrcAction) {
                    mHandler.sendEmptyMessageDelayed(HANDLER_CURRENT_INFO, handlerDelayMills);
                    if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING)
                        getLrcContent();
                }
            }
        }
    };

    @Override
    public void setPlayingPlaybackState() {
        buildState(PlaybackStateCompat.STATE_PLAYING);
    }

    @Override
    public void setCompletionPlaybackState() {
        SessionCallback.onSkipToNext();
    }

    @Override
    public void setBufferingUpdate(SimpleExoPlayer mp, int percent) {
        LogUtils.debug("setBufferingUpdate" + percent);
        if (percent >= 100)
            buildState(0, PlaybackStateCompat.STATE_PLAYING);
        else
            buildState(percent, PlaybackStateCompat.STATE_BUFFERING);
    }

    @Override
    public void onVideoSizeChanged(SimpleExoPlayer mp, int width, int height) {
        LogUtils.debug("onVideoSizeChanged(MediaPlayer" + mp + ", " + "width" + width + "height" + height + " )");
    }

    @Override
    public void onSeekComplete(SimpleExoPlayer mp) {
        LogUtils.debug("onSeekComplete");
    }

    @Override
    public boolean onInfo(SimpleExoPlayer mp, int what, int extra) {
        LogUtils.debug("onInfo(MediaPlayer" + mp + ", " + "what" + what + "extra" + extra + " )");
        return false;
    }

    @Override
    public boolean onError(SimpleExoPlayer mp, ExoPlaybackException exception) {
        buildStateError(exception.type, exception.rendererIndex);
        Toast.makeText(this, "The song cannot be played！", Toast.LENGTH_SHORT).show();
        return false;
    }
}



