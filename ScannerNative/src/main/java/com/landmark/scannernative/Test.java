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
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: Test
 * @Description: java类作用描述
 * @Author: gaby
 * @Date: 8/13/21 4:33 PM
 */
public class Test {
    private static CallBackAudioInfo callBackAudioInfo;

    static {
        System.loadLibrary("scanner_jni");
    }
    Context mContext;

    private static Handler mHandler;

    private static HandlerThread mHandlerThread;

    public Test(Context context, CallBackAudioInfo callBack) {
        this.callBackAudioInfo = callBack;
        this.mContext = context;
        mHandlerThread = new HandlerThread("media_scanner_thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String info = (String) msg.obj;
                if (msg.what == 0) {
                    if (callBackAudioInfo != null && !TextUtils.isEmpty(info.trim())) {
                        callBackAudioInfo.callBackAudioInfo(info);
                    }
                } else if (msg.what == 1) {
                    if (callBackAudioInfo != null && !TextUtils.isEmpty(info.trim())) {
                        callBackAudioInfo.callBackStatusInfo(info);
                    }
                }
            }
        };

    }

    public native void native_init(String deviceId, String configPath, String scanPath);

    public native void native_stop(String deviceId);


    //java层方法的具体实现
    public void JNICallJava(String msg) {
        Message message = mHandler.obtainMessage();
        message.what = 0;
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
    }

}
