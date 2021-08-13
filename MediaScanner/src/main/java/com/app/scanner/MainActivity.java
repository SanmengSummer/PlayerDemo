package com.app.scanner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.os.Handler;
import android.os.storage.*;

import android.util.Log;
import androidx.annotation.Nullable;

import com.app.scanner.db.*;

import com.app.scanner.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**********************************************
 * Filename： MainActivity
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
//        DeviceManager.getInstance(this);
//
//        StorageManager mStorageManager = (StorageManager) CarApp.contextApp.getSystemService(Context.STORAGE_SERVICE);
//
//        mStorageManager.registerListener(new StorageEventListener(){
//            @Override
//            public void onUsbMassStorageConnectionChanged(boolean connected) {
//                super.onUsbMassStorageConnectionChanged(connected);
//                LogUtils.err("onUsbMassStorageConnectionChanged");
//            }
//
//            @Override
//            public void onStorageStateChanged(String path, String oldState, String newState) {
//                super.onStorageStateChanged(path, oldState, newState);
//                LogUtils.err("onStorageStateChanged");
//            }
//
//            @Override
//            public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
//                super.onVolumeStateChanged(vol, oldState, newState);
//                LogUtils.err("onVolumeStateChanged");
//            }
//
//            @Override
//            public void onVolumeRecordChanged(VolumeRecord rec) {
//                super.onVolumeRecordChanged(rec);
//                LogUtils.err("onVolumeRecordChanged");
//            }
//
//            @Override
//            public void onVolumeForgotten(String fsUuid) {
//                super.onVolumeForgotten(fsUuid);
//
//                LogUtils.err("onVolumeForgotten");
//            }
//
//            @Override
//            public void onDiskScanned(DiskInfo disk, int volumeCount) {
//                super.onDiskScanned(disk, volumeCount);
//
//                LogUtils.err("onDiskScanned");
//            }
//
//            @Override
//            public void onDiskDestroyed(DiskInfo disk) {
//                super.onDiskDestroyed(disk);
//                LogUtils.err("onDiskDestroyed");
//            }
//        });


        final DbOperationHelper helper = new DbOperationHelper(AudioVo.class,
                DaoManager.getInstance().getDaoSession().getAudioVoDao());
        List<AudioVo> tempList = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            AudioVo albumVo = new AudioVo();
            albumVo.setName("1111");
            albumVo.setSymbolName("1");
            albumVo.setDuration("1");
            albumVo.setFavFlag("1");
            albumVo.setSize("1");
            albumVo.setFolderId(1l);
            albumVo.setAlbumId(1l);
            albumVo.setPath("111111");
            albumVo.setYear("1");
            tempList.add(albumVo);
        }
        long temp1 = System.currentTimeMillis();
        boolean flag = helper.insertMultiple(tempList);
        long temp2 = System.currentTimeMillis() - temp1;
        List<AudioVo> list = helper.queryAll();
        Log.d("MainActivity666", list.size() + " ;" + flag + "  ;" + temp2);

    }
}
