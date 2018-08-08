package com.vexanium.vexgift.module.voucher.model;

import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IVoucherInteractor<T> {
    Subscription requestLogin(RequestCallback<T> callback, User user);
    Subscription requestRegister(RequestCallback<T> callback, User user);

}
