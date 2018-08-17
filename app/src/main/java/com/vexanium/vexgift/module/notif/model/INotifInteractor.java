package com.vexanium.vexgift.module.notif.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface INotifInteractor<T> {
    Subscription requestNotifList(RequestCallback<T> callback, int id);

}
