package com.vexanium.vexgift.module.premium.model;

import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IPremiumInteractor<T> {
    Subscription requestPremiumList(RequestCallback<T> callback, int userId);
}
