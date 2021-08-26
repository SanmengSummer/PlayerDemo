package com.app.scanner.vo;


/**********************************************
 * Filename： FileVo
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1        wangyi   1) …
 ***********************************************/

public class StatusInfo {
    public String deviceId;
    public int status;

    @Override
    public String toString() {
        return "StatusInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", status=" + status +
                '}';
    }
}
