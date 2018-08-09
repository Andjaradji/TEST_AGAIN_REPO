package com.vexanium.vexgift.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.socks.library.KLog;
import com.vexanium.vexgift.app.StaticGroup;

public class TimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            KLog.v("TimerReceiver ALARM context : " + context);

            if (intent.getBooleanExtra("noti", false)) {
                KLog.v("TimerReceiver ALARM noti");
                String title = intent.getStringExtra("title");
                String message = intent.getStringExtra("msg");
                String targetUrl = intent.getStringExtra("url");

                StaticGroup.sendLocalNotification(context, title, message, targetUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}