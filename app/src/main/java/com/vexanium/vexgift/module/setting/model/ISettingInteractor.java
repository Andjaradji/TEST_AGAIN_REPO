package com.vexanium.vexgift.module.setting.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface ISettingInteractor<T> {
    Subscription requestCode(RequestCallback<T> callback, int id);
}
