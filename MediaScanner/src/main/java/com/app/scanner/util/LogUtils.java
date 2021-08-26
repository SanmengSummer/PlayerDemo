/*
 *
 * Copyright (C) 2021 Galaxy Auto Technology
 *
 *  All Rights Reserved by Galaxy Auto Technology Co., Ltd and its affiliates.
 *  You may not use, copy, distribute, modify, transmit in any form this file
 *  except in compliance with Galaxy Auto Technology in writing by applicable law.
 *
 *
 *  Edit History
 *
 *  DATE NAME DESCRIPTION
 *  2021-06-16 wangyi Add timeSync service.
 *
 */

package com.app.scanner.util;

import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;

public class LogUtils {
    public static final String TAG = "carScanner";

    public static void debug(String str, String str2) {
        Log.d(TAG, "@" + str + "@: " + str2);
    }

    public static void err(String str, String str2) {
        Log.e(TAG, "@" + str + "@: " + str2);
    }

    public static void info(String str, String str2) {
        Log.i(TAG, "@" + str + "@: " + str2);
    }

    public static void warning(String str, String str2, Throwable th) {
        Log.w(TAG, "@" + str + "@: " + str2, th);
    }

    public static void debug(String str2) {
        Log.d(TAG, "@: " + str2);
    }

    public static void err( String str2) {
        Log.e(TAG, "@: " + str2);
    }

    public static void info( String str2) {
        Log.i(TAG, "@: " + str2);
    }

    public static void warning( String str2, Throwable th) {
        Log.w(TAG,  "@: " + str2, th);
    }

}
