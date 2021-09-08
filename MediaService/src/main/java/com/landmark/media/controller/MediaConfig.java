package com.landmark.media.controller;

/**
 * Author: chenhuaxia
 * Description:Some defined config of this player in use.
 * Date: 2021/8/11 16:10
 **/
public class MediaConfig {
    /**
     * PlaybackStateCompat.state ,use for set a media duration in PlaybackState
     */
    public final static String STATE_DURATION = "state_duration";
    /**
     * Play video by way of MEDIA_PLAYER_ASSETS(get resource of assets) ,
     * MEDIA_PLAYER_PATH(get video`s url path),
     * MEDIA_PLAYER_LIST(get video`s play list).
     */
    public final static String MEDIA_PLAYER_ASSETS = "media_player_assets";
    public final static String MEDIA_PLAYER_PATH = "media_player_path";
    public final static String MEDIA_PLAYER_LIST = "media_player_list";
    /**
     * MediaSession send commend for ACTION
     * {@code ACTION_RELEASE} release media player
     * {@code CUSTOMS_ACTION_SET_SURFACE } set surface
     * {@code CUSTOMS_ACTION_RETURN_CURRENT_INDEX } return media list current index in playing.
     * {@code CUSTOMS_ACTION_RETURN_CURRENT_POSITION}return media list current position in playing.
     * {@code CUSTOMS_ACTION_RETURN_CURRENT_LRC}}return media list current lrc in playing.
     */
    public final static String ACTION_RELEASE = "action_release";
    public final static String CUSTOMS_ACTION_SET_SURFACE = "customs_action_set_surface";
    public final static String CUSTOMS_ACTION_RETURN_CURRENT_POSITION = "customs_action_return_current_position";
    public final static String CUSTOMS_ACTION_RETURN_CURRENT_LRC = "customs_action_return_current_lrc";
}
