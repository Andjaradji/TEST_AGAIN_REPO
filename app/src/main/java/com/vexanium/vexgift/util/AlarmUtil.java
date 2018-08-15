package com.vexanium.vexgift.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.vexanium.vexgift.receiver.TimerReceiver;

public class AlarmUtil {
    private static AlarmUtil _instance;

    public static AlarmUtil getInstance() {
        if (_instance == null) _instance = new AlarmUtil();
        return _instance;
    }

    public void startLocalNotiAlarm(Context context, String title, String msg, String url, long timeStamp) {
        Intent intent = new Intent(context, TimerReceiver.class);
        intent.putExtra("noti", true);
        intent.putExtra("title", title);
        intent.putExtra("msg", msg);
        intent.putExtra("url", url);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        setAlarm(context, pIntent, timeStamp);
    }

    private void setAlarm(Context context, PendingIntent pendingIntent, long alarmTime) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }
}

