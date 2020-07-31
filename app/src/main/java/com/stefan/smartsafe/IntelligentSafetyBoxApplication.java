package com.stefan.smartsafe;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * @Author: Stefan Charles
 * @Date: 2020-07-31
 * @Website: www.stefancharles.cn
 * @E-mail: stefancharles@qq.com
 * @Copyright: Copyright (c) 2020 Security Plus.All rights reserved.
 **/

public class IntelligentSafetyBoxApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getInstance() {
        return mContext;
    }

}
