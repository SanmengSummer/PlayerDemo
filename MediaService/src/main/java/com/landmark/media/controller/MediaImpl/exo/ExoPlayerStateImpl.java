package com.landmark.media.controller.MediaImpl.exo;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Author: chenhuaxia
 * Description: The interface relate PlayerAdapter and MusicService listening native player`s callback.
 * Date: 2021/8/16 9:55
 **/
public interface ExoPlayerStateImpl {
    default void setPlayingPlaybackState() {
    }

    default void setCompletionPlaybackState() {
    }

    default void setBufferingUpdate(SimpleExoPlayer mp, int percent) {
    }

    default void onVideoSizeChanged(SimpleExoPlayer mp, int width, int height) {
    }

    default void onSeekComplete(SimpleExoPlayer mp) {
    }

    default boolean onInfo(SimpleExoPlayer mp, int what, int extra) {
        return false;
    }

    default boolean onError(SimpleExoPlayer mp, ExoPlaybackException exception) {
        return false;
    }
}
