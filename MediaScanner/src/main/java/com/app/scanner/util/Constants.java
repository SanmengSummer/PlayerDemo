package com.app.scanner.util;

public interface Constants {
    public static final String ACTION_USB_UN_MOUNTED = "com.app.scanner.receiver.action_usb_un_mounted";
    public static final String ACTION_USB_MOUNTED = "com.app.scanner.receiver.action_usb_mounted";
    public static final String ACTION_USB_EXTRA_NAME = "usb_extra_path";
    public static final String ACTION_USB_EXTRA_STATUS = "usb_extra_status";
    public static final int ACTION_USB_EXTRA_STATUS_VALUE = 4;
    public static final String ACTION_SCAN_STATUS = "com.app.scanner.receiver.action_scan_status";
    public static final String DB_NAME = "media_scanner.db";
    public static final String DB_FOLDER = "scannerdb";
    public static final int AUDIO_PAGE_SISE = 20;
    public static final String ACTION_SCAN_DEVICE="com.app.scanner.receiver.action_scan_device";
    public static final String ACTION_SCAN_EXTRA_NAME="scan_usb_extra_path";
}
