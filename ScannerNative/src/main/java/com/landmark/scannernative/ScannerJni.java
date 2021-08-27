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

    private static final int TYPE_LIST = 2;
    private static final int TYPE_STATUS = 1;

    static {
        System.loadLibrary("scanner_jni");
    }

    private Context mContext;

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
                if (msg.what == TYPE_STATUS) {
                    String info = (String) msg.obj;
                    if (callBackAudioInfo != null && !TextUtils.isEmpty(info.trim())) {
                        callBackAudioInfo.callBackStatusInfo(info);
                    }
                } else if (msg.what == TYPE_LIST) {
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


    //java层方法的具体实现
    public void JNICallJavaMediaList(ArrayList<MediaInfoVo> msg) {
        Message message = mHandler.obtainMessage();
        message.what = TYPE_LIST;
        message.obj = msg;
        mHandler.handleMessage(message);
    }

    public void JNICallJavaStatus(String msg) {
        Message message = mHandler.obtainMessage();
        message.what = TYPE_STATUS;
        message.obj = msg;
        mHandler.handleMessage(message);
    }


    public interface CallBackAudioInfo {
        void callBackStatusInfo(String info);

        void callBackMediaInfoList(ArrayList<MediaInfoVo> msg);
    }

}
