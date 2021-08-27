package com.landmark.media.controller.MediaImpl;

import static com.landmark.media.controller.MediaConfig.*;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.landmark.media.controller.QueueManager;
import com.landmark.media.controller.utils.LogUtils;
import com.landmark.media.controller.utils.LrcProcess;
import com.landmark.media.controller.utils.PlayerUtils;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.model.MediaData;

import java.util.List;

/**
 * Author: chenhuaxia
 * Description: The manager of MediaPlayer, you can use player by this manager,etc connect,listener,controller.
 * Date: 2021/8/15 15:55
 **/
public class MediaPlayerManager {
    public static final int random = PlaybackStateCompat.SHUFFLE_MODE_NONE;
    public static final int single = PlaybackStateCompat.SHUFFLE_MODE_ALL;
    public static final int order = PlaybackStateCompat.SHUFFLE_MODE_GROUP;
    @SuppressLint("StaticFieldLeak")
    private static MediaPlayerManager mInstance;
    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat mController;
    private MediaBrowserCompat.SubscriptionCallback mBrowserSubscriptionCallback;
    private MediaControllerCompat.Callback mControllerCallback;
    private MediaListDataChangeCallback mMediaListDataChangeCallback;
    private MediaControllerCompat.TransportControls transportControls;
    private Context mContext;
    private String mMediaId;
    private List<MediaBrowserCompat.MediaItem> mMediaItemList;
    private static int mCurrentIndex = 0;
    private static int mMode = order;
    private static int previousIndex = mCurrentIndex;

    /**
     * Author: chenhuaxia
     * Description:A singleton of class for MediaPlayerManager.
     * Date: 2021/8/25 14:54
     **/
    public synchronized static MediaPlayerManager getInstance() {
        if (null == mInstance) {
            mInstance = new MediaPlayerManager();
        }
        return mInstance;
    }

    /**
     * Author: chenhuaxia
     * Description:Connect MediaSession.
     *
     * @param context (Context  for Activity/Service/Application )For initConnect
     * @param mediaId ( Media Id for find out data of media)For initConnect
     *                Date: 2021/8/25 14:54
     **/
    public void connectMediaSession(Context context,
                                    String mediaId) {
        mControllerCallback = ControllerCallback;
        mBrowserSubscriptionCallback = BrowserSubscriptionCallback;
        initConnect(context, mediaId);
    }

    /**
     * Author: chenhuaxia
     * Description:Connect MediaSession.
     *
     * @param context             (Context  for Activity/Service/Application )For initConnect
     * @param iControllerCallback (replace of MediaControllerCompat.Callback)
     * @param mediaId             ( Media Id for find out data of media)For initConnect
     *                            Date: 2021/8/25 14:54
     **/
    public void connectMediaSession(Context context,
                                    MediaControllerCompat.Callback iControllerCallback,
                                    String mediaId) {

        mControllerCallback = iControllerCallback;
        mBrowserSubscriptionCallback = BrowserSubscriptionCallback;
        initConnect(context, mediaId);
    }

    /**
     * Author: chenhuaxia
     * Description:Connect MediaSession.
     *
     * @param context                      (Context  for Activity/Service/Application )For initConnect
     * @param iBrowserSubscriptionCallback (Replace of MediaControllerCompat.Callback)
     * @param iControllerCallback          (Replace of  MediaBrowserCompat.SubscriptionCallback)
     * @param mediaId                      (Media Id for find out data of media)For initConnect
     *                                     Date: 2021/8/25 14:54
     **/
    public void connectMediaSession(Context context,
                                    MediaBrowserCompat.SubscriptionCallback iBrowserSubscriptionCallback,
                                    MediaControllerCompat.Callback iControllerCallback,
                                    String mediaId) {
        mControllerCallback = iControllerCallback;
        mBrowserSubscriptionCallback = iBrowserSubscriptionCallback;
        initConnect(context, mediaId);
    }

    /**
     * Author: chenhuaxia
     * Description:Create MediaBrowserCompat ,init mediaId and context.
     *
     * @param context Context  for Activity/Service/Application
     * @param mediaId Media Id for find out data of media
     *                Date: 2021/8/25 14:54
     **/
    private void initConnect(Context context, String mediaId) {
        mContext = context;
        mMediaId = mediaId;
        mMediaBrowser = new MediaBrowserCompat(context,
                new ComponentName(context, MediaService.class),
                BrowserConnectionCallback, null);
    }

    /**
     * Author: chenhuaxia
     * Description:Set SurfaceView in MediaPlayer.
     *
     * @param surfaceView SurfaceView
     *                    Date: 2021/8/25 14:54
     **/
    public void setSurfaceView(SurfaceView surfaceView) {
        if (mController == null || null == surfaceView) return;
        Bundle bundle = new Bundle();
        SurfaceHolder holder = surfaceView.getHolder();
        setSurface(holder, bundle);
    }

