package com.landmark.media.controller.MediaImpl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.view.Surface;


import java.io.IOException;

/**
 * Author: chenhuaxia
 * Description: The adapter of player on MediaPlayer ,
 * you can replace a new player (exoplayer,ijkplayer,vitamio ...) on this.
 * Date: 2021/8/16 9:53
 **/
@SuppressLint("NewApi")
public class PlayerAdapter {
    private PlayerStateImpl mPlayerImpl;
    private static MediaPlayer mMediaPlayer = null;
    private static PlayerAdapter mInstance;

    public synchronized static PlayerAdapter createPlayer() {
        if (null == mMediaPlayer)
            mMediaPlayer = new MediaPlayer();
        if (null == mInstance)
            mInstance = new PlayerAdapter();
        mMediaPlayer.setOnPreparedListener(mInstance.PreparedListener);
        mMediaPlayer.setOnBufferingUpdateListener(mInstance.BufferingUpdateListener);
        mMediaPlayer.setOnCompletionListener(mInstance.CompletionListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mInstance.VideoSizeChangedListener);
        mMediaPlayer.setOnSeekCompleteListener(mInstance.SeekCompleteListener);
        mMediaPlayer.setOnInfoListener(mInstance.InfoListener);
        mMediaPlayer.setOnErrorListener(mInstance.ErrorListener);
        return mInstance;
    }

    public void setOnPlayerStateImpl(PlayerStateImpl player) {
        mPlayerImpl = player;
    }

    public void stop() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.stop();
    }

    public void play() {
        if (mMediaPlayer == null) return;
        if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
    }

    public void pause() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
    }

    public void reset() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.reset();
    }

    public void setSurface(Surface surface) {
        if (mMediaPlayer == null) return;
        mMediaPlayer.setSurface(surface);
    }

    public void release() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer == null) return;
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public void setLooping(boolean looping) {
        if (mMediaPlayer == null) return;
        mMediaPlayer.setLooping(looping);
    }

    public long getCurrentPosition() {
        if (mMediaPlayer == null) return 0;
        if (mMediaPlayer.getDuration() == 0) return 0;
        return mMediaPlayer.getCurrentPosition();
    }

    public long getDuration() {
        if (mMediaPlayer == null) return 0;
        return mMediaPlayer.getDuration();
    }

    public long getVideoHeight() {
        if (mMediaPlayer == null) return 0;
        return mMediaPlayer.getVideoHeight();
    }

    public long getVideoWidth() {
        if (mMediaPlayer == null) return 0;
        return mMediaPlayer.getVideoWidth();
    }

    public void seekTo(int position) {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(position);
        } else {
            mMediaPlayer.seekTo(position);
            mMediaPlayer.pause();
        }
    }

    public void setVideoScalingMode(int scalingMode) {
        if (mMediaPlayer == null) return;
        mMediaPlayer.setVideoScalingMode(scalingMode);
    }

    public void setPlaybackParams(float speed) {
        if (mMediaPlayer == null) return;
        PlaybackParams playbackParams = mMediaPlayer.getPlaybackParams();
        playbackParams.setSpeed(speed);
        mMediaPlayer.setPlaybackParams(playbackParams);
    }

    public void preparePlayForUri(Context context, Uri uri) {
        if (mMediaPlayer == null) return;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(context, uri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preparePlayForAssets(AssetFileDescriptor assetFileDescriptor) {
        if (mMediaPlayer == null) return;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preparePlayForPath(String path) {
        if (mMediaPlayer == null) return;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            PlaybackParams playbackParams = mMediaPlayer.getPlaybackParams();
            playbackParams.setSpeed(1);
            mMediaPlayer.setPlaybackParams(playbackParams);
            play();
            mPlayerImpl.setPlayingPlaybackState();
        }
    };

    private final MediaPlayer.OnCompletionListener CompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (mediaPlayer.getDuration() != 0)
                mPlayerImpl.setCompletionPlaybackState();
        }
    };

    private final MediaPlayer.OnBufferingUpdateListener BufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mPlayerImpl.setBufferingUpdate(mp, percent);
        }
    };

    private final MediaPlayer.OnVideoSizeChangedListener VideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mPlayerImpl.onVideoSizeChanged(mp, width, height);
        }
    };

    private final MediaPlayer.OnSeekCompleteListener SeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            mPlayerImpl.onSeekComplete(mp);
        }
    };

    private final MediaPlayer.OnInfoListener InfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            return mPlayerImpl.onInfo(mp, what, extra);
        }
    };

    private final MediaPlayer.OnErrorListener ErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return mPlayerImpl.onError(mp, what, extra);
        }
    };
}
