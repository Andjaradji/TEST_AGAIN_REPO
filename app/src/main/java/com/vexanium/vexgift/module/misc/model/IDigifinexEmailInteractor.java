package com.vexanium.vexgift.module.misc.model;

import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IDigifinexEmailInteractor<T> {
    Subscription requestSubmitEmailReferral(RequestCallback<T> callback, int userId, String email);
    Subscription requestEmailReferredList(RequestCallback<T> callback, int userId);

}
