package com.app.scanner.device;


import android.content.Intent;
import android.text.TextUtils;

import com.app.scanner.BaseDevice;
import com.app.scanner.CarApp;
import com.app.scanner.DeviceTypeEnum;
import com.app.scanner.db.AudioDbHelper;
import com.app.scanner.db.AudioVo;
import com.app.scanner.db.DaoManager;
import com.app.scanner.db.FolderDbHelper;
import com.app.scanner.db.FolderVo;
import com.app.scanner.db.StatusInfo;
import com.app.scanner.db.VideoDbHelper;
import com.app.scanner.db.VideoVo;
import com.app.scanner.util.LogUtils;
import com.app.scanner.vo.MediaInfoVo;
import com.landmark.scannernative.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.scanner.util.Constants.ACTION_SCAN_STATUS;
import static com.app.scanner.util.Constants.ACTION_USB_EXTRA_NAME;
import static com.app.scanner.util.Constants.ACTION_USB_EXTRA_STATUS;
import static com.app.scanner.util.LogUtils.getSymbolName;


/**********************************************
 * Filename： UsbDevice
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public class CarUsbDevice extends BaseDevice {
    private String mScanPath;
    Test test;

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
        test = new Test(CarApp.contextApp, new Test.CallBackAudioInfo() {
            @Override
            public void callBackAudioInfo(String msg) {
                List<AudioVo> list = new ArrayList<>();
                if (!TextUtils.isEmpty(msg.trim())) {
                    String result[] = msg.split("@@");
                    for (int i = 0; i < result.length; i++) {
                        if (!TextUtils.isEmpty(result[i].trim())) {

                            String tempStr[] = result[i].split(";");
                            Map<String, String> tempMap = new HashMap<>();
                            for (int j = 0; j < tempStr.length; j++) {
                                String tempResult[] = tempStr[j].split(":");
                                if (tempResult.length == 2) {
                                    tempMap.put(tempResult[0], tempResult[1]);
                                } else {
                                    tempMap.put(tempResult[0], "");
                                }
                            }

                            if ("0".equals(tempMap.get("mFileType"))) {
                                // F_TYPE_DIR = 0,
                                FolderVo folderVo = new FolderVo();
                                folderVo.setName(tempMap.get("mFileName"));
                                if (!TextUtils.isEmpty(tempMap.get("mParentPath"))) {
                                    folderVo.setParentPath(tempMap.get("mParentPath"));
                                }
                                folderVo.setPath(tempMap.get("mFilePath"));
                                folderVo.setSymbolName(getSymbolName(tempMap.get("mFileName")));
                                folderHelper.insert(folderVo);
                            } else if ("1".equals(tempMap.get("mFileType"))) {
                                //F_TYPE_AUDIO,
                                AudioVo audioVo = new AudioVo();
                                audioVo.setPath(tempMap.get("mFilePath"));
                                audioVo.setName(tempMap.get("mFileName"));
                                audioVo.setSymbolName(getSymbolName(tempMap.get("mFileName")));
                                audioVo.setSize(tempMap.get("mFileSize"));
                                audioVo.setTitle(tempMap.get("mTitle"));
                                audioVo.setAlbum(tempMap.get("mAlbum"));
                                audioVo.setSinger(tempMap.get("mComposer"));
                                audioVo.setGenre(tempMap.get("mGenre"));
                                audioVo.setFolder(tempMap.get("mParentPath"));
                                audioVo.setYear(tempMap.get("mYear"));
                                audioVo.setDuration(tempMap.get("mDuration"));

                                audioHelper.insert(audioVo);

                            } else if ("2".equals(tempMap.get("mFileType"))) {
                                //F_TYPE_VIDEO,
                                VideoVo videoVo = new VideoVo();
                                videoVo.setPath(tempMap.get("mFilePath"));
                                videoVo.setName(tempMap.get("mFileName"));
                                videoVo.setSymbolName(getSymbolName(tempMap.get("mFileName")));
                                videoVo.setFolder(tempMap.get("mParentPath"));
                                videoVo.setSize(tempMap.get("mFileSize"));

                                videoHelper.insert(videoVo);
                            }
                        }
                    }
                }
            }

            @Override
            public void callBackStatusInfo(String msg) {
                StatusInfo info = new StatusInfo();
                info.deviceId = msg.split("#")[0];
                info.status = Integer.parseInt(msg.split("#")[1]);
                if (info.status == 3) {
                    Intent childIntent = new Intent();
                    childIntent.setAction(ACTION_SCAN_STATUS);
                    childIntent.putExtra(ACTION_USB_EXTRA_NAME, mScanPath);
                    childIntent.putExtra(ACTION_USB_EXTRA_STATUS, info.status);
                    CarApp.contextApp.sendBroadcast(childIntent);

                    LogUtils.debug("stop scan data1");
//                    test.native_stop(mScanPath);
                    LogUtils.debug("stop all data2");
                }
            }

        });
    }

    public CarUsbDevice build() {

        return this;
    }

    @Override
    public List<MediaInfoVo> createSourceList() {
        return null;
    }


    public String getmScanPath() {
        return mScanPath;
    }

    public void setmScanPath(String mScanPath) {
        this.mScanPath = mScanPath;
    }

    public void scanDevice() {
        String configPath = DaoManager.getInstance().getDbPath() + "/config.json";
        String deviceId = mScanPath + "";
        String scanPath = mScanPath + "/test1";
        test.native_init(deviceId, configPath, scanPath);
    }

    public void release() {
        LogUtils.debug("clear all data");
        DaoManager.getInstance().deleteAllData();
        test.native_stop(mScanPath);

    }

}
