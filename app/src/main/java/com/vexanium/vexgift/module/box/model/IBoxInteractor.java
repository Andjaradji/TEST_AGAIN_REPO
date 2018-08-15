package com.vexanium.vexgift.module.box.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IBoxInteractor<T> {
    Subscription requestUserVoucherList(RequestCallback<T> callback, int id);
}
