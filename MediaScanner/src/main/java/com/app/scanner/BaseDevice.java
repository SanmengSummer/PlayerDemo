package com.app.scanner;

import android.os.AsyncTask;
import com.app.scanner.vo.MediaInfoVo;
import com.app.scanner.vo.MediaTagEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**********************************************
 * Filename： BaseDevice
 * Author:   wangyi@zlingsmart.com.cn
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.0.1           1) …
 ***********************************************/
public abstract class BaseDevice {

    private final int PAGE_NUM = 30;

    //是否正在使用
    public boolean isConnect = false;

    public DeviceTypeEnum typeEnum;

    private List<MediaInfoVo> mediaInfoVoList;

    public BaseDevice(DeviceTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public abstract List<MediaInfoVo> createSourceList();

    public List<MediaInfoVo> getMediaListByPageIndex(int pageIndex) {
        return getMediaListByTag(MediaTagEnum.TAG_LETTER, pageIndex);
    }

    public List<MediaInfoVo> getMediaListByTag(MediaTagEnum tagEnum, int pageIndex) {
        return mediaInfoVoList;
    }

    public void executeScanTask() {
        WorkTask workTask = new WorkTask();
        FutureTask<List<MediaInfoVo>> futureTask = new FutureTask<List<MediaInfoVo>>(workTask) {
            @Override
            protected void done() {
                try {
                    mediaInfoVoList = get();
                    Thread.currentThread().getName();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(futureTask);
        thread.start();
    }


    private class WorkTask implements Callable<List<MediaInfoVo>> {

        @Override
        public List<MediaInfoVo> call() throws Exception {
            //耗时操作scan数据
            List<MediaInfoVo> list = createSourceList();
            return list;
        }
    }


}
