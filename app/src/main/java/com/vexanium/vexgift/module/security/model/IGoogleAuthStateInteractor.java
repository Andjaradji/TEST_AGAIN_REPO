package com.vexanium.vexgift.module.security.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IGoogleAuthStateInteractor<T> {
    Subscription updateGoogle2faState(RequestCallback<T> callback, int id, String key, String token, boolean isSetToEnable);

    Subscription requestCode(RequestCallback callback, int id);
}
