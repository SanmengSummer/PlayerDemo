package com.app.scanner;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.util.SparseArray;
import com.app.scanner.device.BlueToothDevice;
import com.app.scanner.device.CarLifeDevice;
import com.app.scanner.device.CarPlayDevice;
import com.app.scanner.device.CarUsbDevice;
import com.app.scanner.receiver.UsbDiskReceiver;

import java.util.HashMap;
import java.util.Map;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

/**********************************************
 * Filename： DeviceManager
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public class DeviceManager {
    public static DeviceManager mInstance = null;

    private Context mContext;

    private Map<String,BaseDevice> sourceArray = new HashMap<>();

    private DeviceManager(Context context) {
        this.mContext = context;
    }

    public static DeviceManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DeviceManager.class) {
                if (mInstance == null) {
                    mInstance = new DeviceManager(context);
                }
            }
        }
        return mInstance;
    }

    public void destroy() {

    }

    public BaseDevice createDevice(String path) {
        BaseDevice deviceInfo = null;
        if (sourceArray.get(path)==null) {
            deviceInfo = new CarUsbDevice(DeviceTypeEnum.USB_1);
        }
        sourceArray.put(path, deviceInfo);

        return deviceInfo;
    }


    public synchronized CarUsbDevice getCarUsbDeviceByType(String path) {
        if (sourceArray.get(path)!=null) {
            return (CarUsbDevice) sourceArray.get(path);
        }
         return null;
    }

    public synchronized void deleteDevice(String path) {
        sourceArray.remove(path);
    }
}
