package com.stefan.smartsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.stefan.smartsafe.R;

/**
 * @Author: Stefan Charles
 * @Date: 2020-07-31
 * @Website: www.stefancharles.cn
 * @E-mail: stefancharles@qq.com
 * @Copyright: Copyright (c) 2020 Security Plus.All rights reserved.
 **/

public class SPHelper {
    private String defaultModelName;

    private static SPHelper spHelperInstant;

    private SPHelper() {

    }

    public static SPHelper getInstant(Context context) {
        if (spHelperInstant == null) {
            synchronized (SPHelper.class) {
                if (spHelperInstant == null) {
                    spHelperInstant = new SPHelper();
                    spHelperInstant.defaultModelName = context.getResources().getString(R.string.app_name);
                }
            }
        }
        return spHelperInstant;
    }

    /**
     * sharepreference方式存储
     *
     * @param context   context
     * @param modelName XML的文件名，通常取模块名
     * @param key       存储在XML中的key
     * @param value     值
     */
    public void putData2SP(Context context, String modelName, String key, Object value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(modelName, Context.MODE_PRIVATE); // 私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else {
            return;
        }
        editor.apply();
    }

    public void putData2SP(Context context, String key, Object value) {
        putData2SP(context, defaultModelName, key, value);
    }

    public String getStringFromSP(Context context, String key) {
        return getStringFromSP(context, defaultModelName, key);
    }

    public String getStringFromSP(Context context, String modelName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(modelName, Context.MODE_PRIVATE); // 私有数据
        return sharedPreferences.getString(key, "");
    }

    public Boolean getBooleanFromSP(Context context, String modelName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(modelName, Context.MODE_PRIVATE); // 私有数据
        return sharedPreferences.getBoolean(key, true);
    }
}
