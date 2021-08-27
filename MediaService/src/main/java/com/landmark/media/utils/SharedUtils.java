package com.landmark.media.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
 * V0.xx  2021/8/26 11  chenqiang   1) …
 ***********************************************/
public class SharedUtils {

    private static SharedPreferences mSp;

    private static SharedUtils sInstance;

    public static synchronized SharedUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SharedUtils();
        }
        if (mSp == null) {
            mSp = context.getSharedPreferences(Constants.SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        return sInstance;
    }

    public void setParam(String key, Object object) {
        String type = object.getClass().getSimpleName();
        SharedPreferences.Editor editor = mSp.edit();
        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }

    public Object getParam(String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        if ("String".equals(type)) {
            return mSp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return mSp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return mSp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return mSp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return mSp.getLong(key, (Long) defaultObject);
        }

        return null;
    }


    /**
     * 清除所有数据
     *
     * @param context
     */
    public void clear() {
        SharedPreferences.Editor editor = mSp.edit();
        editor.clear().commit();
    }

    /**
     * 清除指定数据
     *
     * @param context
     */
    public void clearAll() {
        SharedPreferences.Editor editor = SharedUtils.mSp.edit();
        editor.remove("定义的键名");
        editor.commit();
    }
}
