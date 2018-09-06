package com.vexanium.vexgift.fcm;

import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.util.RxBus;

import java.util.Map;

public class VexFirebaseMessagingService extends FirebaseMessagingService {

    public VexFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        KLog.v("VexFirebaseMessagingService", "onMessageReceived: From :" + remoteMessage.getFrom());
//        KLog.v("VexFirebaseMessagingService", "onMessageReceived: MessageBody" + remoteMessage.getNotification().getBody());
//        remoteMessage.get

        if(remoteMessage.getData()!= null){
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            if(TextUtils.isEmpty(title)){
                title = "VexGift";
            }
            String message = data.get("body");
            if(TextUtils.isEmpty(title)){
                message = "Notification";
            }
            String imageUrl = data.get("imageUrl");
            String url = data.get("url");
            if(TextUtils.isEmpty("url")){
                url = "vexgift://notif";
            }

            StaticGroup.sendLocalNotification(App.getContext(), title,message, url);
            if(StaticGroup.isScreenOn(App.getContext(), false )){
                RxBus.get().post(RxBus.KEY_NOTIF_ADDED,1);
            }
        }

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        KLog.v("VexFirebaseMessagingService", "onNewToken: " + token);
        StaticGroup.insertRegistrationID(token);
    }
}
