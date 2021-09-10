package com.landmark.media.controller.MediaImpl.exo;

import static com.google.android.exoplayer2.Player.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.view.Surface;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.Metadata;

import java.util.List;

/**
 * Author: chenhuaxia
 * Description: The adapter of player on ExoPlayer ,
 * you can replace a new player (exoplayer,ijkplayer,vitamio ...) on this.
 * Date: 2021/8/16 9:53
 **/
@SuppressLint("NewApi")
public class ExoPlayerAdapter {
    private static ExoPlayerStateImpl mPlayerImpl;
    private static SimpleExoPlayer mExoPlayer = null;
    private static ExoPlayerAdapter mInstance;

    public synchronized static ExoPlayerAdapter createPlayer(Context mContext) {
        if (null == mExoPlayer)
            mExoPlayer = new SimpleExoPlayer.Builder(mContext).build();
        if (null == mInstance)
            mInstance = new ExoPlayerAdapter();
        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            }

            @Override
            public void onStaticMetadataChanged(List<Metadata> metadataList) {
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_BUFFERING:
                        mPlayerImpl.setBufferingUpdate(mExoPlayer, mExoPlayer.getBufferedPercentage());
                        break;
                    case Player.STATE_READY:
                        play();
                        mPlayerImpl.setPlayingPlaybackState();
                        break;
                    case Player.STATE_ENDED:
                        if (mExoPlayer.getDuration() != 0)
                            mPlayerImpl.setCompletionPlaybackState();
                        break;
                }
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {//prepare
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                mPlayerImpl.onError(mExoPlayer, error);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
            }
        });

        return mInstance;
    }

    public void setOnPlayerStateImpl(ExoPlayerStateImpl player) {
        mPlayerImpl = player;
    }

    public void stop() {
        if (mExoPlayer == null) return;
        mExoPlayer.stop();
    }

    public static void play() {
        if (mExoPlayer == null) return;
        if (!mExoPlayer.isPlaying()) mExoPlayer.play();
    }

    public void pause() {
        if (mExoPlayer == null) return;
        if (mExoPlayer.isPlaying()) mExoPlayer.pause();
    }


    public void setSurface(Surface surface) {
        if (mExoPlayer == null) return;
        mExoPlayer.setVideoSurface(surface);
    }

    public void release() {
        if (mExoPlayer == null) return;
        mExoPlayer.release();
        mExoPlayer = null;
    }

    public void reset() {
        if (mExoPlayer == null) return;
    }

    public void setVolume(float volume) {
        if (mExoPlayer == null) return;
        mExoPlayer.setVolume(volume);
    }

    public void setLooping(boolean looping) {
        if (mExoPlayer == null) return;
        mExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
    }

    public long getCurrentPosition() {
        if (mExoPlayer == null) return 0;
        if (mExoPlayer.getDuration() == 0) return 0;
        return mExoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        if (mExoPlayer == null) return 0;
        return mExoPlayer.getDuration();
    }

//    public long getVideoHeight() {
//        if (mExoPlayer == null) return 0;
//        return mExoPlayer.getVideoHeight();
//    }
//
//    public long getVideoWidth() {
//        if (mExoPlayer == null) return 0;
//        return mExoPlayer.getVideoWidth();
//    }

    public void seekTo(int position) {
        if (mExoPlayer == null) return;
        if (mExoPlayer.isPlaying()) {
            mExoPlayer.seekTo(position);
        } else {
            mExoPlayer.seekTo(position);
            mExoPlayer.pause();
        }
    }

    public void setVideoScalingMode(int scalingMode) {
        if (mExoPlayer == null) return;
        mExoPlayer.setVideoScalingMode(scalingMode);
    }
//
//    public void setPlaybackParams(float speed) {
//        if (mExoPlayer == null) return;
//        PlaybackParams playbackParams = mExoPlayer.getPlaybackParams();
//        playbackParams.setSpeed(speed);
//        mExoPlayer.setPlaybackParams(playbackParams);
//    }

    public void preparePlayForUri(Context context, Uri uri) {
        if (mExoPlayer == null) return;
        try {
            MediaItem mediaItem = MediaItem.fromUri(uri);
            mExoPlayer.setMediaItem(mediaItem);
            mExoPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preparePlayForAssets(AssetFileDescriptor assetFileDescriptor) {
        if (mExoPlayer == null) return;
//        try {
//            MediaItem.
//            assetFileDescriptor.createOutputStream()
//            MediaItem mediaItem = MediaItem.fromUri(uri);
//            mExoPlayer.setMediaItem(mediaItem);
//            mExoPlayer.prepare();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void preparePlayForPath(String path) {
        if (mExoPlayer == null) return;
        try {
            MediaItem mediaItem = MediaItem.fromUri(path);
            mExoPlayer.setMediaItem(mediaItem);
            mExoPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
