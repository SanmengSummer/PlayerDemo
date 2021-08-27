package com.landmark.media.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.landmark.media.common.Constants;
import com.landmark.media.db.data.MediaDataHelper;
import com.landmark.media.utils.LogUtils;

/**********************************************
 * Filename：
 * Author:   qiang.chen@landmark-phb.com
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.xx  2021/8/23 11  chenqiang   1) …
 ***********************************************/
public class DeviceBroadcastReceiver extends BroadcastReceiver {

    private String TAG = "DeviceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.debug(TAG, " onReceive intent action: " + action);
        //挂载
        if (action.equals(Constants.ACTION_USB_MOUNTED) || action.equals(Constants.ACTION_SCAN_STATUS)) {
            //u盘的名字 /storage/AE3C9F0F3C9ED1A7
            String name = intent.getStringExtra(Constants.ACTION_USB_EXTRA_NAME);
            LogUtils.debug(TAG, " onReceive ACTION_USB_MOUNTED name: " + name);
            //u盘的状态 0开始 1正在扫描 2中断  3扫描完成   4 可以查询
            int status = intent.getIntExtra(Constants.ACTION_USB_EXTRA_STATUS, -1);
            LogUtils.debug(TAG, " onReceive ACTION_USB_MOUNTED status: " + status);
            if (Constants.ACTION_USB_EXTRA_STATUS_VALUE == status
                    || Constants.ACTION_USB_EXTRA_STATUS_VALUE_FINISH == status
                    || Constants.ACTION_USB_EXTRA_STATUS_VALUE_ING == status) {
                MediaDataHelper.getInstance(context).setDevicesStatus(true,
                        Constants.ACTION_USB_EXTRA_STATUS_VALUE);//可以开始查询了
            } else if (Constants.ACTION_USB_EXTRA_STATUS_VALUE_BREAK == status) {
                MediaDataHelper.getInstance(context).setDevicesStatus(false,
                        Constants.ACTION_USB_EXTRA_STATUS_VALUE_BREAK);//中断
            }
        } else if (action.equals(Constants.ACTION_USB_UN_MOUNTED)) {
            //卸载
            String name = intent.getStringExtra(Constants.ACTION_USB_EXTRA_NAME);
            LogUtils.debug(TAG, " onReceive ACTION_USB_MOUNTED name: " + name);
            MediaDataHelper.getInstance(context).setDevicesStatus(false,
                    Constants.ACTION_USB_EXTRA_STATUS_VALUE_UNLOAD);
        }
    }
}
