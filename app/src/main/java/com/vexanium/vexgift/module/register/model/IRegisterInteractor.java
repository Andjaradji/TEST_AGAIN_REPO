package com.vexanium.vexgift.module.register.model;

import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IRegisterInteractor<T> {
    Subscription requestLogin(RequestCallback<T> callback, User user);
    Subscription requestRegister(RequestCallback<T> callback, User user);
    Subscription requestEmailConfirmation(RequestCallback<T> callback, int userId, String code);
    Subscription requestResendEmail(RequestCallback<T> callback, int userId);
}
