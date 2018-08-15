package com.vexanium.vexgift.module.detail.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IDetailInteractor<T> {
    Subscription requestBuyVoucher(RequestCallback<T> callback, int userId, int voucherId, String token);

}
