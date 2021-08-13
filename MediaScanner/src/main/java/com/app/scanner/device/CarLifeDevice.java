package com.app.scanner.device;

import com.app.scanner.BaseDevice;
import com.app.scanner.DeviceTypeEnum;
import com.app.scanner.vo.MediaInfoVo;

import java.util.List;

/**********************************************
 * Filename： CarLifeDevice
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public class CarLifeDevice extends BaseDevice {
    public CarLifeDevice() {
        super(DeviceTypeEnum.CAR_LIFE);
    }

    @Override
    public List<MediaInfoVo> createSourceList() {
        return null;
    }
}
