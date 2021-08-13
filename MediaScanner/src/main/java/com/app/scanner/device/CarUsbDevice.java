package com.app.scanner.device;

import android.content.Context;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import androidx.annotation.NonNull;
import com.app.scanner.BaseDevice;
import com.app.scanner.CarApp;
import com.app.scanner.DeviceTypeEnum;
import com.app.scanner.util.LogUtils;
import com.app.scanner.vo.MediaInfoVo;
import com.app.scanner.vo.MediaTagEnum;

import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


/**********************************************
 * Filename： UsbDevice
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public class CarUsbDevice extends BaseDevice {
    private int vendorId;

    private UsbManager mUsbManager;
    private UsbDevice mDevice;

    public CarUsbDevice(DeviceTypeEnum typeEnum) {
        super(typeEnum);
        scanDevice();
    }

    public CarUsbDevice build(UsbManager manager, UsbDevice device) {
        this.mUsbManager = manager;
        this.mDevice = device;
        return this;
    }

    private boolean checkPermission() {
        return mUsbManager.hasPermission(mDevice);
    }

    private FileSystem getDevicePath() {
        if (!checkPermission()) {
            LogUtils.debug("has no usb read permission");
            return null;
        }
        int count = mDevice.getConfigurationCount();

        for (int i = 0; i < count; i++) {
            UsbConfiguration configuration = mDevice.getConfiguration(i);
        }
        return null;
    }

    @Override
    public List<MediaInfoVo> createSourceList() {
        return null;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public void scanDevice() {

    }


}
