package com.landmark.media.interfaces;


import com.landmark.media.model.MediaData;
import com.landmark.media.model.MediaDataModel;

import java.util.List;

/**********************************************
 * Filename：
 * Author:   qiang.chen@landmark-phb.com
 * Description：
 * Date：
 * Version:
 * History:
 *------------------------------------------------------
 * Version  date      author   description
 * V0.xx  2021/8/9 10  chenqiang   1) …
 ***********************************************/
public interface IDataProvider {

    MediaData getMusicDataList(int page, int size, String type);

    MediaData getSearch(int page, int size, String type);

    MediaData getSearchList(int page, int size, String type);

    boolean addCollectList(String mediaId);

    boolean cancelCollectList(String mediaId);

    MediaData getCollectList(int page, int size);

    boolean clearCollectList();

    boolean addHistoryList(String mediaId, long currentTime, long endDuration);

    MediaData getHistoryList(int page, int size);

    MediaData getLastHistory();

    boolean clearHistoryList();


}
