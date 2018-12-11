package com.vexanium.vexgift.module.exchanger.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IExchangeInteractor<T> {
    Subscription requestExchangeList(RequestCallback<T> callback, int id);
}
