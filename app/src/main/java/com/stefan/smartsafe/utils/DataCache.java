package com.stefan.smartsafe.utils;

import android.content.Context;
import android.text.TextUtils;

import com.stefan.smartsafe.Constant;

/**
 * @Author: Stefan Charles
 * @Date: 2020-07-31
 * @Website: www.stefancharles.cn
 * @E-mail: stefancharles@qq.com
 * @Copyright: Copyright (c) 2020 Security Plus.All rights reserved.
 **/

public class DataCache {
    private static String baseUrl;
    private static String userName;
    private static String pwd;
    private static String accessToken;
    private static String gateWayTag;


    public static String getBaseUrl(Context context) {
        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = SPHelper.getInstant(context).getStringFromSP(context, Constant.BASE_URL);
        }
        return baseUrl;
    }

    public static void updateBaseUrl(Context context, String value) {
        baseUrl = value;
        SPHelper.getInstant(context).putData2SP(context, Constant.BASE_URL, value);
    }

    public static String getUserName(Context context) {
        if (TextUtils.isEmpty(userName)) {
            userName = SPHelper.getInstant(context).getStringFromSP(context, Constant.LOGIN_USER_NAME);
        }
        return userName;
    }

    public static void updateUserName(Context context, String value) {
        userName = value;
        SPHelper.getInstant(context).putData2SP(context, Constant.LOGIN_USER_NAME, value);
    }

    public static String getPwd(Context context) {
        if (TextUtils.isEmpty(pwd)) {
            pwd = SPHelper.getInstant(context).getStringFromSP(context, Constant.LOGIN_PWD);
        }
        return pwd;
    }

    public static void updatePwd(Context context, String value) {
        pwd = value;
        SPHelper.getInstant(context).putData2SP(context, Constant.LOGIN_PWD, value);
    }

    public static String getAccessToken(Context context) {
        if (TextUtils.isEmpty(accessToken)) {
            accessToken = SPHelper.getInstant(context).getStringFromSP(context, Constant.ACCESS_TOKEN);
        }
        return accessToken;
    }

    public static void updateAccessToken(Context context, String value) {
        accessToken = value;
        SPHelper.getInstant(context).putData2SP(context, Constant.ACCESS_TOKEN, value);
    }

    public static String getGateWayTag(Context context) {
        if (TextUtils.isEmpty(gateWayTag)) {
            gateWayTag = SPHelper.getInstant(context).getStringFromSP(context, Constant.SETTING_GATEWAY_TAG);
        }
        return gateWayTag;
    }

    public static void updateGateWayTag(Context context, String value) {
        gateWayTag = value;
        SPHelper.getInstant(context).putData2SP(context, Constant.SETTING_GATEWAY_TAG, value);
    }
}
