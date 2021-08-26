/**********************************************
 * FileName: Test
 * Author: gaochengjia@zlingsmart.com.cn
 * Date: 8/13/21 4:33 PM
 * Description: 
 * History:
 *------------------------------------------------------
 * Version    date      author          description
 * V0.XX     8/13/21    gaochengjia     1)...
 ***********************************************/
package com.landmark.scannernative;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * @ClassName: Test
 * @Description: java类作用描述
 * @Author: gaby
 * @Date: 8/13/21 4:33 PM
 */
public class ScannerJni {
    private static CallBackAudioInfo callBackAudioInfo;

    static {
        System.loadLibrary("scanner_jni");
    }

    Context mContext;

    private static Handler mHandler;

    private static HandlerThread mHandlerThread;

    public ScannerJni(Context context, CallBackAudioInfo callBack) {
        this.callBackAudioInfo = callBack;
        this.mContext = context;
        mHandlerThread = new HandlerThread("media_scanner_thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0) {
                    String info = (String) msg.obj;
                    if (callBackAudioInfo != null && !TextUtils.isEmpty(info.trim())) {
                        callBackAudioInfo.callBackAudioInfo(info);
                    }
                } else if (msg.what == 1) {
                    String info = (String) msg.obj;
                    if (callBackAudioInfo != null && !TextUtils.isEmpty(info.trim())) {
                        callBackAudioInfo.callBackStatusInfo(info);
                    }
                } else if (msg.what == 2) {
                    ArrayList<MediaInfoVo> list = (ArrayList<MediaInfoVo>) msg.obj;
                    if (callBackAudioInfo != null) {
                        callBackAudioInfo.callBackMediaInfoList(list);
                    }
                }
            }
        };
    }

    public native void native_init(String deviceId, String configPath, String scanPath);

    public native void native_stop(String deviceId);

    public native ArrayList<MediaInfoVo> native_getListMedias();

    //java层方法的具体实现
    public void JNICallJava(String msg) {
        Message message = mHandler.obtainMessage();
        message.what = 0;
        message.obj = msg;
        mHandler.handleMessage(message);
    }

    //java层方法的具体实现
    public void JNICallJavaMediaList(ArrayList<MediaInfoVo> msg) {
        Message message = mHandler.obtainMessage();
        message.what = 2;
        message.obj = msg;
        mHandler.handleMessage(message);
    }

    public void JNICallJavaStatus(String msg) {
        Message message = mHandler.obtainMessage();
        message.what = 1;
        message.obj = msg;
        mHandler.handleMessage(message);
    }


    public interface CallBackAudioInfo {
        void callBackAudioInfo(String info);

        void callBackStatusInfo(String info);

        void callBackMediaInfoList(ArrayList<MediaInfoVo> msg);
    }

}
