package com.app.scanner;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.scanner.db.AudioDbHelper;
import com.app.scanner.db.AudioVo;
import com.app.scanner.db.DaoManager;
import com.app.scanner.db.DbOperationHelper;
import com.app.scanner.db.FolderDbHelper;
import com.app.scanner.db.FolderVo;
import com.app.scanner.db.StatusInfo;
import com.app.scanner.db.VideoDbHelper;
import com.app.scanner.db.VideoVo;
import com.landmark.scannernative.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.scanner.util.LogUtils.getSymbolName;

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

        Test test = new Test(new Test.CallBackAudioInfo() {
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
                System.out.println(info.toString());
            }

        });
        test.stringFromJNI();
    }

}
