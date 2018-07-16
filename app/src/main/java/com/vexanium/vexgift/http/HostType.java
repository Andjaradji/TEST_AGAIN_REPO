package com.vexanium.vexgift.http;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mac on 11/21/17.
 */

public class HostType {
    public static final int TYPE_COUNT = 4;

    @HostTypeChecker
    public static final int COMMON_API = 1;

    public static final int TEST_API = 2;

    public static final int WEB_URL = 3;

    @HostTypeChecker
    public static final int STATIC_CDN_API = 4;

    @HostTypeChecker
    public static final int SHORT_URL = 5;

    @IntDef({COMMON_API, WEB_URL, STATIC_CDN_API, SHORT_URL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HostTypeChecker {
    }
}
