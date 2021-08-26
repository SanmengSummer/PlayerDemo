package com.landmark.media.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.landmark.media.R;
import com.landmark.media.controller.bean.MediaInfoBean;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.model.MediaData;
import com.landmark.media.model.MediaDataModel;
import com.landmark.media.controller.utils.LogUtils;
import com.landmark.media.controller.utils.MP3ID3v2.MP3ReadID3v2;
import com.landmark.media.controller.utils.UriToPathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to help on queue related tasks.
 */

@SuppressLint("NewApi")
public class QueueManager {
    public static int mInitPosition = 0;
    private static MediaData mMediaData;
    private static List<MediaMetadataCompat> mMetadataLists = new ArrayList<>();
    private static List<MediaMetadataCompat> mCurrentPlayLists = new ArrayList<>();
    private static List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

    public static List<MediaMetadataCompat> getCurrentPlayList() {
        return mCurrentPlayLists;
    }

    public static List<MediaBrowserCompat.MediaItem> getCurrentListFromNameOnSearch(Context mContext, int page, int size, String type) {
        mMediaData = MediaDataHelper.getInstance(mContext).getSearch(page, size, type);
        return getList(mContext);
    }

    public static List<MediaBrowserCompat.MediaItem> getCurrentListForIDOnSearch(Context mContext, int page, int size, String type) {
        mMediaData = MediaDataHelper.getInstance(mContext).getMusicDataList(page, size, type);
        return getList(mContext);
    }

    public static List<MediaBrowserCompat.MediaItem> getCurrentListForNoneOnSearch() {
        return mediaItems;
    }

    public static List<MediaBrowserCompat.MediaItem> getData() {
        return mediaItems;
    }

    public static void setData(Context mContext, MediaData data, int initPosition) {
        mInitPosition = initPosition;
        mMediaData = data;
        getList(mContext);
    }

    private static List<MediaBrowserCompat.MediaItem> getList(Context mContext) {
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


    public static List<MediaMetadataCompat> getRandomPlayList() {
//        if (mMediaData == null || mediaItems == null || mediaItems.isEmpty())
//            mediaItems = getCurrentListForIDOnSearch(mContext, 0, 10, type);
        mCurrentPlayLists = new ArrayList<>(mMetadataLists);
        Collections.shuffle(mCurrentPlayLists);
        return mCurrentPlayLists;
    }

    public static List<MediaMetadataCompat> getSinglePlayList(int currentIndex) {
//        if (mMediaData == null || mediaItems == null || mediaItems.isEmpty())
//            mediaItems = getCurrentListForIDOnSearch(mContext, 0, 10, type);
        MediaMetadataCompat mediaMetadataCompat = mCurrentPlayLists.get(currentIndex);
        mCurrentPlayLists.clear();
        mCurrentPlayLists.add(mediaMetadataCompat);
        return mCurrentPlayLists;
    }

    public static List<MediaMetadataCompat> getOrderPlayList() {
//        if (mMediaData == null || mediaItems == null || mediaItems.isEmpty())
//            mediaItems = getCurrentListForIDOnSearch(mContext, 0, 10, type);
        mCurrentPlayLists.clear();
        mCurrentPlayLists.addAll(mMetadataLists);
        return mCurrentPlayLists;
    }

}
