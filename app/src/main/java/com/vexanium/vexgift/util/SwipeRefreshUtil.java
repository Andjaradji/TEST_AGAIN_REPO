package com.vexanium.vexgift.util;

import android.os.SystemClock;

/**
 * Created by mac on 11/16/17.
 */

public class SwipeRefreshUtil {
    private static long sLastRefreshTime = 0L;

    public static boolean isFastMultipleSwipe() {
        long nowTime = SystemClock.elapsedRealtime();
        if ((nowTime - sLastRefreshTime) < 300) {
            return true;
        } else {
            sLastRefreshTime = nowTime;
            return false;
        }
    }
}
