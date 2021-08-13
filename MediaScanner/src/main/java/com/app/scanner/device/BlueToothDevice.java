package com.app.scanner.device;

import com.app.scanner.BaseDevice;
import com.app.scanner.DeviceTypeEnum;
import com.app.scanner.vo.MediaInfoVo;

import java.util.List;

/**********************************************
 * Filename： BlueToothDevice
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public class BlueToothDevice extends BaseDevice {
    public BlueToothDevice() {
        super(DeviceTypeEnum.BLUE_TOOTH);
    }

    @Override
    public List<MediaInfoVo> createSourceList() {
        return null;
    }
}
