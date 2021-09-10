package com.landmark.media.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.landmark.media.R
import com.landmark.media.controller.MediaImpl.MediaPlayerManager
import com.landmark.media.controller.MediaImpl.MediaPlayerManager.MediaListDataChangeCallback
import com.landmark.media.controller.utils.LogUtils
import com.landmark.media.controller.utils.LrcProcess.LrcContent
import com.landmark.media.controller.utils.PlayerUtils
import com.landmark.media.db.data.MediaDataHelper
import com.landmark.media.db.data.MediaIDHelper
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.item.*
import java.lang.Exception
import java.util.ArrayList

class PlayActivity : AppCompatActivity() {
    private var instance: MediaPlayerManager? = null

    private var mediaItemList: ArrayList<MediaBrowserCompat.MediaItem?>? = null
    private var initIndex = 0

    companion object {
        private var playMode = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        seek.setOnSeekBarChangeListener(onSeekBarChangeListener)
        initPlayer()
        MediaDataHelper.getInstance(this)
            .registerDeviceListener { status: Boolean, actionUsbExtraStatusValue: Int ->
                if (!status || actionUsbExtraStatusValue == -1) {
                    Toast.makeText(
                        this,
                        "U盘加载失败！没有播放数据！",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        instance!!.connect()
    }

    override fun onStop() {
        super.onStop()
        instance!!.disconnect()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val data = PlayerUtils.getData()
        val mediaDataModel = data.data[initIndex]
        val iconBitmap = mediaDataModel.icon
        val title: CharSequence = mediaDataModel.name
        val artist: CharSequence = mediaDataModel.singerVo.name
        val album: CharSequence = mediaDataModel.albumVo.name
        val genre: CharSequence = mediaDataModel.genreVo.name
        image!!.setImageBitmap(iconBitmap)
        textView!!.text = """
            title: $title
            artist: $artist
            album: $album
            genre: $genre
            """.trimIndent()
    }

    private fun initPlayer() {
        matchPlayMode()
        initIndex = intent.getIntExtra(PlayerUtils.PLAYER_FOR_INDEX, 0)
        requestPermissions(101)
        instance = MediaPlayerManager.getInstance()
        instance!!.setData(initIndex)
        initView()
        instance!!.connectMediaSession(this, MediaIDHelper.MEDIA_ID_ROOT)
        instance!!.setOnRegisterSessionCallback { list: List<MediaBrowserCompat.MediaItem?>? ->
            mediaItemList = list as ArrayList<MediaBrowserCompat.MediaItem?>?
            val surfaceView =
                findViewById<SurfaceView>(R.id.surfaceView)
            instance!!.setSurfaceView(surfaceView)
            instance!!.transportControls.playFromSearch(MediaConfig.MEDIA_PLAYER_LIST, null)

            instance!!.setOnMediaListDataChangeCallback(object : MediaListDataChangeCallback {
                override fun getMediaListDataChangeCallback(
                    currentPosition: Long,
                    mLrcContent: LrcContent
                ) {
                    updateSeekBar(currentPosition)
                    if (mLrcContent.lrc_time != -1) text_title.text =
                        mLrcContent.lrc else text_title.text = ""
                }

                @SuppressLint("SetTextI18n")
                override fun updateCurrentMedia(
                    children: List<MediaMetadataCompat>?,
                    index: Int
                ) {
                    val mediaMetadataCompat = children!![index]
                    val iconBitmap =
                        mediaMetadataCompat.description.iconBitmap
                    val title =
                        mediaMetadataCompat.description.title
                    val artist: CharSequence =
                        mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                    val album: CharSequence =
                        mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
                    val genre: CharSequence =
                        mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_GENRE)
                    image.setImageBitmap(iconBitmap)
                    textView!!.text = """
                        title: $title
                        artist: $artist
                        album: $album
                        genre: $genre
                        """.trimIndent()
                }
            })
        }
    }

    private var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                LogUtils.debug("MediaSessionCompat  onStopTrackingTouch: ")
                val playbackState = instance!!.playbackState
                if (playbackState == null || playbackState.state == PlaybackStateCompat.STATE_STOPPED)
                    instance!!.transportControls.playFromSearch(
                        MediaConfig.MEDIA_PLAYER_LIST,
                        null
                    )

                assert(playbackState != null)
                val extras = playbackState!!.extras!!
                val duration = extras.getLong(MediaConfig.STATE_DURATION)
                val position = seekBar.progress * duration / 100
                instance!!.transportControls.seekTo(position)
            }
        }

