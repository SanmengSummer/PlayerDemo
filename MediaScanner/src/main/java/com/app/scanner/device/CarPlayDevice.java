package com.app.scanner.device;

import com.app.scanner.BaseDevice;
import com.app.scanner.DeviceTypeEnum;
import com.app.scanner.vo.MediaInfoVo;

import java.util.List;

/**********************************************
 * Filename： CarPlayDevice
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public class CarPlayDevice extends BaseDevice {
    public CarPlayDevice() {
        super(DeviceTypeEnum.CAR_PLAY);
    }

    @Override
    public List<MediaInfoVo> createSourceList() {
        return null;
    }
}
