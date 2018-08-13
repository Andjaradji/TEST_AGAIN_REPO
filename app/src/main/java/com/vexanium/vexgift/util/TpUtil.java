package com.vexanium.vexgift.util;

import android.content.Context;

import net.grandcentrix.tray.TrayPreferences;

/**
 * Created by mac on 11/16/17.
 */

public class TpUtil extends TrayPreferences {
    Context context;
    private static TpUtil _tpUtil;

    public TpUtil(final Context context) {
        super(context, "TpUtil", 1);
    }

    public static TpUtil getInstance(Context context){
        if(_tpUtil == null){
            _tpUtil = new TpUtil(context);
        }
        return _tpUtil;
    }

    public static String KEY_CURRENT_LOGGED_IN_USER = "key_current_logged_in_user";
    public static String KEY_IS_PASS_SET = "key_is_pass_set";
    public static String KEY_IS_VEX_ADD_SET = "key_is_vex_add_set";
    public static String KEY_SESSION_KEY = "key_current_key";
    public static String KEY_WALKTHROUGH = "key_walkthrough";
    public static String KEY_GOOGLE2FA = "key_google2fa";
    public static String KEY_GOOGLE2FA_LOCK = "key_google2fa_lock";
    public static String KEY_LAST_ACTIVE_TIME = "key_last_active_time";
    public static String KEY_VEX_ADDRESS_VERIF_TIME = "key_vex_address_verif_time";
    public static String KEY_REFERRER = "key_referrer";

    public void removePrivate(){
        put(TpUtil.KEY_CURRENT_LOGGED_IN_USER,"");
        remove(TpUtil.KEY_CURRENT_LOGGED_IN_USER);

        put(TpUtil.KEY_SESSION_KEY,"");
        remove(TpUtil.KEY_SESSION_KEY);

        put(TpUtil.KEY_GOOGLE2FA,"");
        remove(TpUtil.KEY_GOOGLE2FA);

        put(TpUtil.KEY_GOOGLE2FA_LOCK,"");
        remove(TpUtil.KEY_GOOGLE2FA_LOCK);

        put(TpUtil.KEY_VEX_ADDRESS_VERIF_TIME,"");
        remove(TpUtil.KEY_VEX_ADDRESS_VERIF_TIME);
    }

}
