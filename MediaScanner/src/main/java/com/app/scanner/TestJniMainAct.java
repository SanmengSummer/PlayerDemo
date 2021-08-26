package com.app.scanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.scanner.db.AudioDbHelper;
import com.app.scanner.db.DaoManager;
import com.app.scanner.db.FolderDbHelper;
import com.app.scanner.db.VideoDbHelper;
import com.app.scanner.vo.AudioVo;
import com.app.scanner.vo.FolderVo;
import com.app.scanner.vo.VideoVo;


/**********************************************
 * Filename： TestJniMainAct
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1        wangyi   1) …
 ***********************************************/
public class TestJniMainAct extends Activity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_jni_test);
        TextView textView = findViewById(R.id.tv_file);
        TextView tv_audio = findViewById(R.id.tv_audio);
        TextView tv_clear = findViewById(R.id.tv_clear);
        TextView tv_video = findViewById(R.id.tv_video);

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


//        RootCmd.getPath();

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

        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                folderHelper.deleteAll();
                audioHelper.deleteAll();
                videoHelper.deleteAll();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(TestJniMainAct.this, "size:+" + folderHelper.queryAll().size(), Toast.LENGTH_SHORT).show();
            }
        });

        tv_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(TestJniMainAct.this, "size:+" + audioHelper.queryAll().size(), Toast.LENGTH_SHORT).show();
            }
        });

        tv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TestJniMainAct.this, "size:+" + videoHelper.queryAll().size(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
