package com.vexanium.vexgift.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.vexanium.vexgift.util.NetworkUtil;
import com.vexanium.vexgift.util.RxBus;

public class ConnReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            RxBus.get().post(RxBus.KEY_NETWORK_STATUS_CHANGE, NetworkUtil.isOnline(context));
        }
    }
}
