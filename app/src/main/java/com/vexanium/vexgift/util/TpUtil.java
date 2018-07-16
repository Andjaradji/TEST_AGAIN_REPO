package com.vexanium.vexgift.util;

import android.content.Context;

import net.grandcentrix.tray.TrayPreferences;

/**
 * Created by mac on 11/16/17.
 */

public class TpUtil extends TrayPreferences {
    public TpUtil(final Context context) {
        super(context, "TpUtil", 1);
    }

    public static String KEY_CURRENT_LOGGED_IN_USER = "key_current_logged_in_user";
    public static String KEY_CURRENT_UID = "key_current_uid";
    public static String KEY_SESSION_KEY = "key_current_key";

}
