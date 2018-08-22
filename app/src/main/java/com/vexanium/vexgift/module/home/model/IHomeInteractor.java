package com.vexanium.vexgift.module.home.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IHomeInteractor<T> {
    Subscription requestVoucherList(RequestCallback<T> callback, int id);

    Subscription requestBestVoucherList(RequestCallback<T> callback, int id);

    Subscription requestFeaturedVoucherList(RequestCallback<T> callback, int id);

    Subscription requestKyc(RequestCallback<T> callback, int id);
}
