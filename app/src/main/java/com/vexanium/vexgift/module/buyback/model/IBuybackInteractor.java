package com.vexanium.vexgift.module.buyback.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IBuybackInteractor<T> {
    Subscription requestBuybackList(RequestCallback<T> callback, int id);

    Subscription requestBuybackHistoryList(RequestCallback<T> callback, int id);

    Subscription requestBuyBuyback(RequestCallback<T> callback, int id, int buybackId, int buybackPaymentOptionId, float amount);

//    Subscription requestBuybackPayment(RequestCallback<T> callback, int id, int buybackPaymentId);

//    Subscription requestUpdateDistributionAddress(RequestCallback<T> callback, int id, int buybackPaymentId, String address);
}
