package com.landmark.media.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.model.MediaData;
import com.landmark.media.model.MediaDataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Author: chenhuaxia
 * Description: Utility class to help on queue related tasks.
 * Date: 2021/8/16 14:19
 **/
@SuppressLint("NewApi")
public class QueueManager {
    public static int mInitPosition = 0;
    private static MediaData mMediaData;
    private static List<MediaMetadataCompat> mMetadataLists = new ArrayList<>();
    private static List<MediaMetadataCompat> mCurrentPlayLists = new ArrayList<>();
    private static List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

    /**
     * Get a current play list (List<MediaMetadataCompat> ) after setData()
     *
     * @return List<MediaMetadataCompat>
     * @DATE 2021/8/16 @Time 14:24
     */
    public static List<MediaMetadataCompat> getCurrentPlayList() {
        return mCurrentPlayLists;
    }

    /**
     * Get a media list from MediaSession (List<MediaBrowserCompat.MediaItem>) after setData()
     *
     * @return List<MediaBrowserCompat.MediaItem>
     * @DATE 2021/8/16 @Time 14:24
     */
    public static List<MediaBrowserCompat.MediaItem> getData() {
        return mediaItems;
    }

    /**
     * Set a media list by MediaData from Database (List<MediaBrowserCompat.MediaItem>) after setData()
     *
     * @param data         (MediaData)  Entity  of media list from Database.
     * @param initPosition (int)
     * @DATE 2021/8/16 @Time 14:24
     */
    public static void setData(MediaData data, int initPosition) {
        mInitPosition = initPosition;
        mMediaData = data;
        getList();
    }

    /**
     * Get a random media list from CurrentPlayLists and update CurrentPlayLists (List<MediaMetadataCompat>) after setData()
     *
     * @return List<MediaMetadataCompat>
     * @DATE 2021/8/16 @Time 14:24
     */
    public static List<MediaMetadataCompat> getRandomPlayList() {
//        if (mMediaData == null || mediaItems == null || mediaItems.isEmpty())
//            mediaItems = getCurrentListForIDOnSearch(mContext, 0, 10, type);
        mCurrentPlayLists = new ArrayList<>(mMetadataLists);
        Collections.shuffle(mCurrentPlayLists);
        return mCurrentPlayLists;
    }

    /**
     * Get a single media list from CurrentPlayLists and update CurrentPlayLists (List<MediaMetadataCompat>) after setData()
     *
     * @param currentIndex (int) Current index in CurrentPlayLists.
     * @return List<MediaMetadataCompat>
     * @DATE 2021/8/16 @Time 14:24
     */
    public static List<MediaMetadataCompat> getSinglePlayList(int currentIndex) {
//        if (mMediaData == null || mediaItems == null || mediaItems.isEmpty())
//            mediaItems = getCurrentListForIDOnSearch(mContext, 0, 10, type);
        MediaMetadataCompat mediaMetadataCompat = mCurrentPlayLists.get(currentIndex);
        mCurrentPlayLists.clear();
        mCurrentPlayLists.add(mediaMetadataCompat);
        return mCurrentPlayLists;
    }

    /**
     * Get a order media list from CurrentPlayLists and update CurrentPlayLists (List<MediaMetadataCompat>) after setData()
     *
     * @return List<MediaMetadataCompat>
     * @DATE 2021/8/16 @Time 14:24
     */
    public static List<MediaMetadataCompat> getOrderPlayList() {
//        if (mMediaData == null || mediaItems == null || mediaItems.isEmpty())
//            mediaItems = getCurrentListForIDOnSearch(mContext, 0, 10, type);
        mCurrentPlayLists.clear();
        mCurrentPlayLists.addAll(mMetadataLists);
        return mCurrentPlayLists;
    }

    /**
     * Fetch a current media list  directly from the database and update MediaData ,
     * (List<MediaBrowserCompat.MediaItem>)
     * by MediaDataHelper(Utils class).
     *
     * @return List<MediaBrowserCompat.MediaItem>
     * @DATE 2021/8/16 @Time 14:24
     */
    public static List<MediaBrowserCompat.MediaItem>
    getCurrentListFromNameOnSearch(Context mContext, int page, int size, String type) {
        mMediaData = MediaDataHelper.getInstance(mContext).getSearch(page, size, type);
        return getList();
    }

    /**
     * Fetch a current media list  directly from the database and update MediaData ,
     * (List<MediaBrowserCompat.MediaItem>)
     * by MediaDataHelper(Utils class).
     *
     * @return List<MediaBrowserCompat.MediaItem>
     * @DATE 2021/8/16 @Time 14:24
     */
    public static List<MediaBrowserCompat.MediaItem>
    getCurrentListForIDOnSearch(Context mContext, int page, int size, String type) {
        mMediaData = MediaDataHelper.getInstance(mContext).getMusicDataList(page, size, type);
        return getList();
    }

    /**
     * Get  MediaItem list from mMediaData (Data conversion)
     *
     * @return List<MediaBrowserCompat.MediaItem>
     */
    private static List<MediaBrowserCompat.MediaItem> getList() {
        List<MediaDataModel> data = mMediaData.getData();
        data.forEach(mediaDataModel -> {
            if (mediaDataModel != null) {
                String path = "";
                if (mediaDataModel.getPath() != null)
                    path = mediaDataModel.getPath();
                String name = mediaDataModel.getName();
                String singerName = mediaDataModel.getSingerVo().getName();
                String albumName = mediaDataModel.getAlbumVo().getName();
                String genreName = mediaDataModel.getGenreVo().getName();
                Bitmap icon = mediaDataModel.getIcon();

                MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(mediaDataModel.getId()))
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, path)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, name)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, singerName)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumName)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genreName)
                        .build();
                mMetadataLists.add(metadata);
                mediaItems.add(new MediaBrowserCompat.MediaItem(
                        metadata.getDescription(),
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                ));
            }
        });
        return mediaItems;
    }
}
