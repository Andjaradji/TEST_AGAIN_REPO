package com.vexanium.vexgift.module.login.model;

import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IForgotPwInteractor<T> {
    Subscription requestResetPassword(RequestCallback<T> callback, String email);

}
