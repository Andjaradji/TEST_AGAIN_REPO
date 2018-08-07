package com.vexanium.vexgift.module.vexpoint.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IVexpointInteractor<T> {
    Subscription requestGetActAddress(RequestCallback<T> callback, int id);
    Subscription requestSetActAddress(RequestCallback<T> callback, int id, String address, String token);
}
