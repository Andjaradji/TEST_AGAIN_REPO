package com.vexanium.vexgift.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.socks.library.KLog;
import com.vexanium.vexgift.database.TablePrefDaoUtil;
import com.vexanium.vexgift.util.TpUtil;

import java.util.Set;

public class ReferrerReceiver extends BroadcastReceiver {

    private static String referrer = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        referrer = "";

        Bundle extras = intent.getExtras();

        if (extras != null) {
            referrer = extras.getString("referrer");
        }

        KLog.v("PARENT TEST", "Referrer is : " + referrer);
        if (!TextUtils.isEmpty(referrer)) {
            TpUtil tpUtil = new TpUtil(context);
            tpUtil.put(TpUtil.KEY_REFERRER, referrer);

            TablePrefDaoUtil.getInstance().saveInviteCodeToDb(referrer);
        }

        try {
            ActivityInfo ai = context.getPackageManager().getReceiverInfo(new ComponentName(context, ReferrerReceiver.class.getName()), PackageManager.GET_META_DATA);
            if (ai != null) {
                Bundle bundle = ai.metaData;
                if (bundle != null) {
                    Set<String> keys = bundle.keySet();
                    for (String k : keys) {
                        String v = bundle.getString(k);
                        ((BroadcastReceiver) Class.forName(v).newInstance()).onReceive(context, intent); //send intent by dynamically creating instance of receiver
                        Log.i("Referrer", String.format("PASS REFERRER TO...%s", v));
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Log.e("Referrer", "PASS REFERRER error : "+e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // adb
        // adb shell am broadcast -a com.android.vending.INSTALL_REFERRER -n com.vexanium.vexgift/.receiver.ReferrerReceiver --es "referrer" "utm_source=vexgift\&utm_medium=invite\&i=4a"
    }

}