package com.landmark.media.utils;

import com.landmark.media.model.MediaDataModel;

import java.util.ArrayList;
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
 * V0.xx  2021/8/23 10  chenqiang   1) …
 ***********************************************/
public class SortUtils {
    public static List<MediaDataModel> SortByLength(List<MediaDataModel> filterList, String[][] strs, int size) {
        //要检索字符串长度
        for (int i = 0; i < size; i++) {
            //该行字符串长度
            int minlen = strs[i][1].length();
            int min = Integer.parseInt(strs[i][2]);
            String str = strs[i][1];
            int minindex = i;
            String sortindex = strs[i][0];
            for (int j = i + 1; j < strs.length; j++) {
                //if(str.indexOf(filter)==tempstr.indexOf(filter)&&a[j][0].length()<minlen){
                //如果索引相同
                if (strs[i][2].equals(strs[j][2])) {
                    //如果剩下的字符串的长度小于上一个字符串的长度
                    if (strs[j][1].length() < strs[minindex][1].length()) {
                        minlen = strs[j][1].length();
                        min = Integer.parseInt(strs[j][2]);
                        str = strs[j][1];
                        sortindex = strs[j][0];
                        minindex = j;
                    }
                }
            }
            if (i != minindex) {
                //交换索引位置
                strs[minindex][2] = strs[i][2];
                strs[i][2] = String.valueOf(min);
                //交换字符串位置
                strs[minindex][1] = strs[i][1];
                strs[i][1] = str;
                //交换次数位置
                strs[minindex][0] = strs[i][0];
                strs[i][0] = sortindex;
            }
        }
        List<MediaDataModel> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(filterList.get(Integer.parseInt(strs[i][0])));
        }
        return list;
    }

    /**
     * 根据关键字的索引带大小进行排序
     *
     * @param filterList 包含关键字的list字符数组
     * @param filter     关键字
     * @return 三维字符数组，第一位是原始顺序，第二维是字符串，第三维是索引
     */
    public static String[][] SortByIndex(List<MediaDataModel> filterList, String filter) {
        int size = filterList.size();
        String[][] strs = new String[size][3];
        for (int i = 0; i < size; i++) {
            //查询到的结果集的第i条数据
            String str = filterList.get(i).getName().toString();
            int index = str.indexOf(filter);
            strs[i][0] = String.valueOf(i);
            strs[i][2] = String.valueOf(index);
            strs[i][1] = str;
        }
        for (int i = 0; i < strs.length - 1; i++) {
            //临时交换顺序
            String sortindex = strs[i][0];
            //临时交换字符串
            String str = strs[i][1];
            //临时交换索引用于比较
            int min = Integer.parseInt(strs[i][2]);
            //用来比较索引大小
            int minindex = i;
            for (int j = i + 1; j < strs.length; j++) {
                if (Integer.parseInt(strs[j][2]) < min) {
                    min = Integer.parseInt(strs[j][2]);
                    str = strs[j][1];
                    sortindex = strs[j][0];
                    minindex = j;
                }
            }
            if (i != minindex) {
                //交换索引位置
                strs[minindex][2] = strs[i][2];
                strs[i][2] = String.valueOf(min);
                //交换字符串位置
                strs[minindex][1] = strs[i][1];
                strs[i][1] = str;
                //交换次数位置
                strs[minindex][0] = strs[i][0];
                strs[i][0] = sortindex;
            }
        }
        return strs;
    }
}
