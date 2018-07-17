package com.vexanium.vexgift.module.register.model;

import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IRegisterInteractor<T> {
    Subscription requestRegister(RequestCallback<T> callback, User user);
}
