package com.vexanium.vexgift.util;

import android.content.Context;

import net.grandcentrix.tray.TrayPreferences;

/**
 * Created by mac on 11/16/17.
 */

public class TpUtil extends TrayPreferences {
    public static final String KEY_REG_ID = "key_reg_id";
    public static final String KEY_LEAVE_APP = "key_leave_app";
    public static String KEY_CURRENT_LOGGED_IN_USER = "key_current_logged_in_user";
    public static String KEY_IS_PASS_SET = "key_is_pass_set";
    public static String KEY_IS_VEX_ADD_SET = "key_is_vex_add_set";
    public static String KEY_WALKTHROUGH = "key_walkthrough";
    public static String KEY_GOOGLE2FA = "key_google2fa";
    public static String KEY_GOOGLE2FA_LOCK = "key_google2fa_lock";
    public static String KEY_LAST_ACTIVE_TIME = "key_last_active_time";
    public static String KEY_USER_ADDRESS = "key_user_address";
    public static String KEY_REFERRER = "key_referrer";
    public static String KEY_LAST_EMAIL_SEND_TIME = "key_time_email";
    public static String KEY_USER_PREMIUM_DUE_DATE = "key_user_premium_due_date";
    public static String KEY_IS_LATEST_CHECK_VERSION= "key_already_notice_update";
    public static String KEY_IS_ALREADY_GUIDE_HOME = "key_is_already_guide_home";
    public static String KEY_IS_ALREADY_GUIDE_VP = "key_is_already_guide_vp";
    public static String KEY_IS_ALREADY_GUIDE_MYBOX = "key_is_already_guide_mybox";
    private static TpUtil _tpUtil;
    public TpUtil(final Context context) {
        super(context, "TpUtil", 1);
    }

    public static TpUtil getInstance(Context context) {
        if (_tpUtil == null) {
            _tpUtil = new TpUtil(context);
        }
        return _tpUtil;
    }

    public void removePrivate() {
        put(TpUtil.KEY_CURRENT_LOGGED_IN_USER, "");
        remove(TpUtil.KEY_CURRENT_LOGGED_IN_USER);

        put(TpUtil.KEY_IS_PASS_SET, "");
        remove(TpUtil.KEY_IS_PASS_SET);

        put(TpUtil.KEY_REFERRER, "");
        remove(TpUtil.KEY_REFERRER);

        put(TpUtil.KEY_IS_VEX_ADD_SET, "");
        remove(TpUtil.KEY_IS_VEX_ADD_SET);

        put(TpUtil.KEY_GOOGLE2FA, "");
        remove(TpUtil.KEY_GOOGLE2FA);

        put(TpUtil.KEY_GOOGLE2FA_LOCK, "");
        remove(TpUtil.KEY_GOOGLE2FA_LOCK);

        put(TpUtil.KEY_USER_ADDRESS, "");
        remove(TpUtil.KEY_USER_ADDRESS);

        put(TpUtil.KEY_REG_ID, "");
        remove(TpUtil.KEY_REG_ID);

        put(TpUtil.KEY_USER_PREMIUM_DUE_DATE, "");
        remove(TpUtil.KEY_USER_PREMIUM_DUE_DATE);

        put(TpUtil.KEY_IS_LATEST_CHECK_VERSION, "");
        remove(TpUtil.KEY_IS_LATEST_CHECK_VERSION);
    }


}
