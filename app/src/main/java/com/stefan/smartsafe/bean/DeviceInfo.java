package com.stefan.smartsafe.bean;

/**
 * @Author: Stefan Charles
 * @Date: 2020-07-31
 * @Website: www.stefancharles.cn
 * @E-mail: stefancharles@qq.com
 * @Copyright: Copyright (c) 2020 Security Plus.All rights reserved.
 **/

public class DeviceInfo {
    public static String deviceId = "41210";
    public static String apiTagBoxDefence = "defense";
    public static String apiTagBoxControl = "ctrl";
    public static String apiTagBoxAlarm = "alarm";
    public static String apiTagBoxDefenceStatus = "DefenseState";
    public static Object openBoxValue = 1;
    public static Object closeBoxValue = 0;
    public static Object openDefenceValue = 1;
    public static Object closeDefenceValue = 0;


    private String value;
    private String recordTime;

    public DeviceInfo(String value, String recordTime) {
        this.value = value;
        this.recordTime = recordTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }
}
