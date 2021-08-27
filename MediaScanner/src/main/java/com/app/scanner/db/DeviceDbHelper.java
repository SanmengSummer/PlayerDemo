package com.app.scanner.db;

import com.app.scanner.util.Utils;
import com.app.scanner.vo.FolderVo;
import com.app.scanner.vo.FolderVoDao;
import com.app.scanner.vo.UsbDeviceVo;
import com.app.scanner.vo.UsbDeviceVoDao;
import com.app.scanner.vo.VideoVo;
import com.app.scanner.vo.VideoVoDao;

import org.greenrobot.greendao.AbstractDao;

import java.util.List;

import static com.app.scanner.util.Utils.getSymbolName;

public class DeviceDbHelper extends DbOperationHelper<UsbDeviceVo> {

    public DeviceDbHelper(Class<UsbDeviceVo> pEntityClass, AbstractDao<UsbDeviceVo, Long> pEntityDao) {
        super(pEntityClass, pEntityDao);
    }

    @Override
    public boolean insert(UsbDeviceVo pEntity) {

        UsbDeviceVo tempVo = queryByKey(pEntity.getPath());
        if (tempVo != null) {
            pEntity.setId(tempVo.getId());
            return super.update(pEntity);
        } else {
            return super.insert(pEntity);
        }

    }

    @Override
    public UsbDeviceVo queryByKey(String key) {
        List<UsbDeviceVo> list = entityDao.queryBuilder().where(UsbDeviceVoDao.Properties.Path.eq(key)).list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
