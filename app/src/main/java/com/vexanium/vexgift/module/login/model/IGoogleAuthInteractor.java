package com.vexanium.vexgift.module.login.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IGoogleAuthInteractor<T> {
    Subscription checkToken(RequestCallback<T> callback, int id, String token);
}
