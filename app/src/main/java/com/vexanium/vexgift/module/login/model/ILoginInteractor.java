package com.vexanium.vexgift.module.login.model;

import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface ILoginInteractor<T> {
    Subscription requestLogin(RequestCallback<T> callback, User user);
    Subscription requestSetting(RequestCallback<T> callback, int userId);
    Subscription requestAppStatus(RequestCallback<T> callback);

}
