package com.app.scanner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.UserManager;
import android.util.Log;
import com.app.scanner.DeviceManager;
import com.app.scanner.DeviceTypeEnum;
import com.app.scanner.device.CarUsbDevice;
import com.app.scanner.util.LogUtils;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.app.scanner.ScannerService.MOCK_IN;
import static com.app.scanner.ScannerService.MOCK_OUT;

/**********************************************
 * Filename： UsbDiskReceiver
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public class UsbDiskReceiver extends BroadcastReceiver {

    //usb被拔出
    public final String ACTION_USB_DETACHED = "com.app.scanner.receiver.action_usb_detached";
    public final String ACTION_USB_EXTRA_NAME = "usb_extra_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.debug(action);
        if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            LogUtils.debug("拔出usb vendorId:" + device.getVendorId());
            CarUsbDevice tempDevice = DeviceManager.getInstance(context).getCarUsbDeviceByType(device.getVendorId());

            if (tempDevice != null) {
                DeviceManager.getInstance(context).deleteDevice(tempDevice.typeEnum);

                Intent childIntent = new Intent();
                childIntent.setAction(ACTION_USB_DETACHED);
                childIntent.putExtra(ACTION_USB_EXTRA_NAME, tempDevice.typeEnum.getName());
                context.sendBroadcast(childIntent);
            }
        } else if (ACTION_USB_DEVICE_ATTACHED.equals(action)) {

            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                CarUsbDevice tempDevice = DeviceManager.getInstance(context).getCarUsbDeviceByType(device.getVendorId());
                LogUtils.debug("插入usb vendorId:" + device.getVendorId());
                if (tempDevice == null && DeviceManager.getInstance(context).getDeviceByType(DeviceTypeEnum.USB_1) == null) {
                    tempDevice = (CarUsbDevice) DeviceManager.getInstance(context).createDevice(DeviceTypeEnum.USB_1);
                    tempDevice.setVendorId(device.getVendorId());
                    tempDevice.build(usbManager, device);

                } else if (tempDevice == null && DeviceManager.getInstance(context).getDeviceByType(DeviceTypeEnum.USB_2) == null) {
                    tempDevice = (CarUsbDevice) DeviceManager.getInstance(context).createDevice(DeviceTypeEnum.USB_2);
                    tempDevice.setVendorId(device.getVendorId());
                    tempDevice.build(usbManager, device);
                }
            }
        }
        if (MOCK_IN.equals(action)) {
            LogUtils.debug("插入了usb");
        }
        if (MOCK_OUT.equals(action)) {
            LogUtils.debug("拔出usb");
        }
    }
}
