package com.vexanium.vexgift.module.deposit.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IDepositInteractor<T> {
    Subscription requestDepositList(RequestCallback<T> callback, int id);
}
