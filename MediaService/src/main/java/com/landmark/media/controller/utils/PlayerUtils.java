package com.landmark.media.controller.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.landmark.media.PlayActivity;
import com.landmark.media.R;
import com.landmark.media.controller.bean.MediaInfoBean;
import com.landmark.media.controller.utils.MP3ID3v2.MP3ReadID3v2;
import com.landmark.media.db.table.AlbumVo;
import com.landmark.media.db.table.GenreVo;
import com.landmark.media.db.table.SingerVo;
import com.landmark.media.model.MediaData;

/**
 * Author: chenhuaxia
 * Description: Player utils(Intent to PlayerActivity)
 * Date: 2021/8/18 15:33
 **/
public class PlayerUtils {
    public final static String PLAYER_FOR_INDEX = "player_for_index";
    public static MediaData data;

    public static MediaData getData() {
        return data;
    }

    public static void startPlayer(Context context, MediaData data, int position) {
        supplementData(context, data);
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(PLAYER_FOR_INDEX, position);
        context.startActivity(intent);
    }

    private static void supplementData(Context context, MediaData data) {
        data.getData().forEach(mediaDataModel -> {
            if (mediaDataModel.getPath() != null) {
                MediaInfoBean mediaInfo = getMediaInfo(context, UriToPathUtil.getUri(mediaDataModel.getPath()));
                if (mediaDataModel.getName() == null || mediaDataModel.getName().isEmpty())
                    mediaDataModel.setName(mediaInfo.getMediaTitle());

                if (mediaDataModel.getSingerVo() == null) {
                    SingerVo singerVo = new SingerVo();
                    singerVo.setId(0L);
                    singerVo.setName(mediaInfo.getMediaArtist());
                    mediaDataModel.setSingerVo(singerVo);
                } else if (mediaDataModel.getSingerVo().getName().isEmpty())
                    mediaDataModel.getSingerVo().setName(mediaInfo.getMediaArtist());

                if (mediaDataModel.getAlbumVo() == null) {
                    AlbumVo albumVo = new AlbumVo();
                    albumVo.setName(mediaInfo.getMediaAlbum());
                    albumVo.setId(0L);
                    mediaDataModel.setAlbumVo(albumVo);
                } else if (mediaDataModel.getAlbumVo().getName().isEmpty())
                    mediaDataModel.getAlbumVo().setName(mediaInfo.getMediaAlbum());

                if (mediaDataModel.getGenreVo() == null) {
                    GenreVo genreVo = new GenreVo();
                    genreVo.setName(mediaInfo.getMediaGenre());
                    mediaDataModel.setGenreVo(genreVo);
                } else if (mediaDataModel.getGenreVo().getName().isEmpty())
                    mediaDataModel.getGenreVo().setName(mediaInfo.getMediaGenre());
                mediaDataModel.setIcon(mediaInfo.getMediaIconBitmap());
            }
        });
        PlayerUtils.data = data;
    }

    private static MediaInfoBean getMediaInfo(Context mContext, Uri mediaUri) {
        MediaInfoBean mediaInfoBean = new MediaInfoBean();
        Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getApplicationContext().getResources(), R.drawable.play_img_default);
        mediaInfoBean.setMediaIconBitmap(bitmap1);
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mContext, mediaUri);
            mediaInfoBean.setMediaTitle(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            mediaInfoBean.setMediaAlbum(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            mediaInfoBean.setMediaArtist(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            mediaInfoBean.setMediaGenre(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));

            if (mediaUri.toString().contains(".mp4") || mediaUri.toString().contains(".mkv")) {
                int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//时长(毫秒)
                if (duration >= 10 * 1000) {
                    Bitmap bitmap = retriever.getFrameAtTime(10 * 1000L, MediaMetadataRetriever.OPTION_CLOSEST);
                    if (bitmap != null)
                        mediaInfoBean.setMediaIconBitmap(bitmap);
                    else {
                        LogUtils.debug("bitmap == null");
                    }
                } else {
                    LogUtils.debug("the time is out of video");
                }
            } else {
//                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                retriever.setDataSource(this, mediaUri);
//                LogUtils.debug("getAuthor:" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
//                LogUtils.debug("123:" + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));

//                MetaInfoParser_MP3 metaInfoParser_mp3 = new MetaInfoParser_MP3();
//                metaInfoParser_mp3.parse(UriToPathUtil.getRealFile(this, mediaUri));
//                LogUtils.debug("getArtist: " + metaInfoParser_mp3.getArtist());
//                LogUtils.debug("getTitle: " + metaInfoParser_mp3.getTitle());
//                LogUtils.debug("getLrc: " + metaInfoParser_mp3.getLrc());

//                LogUtils.debug("getLrc: " + mp3ReadId3v2.getLrc());
//                LogUtils.debug("getAuthor: " + mp3ReadId3v2.getAuthor());
//                LogUtils.debug("getName: " + mp3ReadId3v2.getName());
//                LogUtils.debug("getSpecial: " + mp3ReadId3v2.getSpecial());
                MP3ReadID3v2 mp3ReadId3v2 = new MP3ReadID3v2(UriToPathUtil.getRealFile(mContext, mediaUri));
                byte[] img = mp3ReadId3v2.getImg();
                if (img != null && img.length != 0) {
                    mediaInfoBean.setMediaIconBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaInfoBean;
    }
}
