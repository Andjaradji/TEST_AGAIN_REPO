package com.vexanium.vexgift.module.voucher.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IVoucherInteractor<T> {
    Subscription requestMemberType(RequestCallback<T> callback, int id);
    Subscription requestPaymentType(RequestCallback<T> callback, int id);
    Subscription requestLocation(RequestCallback<T> callback, int id);

}
