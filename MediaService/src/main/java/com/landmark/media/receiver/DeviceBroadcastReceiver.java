package com.landmark.media.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.landmark.media.common.Constants;
import com.landmark.media.db.data.MediaDataHelper;

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
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constants.BROADCAST_ACTION)) {
            MediaDataHelper.getInstance(context).setDevicesStatus(true);
        }
    }
}
