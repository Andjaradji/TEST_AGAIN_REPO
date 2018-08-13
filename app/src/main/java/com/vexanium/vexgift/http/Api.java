package com.vexanium.vexgift.http;

import android.os.Build;
import android.text.TextUtils;

import com.socks.library.KLog;
import com.vexanium.vexgift.BuildConfig;

import java.util.HashMap;



/**
 * Created by hizkia on 11/21/17.
 */

public class Api {
    public static final String API_HOST = "http://api.vexgift.com/";
    public static final String TEST_HOST = "https://s3-ap-southeast-1.amazonaws.com/";
    public static final String WEB_HOST = "http://www.vexanium.com/";
    public static final String STATIC_CDN_HOST = "http://cdn.vexanium.com/";
    public static final String SHORT_HOST = "http://";
    public static final String API_KEY = "8613f2d52312f57a028464ab63e03c90b5297a07d7c813bf25";


    public static String getHost(int hostType) {
        switch (hostType) {
            case HostType.COMMON_API: {
                boolean isDevEnv = BuildConfig.TARGET_ENV.equals("development");
//                return String.format("%s://%s/", isDevEnv ? "https" : "https", API_HOST);
                return API_HOST;
            }
            case HostType.TEST_API: {
                return TEST_HOST;
            }
            case HostType.WEB_URL: {
                return WEB_HOST;
            }
            case HostType.STATIC_CDN_API: {
                return STATIC_CDN_HOST;
            }
            case HostType.SHORT_URL: {
                return SHORT_HOST;
            }
        }
        return "";
    }

    public static HashMap<String, Object> getBasicParam() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("ov", Build.VERSION.RELEASE);
        map.put("av", "" + BuildConfig.VERSION_CODE);
        map.put("deviceName", getDeviceName());

        KLog.v("--------------------------------------------Request Basic Param Start----------------------------------------------------");
        KLog.json(map.toString());
        KLog.v("--------------------------------------------Response Basic Param End----------------------------------------------------");

        return map;
    }

    public static HashMap<String, Object> getLoginParam() {
        HashMap<String, Object> params = getBasicParam();
        params.put("dvc", getDeviceName());
        KLog.v("--------------------------------------------Request Login Param Start----------------------------------------------------");
        KLog.json(params.toString());
        KLog.v("--------------------------------------------Response Login Param End----------------------------------------------------");

        return params;
    }

    private static String getDeviceName() {
        String deviceString = "";
        if (!TextUtils.isEmpty(Build.MODEL)) {
            deviceString = Build.MODEL;
        }
        return deviceString;
    }
}
