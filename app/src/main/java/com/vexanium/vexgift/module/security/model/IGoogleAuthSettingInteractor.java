package com.vexanium.vexgift.module.security.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IGoogleAuthSettingInteractor<T> {
    Subscription requestCode(RequestCallback<T> callback, int id);
}
