package com.vexanium.vexgift.module.profile.model;

import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IProfileInteractor<T> {
    Subscription requestKyc(RequestCallback<T> callback, int id);
    Subscription submitKyc(RequestCallback<T> callback, Kyc kyc);
    Subscription changePassword(RequestCallback<T> callback, int id, String password, String newPassword);
}
