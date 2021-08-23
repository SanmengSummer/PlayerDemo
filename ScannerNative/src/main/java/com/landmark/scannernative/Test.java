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

import android.text.TextUtils;
import android.util.Log;

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
        native_init();
    }


    public Test(CallBackAudioInfo callBack) {
        this.callBackAudioInfo = callBack;
    }

    public native static void native_init();

    public native void native_setup();

    public native String stringFromJNI();

    //java层方法的具体实现
    public void JNICallJava(String msg) {
        if (this.callBackAudioInfo != null && !TextUtils.isEmpty(msg.trim())) {
            callBackAudioInfo.callBackAudioInfo(msg);
        }
    }

    public void JNICallJavaStatus(String msg) {
        if (this.callBackAudioInfo != null && !TextUtils.isEmpty(msg.trim())) {
            callBackAudioInfo.callBackStatusInfo(msg);
        }
    }


    public interface CallBackAudioInfo {
        void callBackAudioInfo(String info);

        void callBackStatusInfo(String info);
    }

}
