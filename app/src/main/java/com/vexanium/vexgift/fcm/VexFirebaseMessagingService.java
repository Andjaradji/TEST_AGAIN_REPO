package com.vexanium.vexgift.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.socks.library.KLog;
import com.vexanium.vexgift.app.StaticGroup;

public class VexFirebaseMessagingService extends FirebaseMessagingService {

    public VexFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        KLog.v("VexFirebaseMessagingService", "onMessageReceived: From :" + remoteMessage.getFrom());
        KLog.v("VexFirebaseMessagingService", "onMessageReceived: MessageBody" + remoteMessage.getNotification().getBody());
//        remoteMessage.get

//        StaticGroup.sendLocalNotification(App.getContext(), "Test","Content", "vex");
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
