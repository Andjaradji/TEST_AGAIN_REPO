package com.vexanium.vexgift.app;

import android.content.Context;
import android.content.Intent;

import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.UserLoginResponse;

import java.util.ArrayList;


/**
 * Created by hizkia on 12/03/18.
 */

public class StaticGroup {
    public static final int SHORTCUT_BAR = 1;
    public static final int HOT_LIST = 2;
    public static final int EXPLORE_BAR = 3;
    public static final int CATEGORY_BAR = 4;
    public static final int NORMAL_COUPON = 5;

    public static UserLoginResponse currentUser;

    public static void shareWithShareDialog(Context context, String message, String dialogTitle) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setType("text/plain");
        String title = dialogTitle.isEmpty() ? "" : dialogTitle;

        try {
            if (context != null) {
                Intent chooserIntent = Intent.createChooser(sendIntent, title);
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooserIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
