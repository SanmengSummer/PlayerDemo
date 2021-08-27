package com.landmark.media.common;

import android.annotation.SuppressLint;

/**********************************************
 * Filename：
 * Author:   qiang.chen@landmark-phb.com
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.xx  2021/8/9 11  chenqiang   1) …
 ***********************************************/
public class Constants {
    @SuppressLint("SdCardPath")
    public static final String DB_DIR = "/data/data/com.landmark.media/scannerdb";
    public static final String DB_NAME = "RECORD_DB.db";
    public static final String BROADCAST_ACTION = "com.android.network.change";
    public static final String SHAREDPREFERENCE_NAME = "media";
    public static final String SHARE_DEVICE_STATUS = "share_device_status";

    public static final String ACTION_USB_UN_MOUNTED = "com.app.scanner.receiver.action_usb_un_mounted";
    public static final String ACTION_USB_MOUNTED = "com.app.scanner.receiver.action_usb_mounted";
    public static final String ACTION_USB_EXTRA_NAME = "usb_extra_path";
    public static final String ACTION_USB_EXTRA_STATUS = "usb_extra_status";
    public static final int ACTION_USB_EXTRA_STATUS_VALUE = 4;
    public static final int ACTION_USB_EXTRA_STATUS_VALUE_FINISH = 3;
    public static final int ACTION_USB_EXTRA_STATUS_VALUE_BREAK = 2;
    public static final int ACTION_USB_EXTRA_STATUS_VALUE_ING = 1;
    public static final int ACTION_USB_EXTRA_STATUS_VALUE_START = 0;
    public static final int ACTION_USB_EXTRA_STATUS_VALUE_UNLOAD = -1; //卸载了
    public static final String ACTION_SCAN_STATUS = "com.app.scanner.receiver.action_scan_status";
}
