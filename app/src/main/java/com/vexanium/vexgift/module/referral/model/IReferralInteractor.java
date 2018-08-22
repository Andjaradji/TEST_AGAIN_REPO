package com.vexanium.vexgift.module.referral.model;

import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IReferralInteractor<T> {
    Subscription requestUserReferral(RequestCallback<T> callback, int userId);
}
