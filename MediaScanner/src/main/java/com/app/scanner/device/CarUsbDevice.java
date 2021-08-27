package com.app.scanner.device;


import android.content.Intent;
import android.text.TextUtils;

import com.app.scanner.CarApp;
import com.app.scanner.db.AudioDbHelper;
import com.app.scanner.db.DaoManager;
import com.app.scanner.db.FolderDbHelper;
import com.app.scanner.db.VideoDbHelper;
import com.app.scanner.util.LogUtils;
import com.app.scanner.vo.AudioVo;
import com.app.scanner.vo.FolderVo;
import com.app.scanner.vo.MediaInfoVo;
import com.app.scanner.vo.StatusInfo;
import com.app.scanner.vo.VideoVo;
import com.landmark.scannernative.ScannerJni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.scanner.util.Constants.ACTION_SCAN_STATUS;
import static com.app.scanner.util.Constants.ACTION_USB_EXTRA_NAME;
import static com.app.scanner.util.Constants.ACTION_USB_EXTRA_STATUS;
import static com.app.scanner.util.Constants.ACTION_USB_EXTRA_STATUS_VALUE;
import static com.app.scanner.util.Constants.AUDIO_PAGE_SISE;
import static com.app.scanner.util.Utils.getSymbolName;


public class CarUsbDevice extends BaseDevice {
    private String mScanPath;
    private ScannerJni scannerJni;
    private int count = 0;

    private long time = System.currentTimeMillis();

    public CarUsbDevice(DeviceTypeEnum typeEnum) {
        super(typeEnum);
        final FolderDbHelper folderHelper = new FolderDbHelper(
                FolderVo.class,
                DaoManager.getInstance().getDaoSession().getFolderVoDao()
        );

        final AudioDbHelper audioHelper = new AudioDbHelper(
                AudioVo.class,
                DaoManager.getInstance().getDaoSession().getAudioVoDao()
        );

        final VideoDbHelper videoHelper = new VideoDbHelper(
                VideoVo.class,
                DaoManager.getInstance().getDaoSession().getVideoVoDao()
        );

        scannerJni = new ScannerJni(CarApp.contextApp, new ScannerJni.CallBackAudioInfo() {

            @Override
            public void callBackStatusInfo(String msg) {
                StatusInfo info = new StatusInfo();
                info.deviceId = msg.split("#")[0];
                info.status = Integer.parseInt(msg.split("#")[1]);
                Intent childIntent = new Intent();
                childIntent.setAction(ACTION_SCAN_STATUS);
                childIntent.putExtra(ACTION_USB_EXTRA_NAME, mScanPath);
                childIntent.putExtra(ACTION_USB_EXTRA_STATUS, info.status);
                CarApp.contextApp.sendBroadcast(childIntent);
                if (info.status == 3) {
                    time = System.currentTimeMillis() - time;
                    LogUtils.debug("scan finish ,spend time:" + time);
//                    scannerJni.native_stop(mScanPath);

                } else if (info.status == 0) {
                    time = System.currentTimeMillis();
                    LogUtils.debug("start  to scan");
                }
            }

            @Override
            public void callBackMediaInfoList(ArrayList<com.landmark.scannernative.MediaInfoVo> list) {
                for (com.landmark.scannernative.MediaInfoVo item : list) {
                    notifyToGetData();
                    if (0 == item.fileType) {
                        // F_TYPE_DIR = 0,
                        FolderVo folderVo = new FolderVo();
                        folderVo.setName(item.fileName);
                        if (!TextUtils.isEmpty(item.parentPath)) {
                            folderVo.setParentPath(item.parentPath);
                        }
                        folderVo.setPath(item.filePath);
                        folderVo.setSymbolName(getSymbolName(item.fileName));
                        folderHelper.insert(folderVo);
                    } else if (1 == item.fileType) {
                        //F_TYPE_AUDIO,
                        AudioVo audioVo = new AudioVo();
                        audioVo.setPath(item.filePath);
                        audioVo.setName(item.fileName);
                        audioVo.setSymbolName(getSymbolName(item.fileName));
                        audioVo.setSize(item.fileSize + "");
                        audioVo.setTitle(item.title);
                        audioVo.setAlbum(item.album);
                        audioVo.setSinger(item.composer);
                        audioVo.setGenre(item.genre);
                        audioVo.setFolder(item.parentPath);
                        audioVo.setYear(item.year);
                        audioVo.setDuration(item.duration + "");

                        audioHelper.insert(audioVo);

                    } else if (2 == item.fileType) {
                        //F_TYPE_VIDEO,
                        VideoVo videoVo = new VideoVo();
                        videoVo.setPath(item.filePath);
                        videoVo.setName(item.fileName);
                        videoVo.setSymbolName(getSymbolName(item.fileName));
                        videoVo.setFolder(item.parentPath);
                        videoVo.setSize(item.fileSize + "");

                        videoHelper.insert(videoVo);
                    }
                }
            }

        });
    }

    @Override
    public List<MediaInfoVo> createSourceList() {
        return null;
    }


    public String getmScanPath() {
        return mScanPath;
    }

    public CarUsbDevice setmScanPath(String mScanPath) {
        this.mScanPath = mScanPath;
        return this;
    }

    public void startScanDevice() {
        String configPath = DaoManager.getInstance().getDbPath() + "/config.json";
        String deviceId = mScanPath + "";
        String scanPath = mScanPath + "";
        scannerJni.native_init(deviceId, configPath, scanPath);
    }

    public void release() {
        DaoManager.getInstance().deleteAllData();
//        scannerJni.native_stop(mScanPath);
        LogUtils.debug("clear db & stop scan");
    }

    private void notifyToGetData() {
        count++;
        if (count == AUDIO_PAGE_SISE) {
            LogUtils.debug("notifyToGetData");
            Intent childIntent = new Intent();
            childIntent.setAction(ACTION_SCAN_STATUS);
            childIntent.putExtra(ACTION_USB_EXTRA_STATUS, ACTION_USB_EXTRA_STATUS_VALUE);
            CarApp.contextApp.sendBroadcast(childIntent);
        }

    }
}
