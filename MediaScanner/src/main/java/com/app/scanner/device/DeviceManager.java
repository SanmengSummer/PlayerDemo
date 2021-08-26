package com.app.scanner.device;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

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
