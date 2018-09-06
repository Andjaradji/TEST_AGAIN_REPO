package com.vexanium.vexgift.module.login.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IForgotPwInteractor<T> {
    Subscription requestResetPassword(RequestCallback<T> callback, String email);

    Subscription requestResetPasswordCodeValidation(RequestCallback<T> callback, String email, String code);

    Subscription requestResetPasswordTokenValidation(RequestCallback<T> callback, String email, String token, String password, String confirmPassword);

}
