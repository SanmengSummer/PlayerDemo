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

package com.app.scanner;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.app.scanner.db.DaoManager;
import com.app.scanner.receiver.UsbDiskReceiver;
import com.app.scanner.util.Utils;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static android.os.Environment.MEDIA_MOUNTED;
import static com.app.scanner.util.Constants.ACTION_SCAN_DEVICE;


public class ScannerService extends Service {

    private static final String TAG = "ScannerService";

    private UsbDiskReceiver mUsbReceiver;

    public static final String MOCK_IN="com.app.scanner.usb_in";
    public static final String MOCK_OUT="com.app.scanner.usb_out";
    @Override
    public void onCreate() {
        super.onCreate();

        mUsbReceiver = new UsbDiskReceiver();

        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(ACTION_MEDIA_MOUNTED);
        usbDeviceStateFilter.addAction(ACTION_MEDIA_UNMOUNTED);
        usbDeviceStateFilter.addAction(ACTION_SCAN_DEVICE);


        usbDeviceStateFilter.addDataScheme("file");

        registerReceiver(mUsbReceiver, usbDeviceStateFilter);

        Utils.copyFilesFromRaw(this,R.raw.config,"config.json", DaoManager.getInstance().getDbPath());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUsbReceiver != null) {
            unregisterReceiver(mUsbReceiver);
        }
    }

}