    private void setSurface(@NonNull SurfaceHolder holder, Bundle bundle) {
        Surface surface = holder.getSurface();
        bundle.putParcelable(CUSTOMS_ACTION_SET_SURFACE, surface);
        getTransportControls().sendCustomAction(CUSTOMS_ACTION_SET_SURFACE, bundle);
    }

    /**
     * Author: chenhuaxia
     * Description:Get transportControls from MediaControllerCompat. It may be null,be careful of NullPointerException
     *
     * @return MediaControllerCompat.TransportControls
     * Date: 2021/8/25 14:54
     **/
    public MediaControllerCompat.TransportControls getTransportControls() {
        if (null != mController && transportControls == null)
            transportControls = mController.getTransportControls();
        return transportControls;
    }

    /**
     * Author: chenhuaxia
     * Description:Get PlaybackState from MediaControllerCompat.TransportControls . It may be null,be careful of NullPointerException
     *
     * @return PlaybackStateCompat
     * Date: 2021/8/25 14:54
     **/
    public PlaybackStateCompat getPlaybackState() {
        if (null != mController)
            return mController.getPlaybackState();
        return null;
    }

    /**
     * Author: chenhuaxia
     * Description:Listen RegisterSessionCallback .
     * If register success ,callback List<MediaBrowserCompat.MediaItem> children;
     *
     * @param mRegisterSessionCallback RegisterSessionCallback
     *                                 Date: 2021/8/25 14:54
     **/
    public void setOnRegisterSessionCallback(RegisterSessionCallback mRegisterSessionCallback) {
        this.mRegisterSessionCallback = mRegisterSessionCallback;
    }

    /**
     * Author: chenhuaxia
     * Description:Listen MediaListDataChangeCallback .
     *
     * @param MediaListDataChangeCallback MediaListDataChangeCallback
     * @Callback getMediaListDataChangeCallback(long currentPosition, LrcProcess.LrcContent mLrcContent);
     * @Callback updateCurrentMedia(List < MediaMetadataCompat > children, int index);
     * <p>
     * Date: 2021/8/25 14:54
     **/
    public void setOnMediaListDataChangeCallback(MediaListDataChangeCallback MediaListDataChangeCallback) {
        if (getTransportControls() == null) return;
        getTransportControls().sendCustomAction(CUSTOMS_ACTION_RETURN_CURRENT_POSITION, null);
        if (mMediaListDataChangeCallback == null)
            mMediaListDataChangeCallback = MediaListDataChangeCallback;
    }

    /**
     * Author: chenhuaxia
     * Description:Set player mode(Random,Single,Order and loop or nu-loop ) .
     *
     * @param mode   int(Random,Single,Order)
     * @param isLoop boolean(loop or nu-loop)
     **/
    public void setPlayerMode(int mode, boolean isLoop) {
        mMode = mode;
        if (getTransportControls() == null) return;
        setPlayerMediaItemList(mode);
        getTransportControls().setRepeatMode(isLoop ? PlaybackStateCompat.REPEAT_MODE_ALL :
                PlaybackStateCompat.REPEAT_MODE_NONE);
    }

    private void setPlayerMediaItemList(int mode) {
        getTransportControls().setShuffleMode(mode);
    }

