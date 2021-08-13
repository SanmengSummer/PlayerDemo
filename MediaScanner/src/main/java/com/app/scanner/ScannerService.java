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
import com.app.scanner.receiver.UsbDiskReceiver;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;


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
        usbDeviceStateFilter.addAction(ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(ACTION_USB_DEVICE_DETACHED);
        usbDeviceStateFilter.addAction(MOCK_IN);
        usbDeviceStateFilter.addAction(MOCK_OUT);

//        usbDeviceStateFilter.addDataScheme("file");

        registerReceiver(mUsbReceiver, usbDeviceStateFilter);
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
