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

    private SparseArray<BaseDevice> sourceArray = new SparseArray<>();

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

    public BaseDevice createDevice(DeviceTypeEnum typeEnum) {
        BaseDevice deviceInfo = null;
        if (typeEnum == DeviceTypeEnum.USB_1) {
            deviceInfo = new CarUsbDevice(DeviceTypeEnum.USB_1);
        }
        if (typeEnum == DeviceTypeEnum.USB_2) {
            deviceInfo = new CarUsbDevice(DeviceTypeEnum.USB_2);
        }
        if (typeEnum == DeviceTypeEnum.CAR_PLAY) {
            deviceInfo = new CarPlayDevice();
        }
        if (typeEnum == DeviceTypeEnum.CAR_LIFE) {
            deviceInfo = new CarLifeDevice();
        }
        if (typeEnum == DeviceTypeEnum.BLUE_TOOTH) {
            deviceInfo = new BlueToothDevice();
        }

        sourceArray.put(typeEnum.getIndex(), deviceInfo);

        return deviceInfo;
    }


    public SparseArray<BaseDevice> getDeviceList() {
        return sourceArray;
    }

    public synchronized BaseDevice getCurrentPlayDevice() {
        for (int i = 0; i < sourceArray.size(); i++) {
            if (sourceArray.valueAt(i).isConnect) {
                return sourceArray.valueAt(i);
            }
        }
        return null;
    }

    public synchronized void changePlayDevice(DeviceTypeEnum typeEnum) {
        for (int i = 0; i < sourceArray.size(); i++) {
            if (sourceArray.valueAt(i).typeEnum.getIndex() == typeEnum.getIndex()) {
                sourceArray.valueAt(i).isConnect = true;
            } else {
                sourceArray.valueAt(i).isConnect = false;
            }
        }
    }

    public BaseDevice getDeviceByType(DeviceTypeEnum typeEnum) {
//        if (sourceArray.contains(typeEnum.getIndex())) {
//            return sourceArray.get(typeEnum.getIndex());
//        }
        return null;
    }

    public synchronized CarUsbDevice getCarUsbDeviceByType(int vendorId) {
        if (getDeviceByType(DeviceTypeEnum.USB_1) != null && ((CarUsbDevice) getDeviceByType(DeviceTypeEnum.USB_1)).getVendorId() == vendorId) {
            return (CarUsbDevice) getDeviceByType(DeviceTypeEnum.USB_1);
        } else if (getDeviceByType(DeviceTypeEnum.USB_2) != null && ((CarUsbDevice) getDeviceByType(DeviceTypeEnum.USB_2)).getVendorId() == vendorId) {
            return (CarUsbDevice) getDeviceByType(DeviceTypeEnum.USB_2);
        }
        return null;
    }

    public synchronized void deleteDevice(DeviceTypeEnum typeEnum) {
        if (getDeviceByType(typeEnum) != null) {
            sourceArray.delete(typeEnum.getIndex());
        }
    }
}
