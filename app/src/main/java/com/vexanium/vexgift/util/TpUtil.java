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
    public static String KEY_SESSION_KEY = "key_current_key";
    public static String KEY_WALKTHROUGH = "key_walkthrough";
    public static String KEY_GOOGLE2FA = "key_google2fa";
    public static String KEY_GOOGLE2FA_LOCK = "key_google2fa_lock";
    public static String KEY_GOOGLE2FA_STATE = "key_google2fa_state";
    public static String KEY_LAST_ACTIVE_TIME = "key_last_active_time";

    public void removePrivate(){
        put(TpUtil.KEY_CURRENT_LOGGED_IN_USER,"");
        remove(TpUtil.KEY_CURRENT_LOGGED_IN_USER);

        put(TpUtil.KEY_SESSION_KEY,"");
        remove(TpUtil.KEY_SESSION_KEY);

        put(TpUtil.KEY_GOOGLE2FA,"");
        remove(TpUtil.KEY_GOOGLE2FA);

        put(TpUtil.KEY_GOOGLE2FA_LOCK,"");
        remove(TpUtil.KEY_GOOGLE2FA_LOCK);

        put(TpUtil.KEY_GOOGLE2FA_STATE,"");
        remove(TpUtil.KEY_GOOGLE2FA_STATE);
    }

}