    private final MediaBrowserCompat.ConnectionCallback BrowserConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    LogUtils.debug("MediaBrowserCompat onConnected: ");
                    try {
                        connectToSession(mMediaId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionFailed() {
                    LogUtils.debug("MediaBrowserCompat onConnectionFailed: ");
                }

                @Override
                public void onConnectionSuspended() {
                    LogUtils.debug("MediaBrowserCompat onConnectionSuspended: ");
                    super.onConnectionSuspended();
                }
            };

    private void connectToSession(@Nullable String mediaId) {
        if (mController == null)
            mController = new MediaControllerCompat(mContext, mMediaBrowser.getSessionToken());
        mController.registerCallback(mControllerCallback);
        if (mediaId == null)
            mediaId = mMediaBrowser.getRoot();
        mMediaBrowser.unsubscribe(mediaId);
        mMediaBrowser.subscribe(mediaId, mBrowserSubscriptionCallback);
    }

    /**
     * Author: chenhuaxia
     * Description:Connect MediaBrowser .
     **/
    public void connect() {
        if (!mMediaBrowser.isConnected())
            mMediaBrowser.connect();
    }

    /**
     * Author: chenhuaxia
     * Description:Disconnect MediaBrowser .
     **/
    public void disconnect() {
        if (mMediaBrowser.isConnected())
            mMediaBrowser.disconnect();
    }

    /**
     * Author: chenhuaxia
     * Description:Release MediaSession .
     **/
    public void release() {
        mController.sendCommand(ACTION_RELEASE, null, null);
        mInstance = null;
        mContext = null;
        mController = null;
        mMediaBrowser = null;
    }

    /**
     * Author: chenhuaxia
     * Description:Add Collect by MediaDataHelper .
     **/
    public void addCollect() {
        boolean b = MediaDataHelper.getInstance(mContext).addCollectList(mMediaItemList.get(mCurrentIndex).getMediaId());
        Toast.makeText(mContext, b ? "收藏成功！" : "收藏失败！", Toast.LENGTH_SHORT).show();
    }

    /**
     * Author: chenhuaxia
     * Description:Cancel Collect by MediaDataHelper .
     **/
    public void cancelCollect() {
        boolean b = MediaDataHelper.getInstance(mContext).cancelCollectList(mMediaItemList.get(mCurrentIndex).getMediaId());
        Toast.makeText(mContext, b ? "取消收藏成功！" : "取消收藏失败！", Toast.LENGTH_SHORT).show();
    }

    /**
     * Author: chenhuaxia
     * Description:Set data for QueueManager by MediaData of PlayerUtils.
     **/
    public void setData(int position) {
        MediaData data = PlayerUtils.getData();
        QueueManager.setData(data, position);
    }

    public interface MediaListDataChangeCallback {
        void getMediaListDataChangeCallback(long currentPosition, LrcProcess.LrcContent mLrcContent);

        void updateCurrentMedia(@Nullable List<MediaMetadataCompat> children, int index);
    }

    private RegisterSessionCallback mRegisterSessionCallback;

    public interface RegisterSessionCallback {
        void registerSuccess(List<MediaBrowserCompat.MediaItem> children);
    }

    MediaBrowserCompat.SubscriptionCallback BrowserSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    mMediaItemList = children;
                    setPlayerMediaItemList(mMode);
                    if (mRegisterSessionCallback != null)
                        mRegisterSessionCallback.registerSuccess(mMediaItemList);
                }

                @Override
                public void onError(@NonNull String parentId) {

                }
            };

    MediaControllerCompat.Callback ControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onSessionReady() {
            super.onSessionReady();
            LogUtils.debug("MediaControllerCompat onSessionReady: ");
            if (mMediaListDataChangeCallback != null)
                mMediaListDataChangeCallback.updateCurrentMedia(QueueManager.getCurrentPlayList(), mCurrentIndex);
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            LogUtils.debug("MediaControllerCompat onSessionDestroyed: ");
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
            LogUtils.debug("MediaControllerCompat onSessionEvent: ");
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            LogUtils.debug("MediaControllerCompat onPlaybackStateChanged: " + state.toString());
            if (mMediaListDataChangeCallback != null && previousIndex != mCurrentIndex) {
                mMediaListDataChangeCallback.updateCurrentMedia(QueueManager.getCurrentPlayList(), mCurrentIndex);
                previousIndex = mCurrentIndex;
            }
        }


        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            LogUtils.debug("MediaControllerCompat onMetadataChanged: ");
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
            LogUtils.debug("MediaControllerCompat onMetadataChanged: ");
        }

        @Override
        public void onQueueTitleChanged(CharSequence title) {
            super.onQueueTitleChanged(title);
            LogUtils.debug("MediaControllerCompat onQueueTitleChanged: ");
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
            try {
                if (mMediaListDataChangeCallback != null) {
                    mCurrentIndex = extras.getInt(CUSTOMS_ACTION_RETURN_CURRENT_INDEX);
                    long mCurrentPosition = extras.getLong(CUSTOMS_ACTION_RETURN_CURRENT_POSITION);
                    LrcProcess.LrcContent mLrcContent = extras.getParcelable(CUSTOMS_ACTION_RETURN_CURRENT_LRC);
                    if (mCurrentPosition > -1 && mLrcContent != null)
                        mMediaListDataChangeCallback.getMediaListDataChangeCallback(mCurrentPosition, mLrcContent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {
            super.onAudioInfoChanged(info);
            LogUtils.debug("MediaControllerCompat onAudioInfoChanged: ");
        }

        @Override
        public void onCaptioningEnabledChanged(boolean enabled) {
            super.onCaptioningEnabledChanged(enabled);
            LogUtils.debug("MediaControllerCompat onCaptioningEnabledChanged: ");
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            super.onRepeatModeChanged(repeatMode);
            LogUtils.debug("MediaControllerCompat onRepeatModeChanged: ");
        }

        @Override
        public void onShuffleModeChanged(int shuffleMode) {
            super.onShuffleModeChanged(shuffleMode);
            LogUtils.debug("MediaControllerCompat onShuffleModeChanged: ");
        }

        @SuppressLint("RestrictedApi")
        @Override
        public IMediaControllerCallback getIControllerCallback() {
            LogUtils.debug("MediaControllerCompat getIControllerCallback: ");
            return super.getIControllerCallback();
        }

        @Override
        public void binderDied() {
            super.binderDied();
            LogUtils.debug("MediaControllerCompat binderDied: ");
        }
    };
}

