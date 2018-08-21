package com.vexanium.vexgift.module.premium.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IPremiumInteractor<T> {
    Subscription requestPremiumList(RequestCallback<T> callback, int userId);

    Subscription purchasePremium(RequestCallback<T> callback, int userId, int duration, int price, String currency);

    Subscription requestGetActAddress(RequestCallback callback, int id);

    Subscription requestPremiumHistoryList(RequestCallback callback, int id);

    Subscription requestPremiumDueDate(RequestCallback callback, int id);
}
