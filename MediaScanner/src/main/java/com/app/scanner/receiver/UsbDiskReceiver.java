package com.app.scanner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.app.scanner.db.DaoManager;
import com.app.scanner.db.DeviceDbHelper;
import com.app.scanner.db.VideoDbHelper;
import com.app.scanner.device.DeviceManager;
import com.app.scanner.device.CarUsbDevice;
import com.app.scanner.util.LogUtils;
import com.app.scanner.util.Utils;
import com.app.scanner.vo.UsbDeviceVo;
import com.app.scanner.vo.VideoVo;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.content.Intent.ACTION_MEDIA_UNMOUNTED;
import static com.app.scanner.util.Constants.ACTION_SCAN_DEVICE;
import static com.app.scanner.util.Constants.ACTION_SCAN_EXTRA_NAME;
import static com.app.scanner.util.Constants.ACTION_USB_EXTRA_NAME;
import static com.app.scanner.util.Constants.ACTION_USB_MOUNTED;
import static com.app.scanner.util.Constants.ACTION_USB_UN_MOUNTED;

public class UsbDiskReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_MEDIA_UNMOUNTED.equals(action)) {
            String mountPath = "";
            Uri uri = intent.getData();
            if (uri.getScheme().equals("file")) {
                mountPath = uri.getPath();
            }
            LogUtils.debug("拔出usb path:" + mountPath);
            CarUsbDevice tempDevice = DeviceManager.getInstance(context).getCarUsbDeviceByType(mountPath);
            if (tempDevice != null) {
                tempDevice.release();
                DeviceManager.getInstance(context).deleteDevice(mountPath);
                Intent childIntent = new Intent();
                childIntent.setAction(ACTION_USB_UN_MOUNTED);
                childIntent.putExtra(ACTION_USB_EXTRA_NAME, mountPath);
                context.sendBroadcast(childIntent);
            } else {
                LogUtils.debug("tempDevice: null");
            }

        }
        if (ACTION_MEDIA_MOUNTED.equals(action)) {
            String mountPath = intent.getData().getPath();
            CarUsbDevice tempDevice = DeviceManager.getInstance(context).getCarUsbDeviceByType(mountPath);
            LogUtils.debug("插入了usb path:" + mountPath);
            if (tempDevice == null) {
                saveDevice(mountPath);
                tempDevice = (CarUsbDevice) DeviceManager.getInstance(context).createDevice(mountPath);
                tempDevice.setmScanPath(mountPath).startScanDevice();

                Intent childIntent = new Intent();
                childIntent.setAction(ACTION_USB_MOUNTED);
                childIntent.putExtra(ACTION_USB_EXTRA_NAME, mountPath);
                context.sendBroadcast(childIntent);
            }
        }
        if (ACTION_SCAN_DEVICE.equals(action)) {
            String mountPath = intent.getStringExtra(ACTION_SCAN_EXTRA_NAME);
            CarUsbDevice tempDevice = DeviceManager.getInstance(context).getCarUsbDeviceByType(mountPath);
            LogUtils.debug("scan device from app, path:" + mountPath);
            if (tempDevice == null) {
                saveDevice(mountPath);
                tempDevice = (CarUsbDevice) DeviceManager.getInstance(context).createDevice(mountPath);
                tempDevice.setmScanPath(mountPath).startScanDevice();
            }
        }
    }


    private void saveDevice(String path) {

        final DeviceDbHelper videoHelper = new DeviceDbHelper(
                UsbDeviceVo.class,
                DaoManager.getInstance().getDaoSession().getUsbDeviceVoDao()
        );
        UsbDeviceVo deviceVo = new UsbDeviceVo();
        deviceVo.setPath(path);
        videoHelper.insert(deviceVo);
    }
}