    fun start(view: View?) {
        if (instance!!.playbackState.state == PlaybackStateCompat.STATE_PLAYING)
            instance!!.transportControls.stop()
        instance!!.transportControls.playFromSearch(MediaConfig.MEDIA_PLAYER_LIST, null)
    }

    fun play_pause(view: View?) {
        if (instance!!.playbackState.state == PlaybackStateCompat.STATE_PLAYING)
            instance!!.transportControls.pause()
        else instance!!.transportControls.play()
    }

    fun stop(view: View?) {
        instance!!.transportControls.stop()
    }

    fun play_mode(v: View?) {
        playMode++
        setPlayMode()
    }

    private fun setPlayMode() {
        when (playMode) {
            1 -> {
                play_mode.text = "随机播放"
                instance!!.setPlayerMode(QueueManager.random, false)
            }
            2 -> {
                play_mode.text = "单曲播放"
                instance!!.setPlayerMode(QueueManager.single, false)
            }
            3 -> {
                play_mode.text = "顺序循环"
                instance!!.setPlayerMode(QueueManager.order, true)
            }
            4 -> {
                play_mode.text = "随机循环"
                instance!!.setPlayerMode(QueueManager.random, true)
            }
            5 -> {
                play_mode.text = "单曲循环"
                instance!!.setPlayerMode(QueueManager.single, true)
            }
            6, 0 -> {
                playMode = 0
                play_mode.text = "顺序播放"
                instance!!.setPlayerMode(QueueManager.order, false)
            }
        }
    }

    private fun matchPlayMode() {
        when (QueueManager.mMode) {
            QueueManager.order -> {
                playMode = if (QueueManager.isLoop) {
                    play_mode.text = "顺序循环"
                    3
                } else {
                    play_mode.text = "顺序播放"
                    0
                }
            }
            QueueManager.random -> {
                playMode = if (QueueManager.isLoop) {
                    play_mode.text = "随机播放"
                    4
                } else {
                    play_mode.text = "随机循环"
                    1
                }
            }
            QueueManager.single -> {
                playMode = if (QueueManager.isLoop) {
                    play_mode.text = "单曲播放"
                    5
                } else {
                    play_mode.text = "单曲循环"
                    2
                }
            }
        }
    }

    private fun updateSeekBar(position: Long) {
        val playbackState = instance!!.playbackState
        val extras = playbackState.extras
        var duration: Long = 0
        if (extras != null) duration = extras.getLong(MediaConfig.STATE_DURATION)
        val p = if (duration == 0L) 0 else position * 100 / duration
        seek.progress = p.toInt()
    }

    fun skipNext(view: View?) {
        instance!!.transportControls.skipToNext()
    }

    fun skipPrevious(view: View?) {
        instance!!.transportControls.skipToPrevious()
    }

    fun rewind(view: View?) {
        instance!!.transportControls.rewind()
    }

    fun fastForward(view: View?) {
        instance!!.transportControls.fastForward()
    }

    fun collect(view: View?) {
        addCollect()
    }

    fun cancel_collect(view: View?) {
        cancelCollect()
    }

    /**
     * Author: chenhuaxia
     * Description:Add Collect by MediaDataHelper .
     */
    fun addCollect() {
        val b = MediaDataHelper.getInstance(this)
            .addCollectList(mediaItemList!![QueueManager.currentPlayIndex]?.mediaId)
        Toast.makeText(this, if (b) "收藏成功！" else "收藏失败！", Toast.LENGTH_SHORT).show()
    }

    /**
     * Author: chenhuaxia
     * Description:Cancel Collect by MediaDataHelper .
     */
    fun cancelCollect() {
        val b = MediaDataHelper.getInstance(this)
            .cancelCollectList(mediaItemList!![QueueManager.currentPlayIndex]?.mediaId)
        Toast.makeText(this, if (b) "取消收藏成功！" else "取消收藏失败！", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NewApi")
    private fun requestPermissions(requestCode: Int) {
        try {
            val requestPrecessionArr = ArrayList<String>()
            val hasSdcardWrite = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (hasSdcardWrite != PackageManager.PERMISSION_GRANTED) requestPrecessionArr.add(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val hasSystemAlertWindow = checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
            if (hasSystemAlertWindow != PackageManager.PERMISSION_GRANTED) requestPrecessionArr.add(
                Manifest.permission.SYSTEM_ALERT_WINDOW
            )
            if (requestPrecessionArr.size >= 1) {
                val requestArray = arrayOfNulls<String>(requestPrecessionArr.size)
                for (i in requestArray.indices) {
                    requestArray[i] = requestPrecessionArr[i]
                }
                requestPermissions(requestArray, requestCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        instance!!.release()
        super.onDestroy()
    }
}