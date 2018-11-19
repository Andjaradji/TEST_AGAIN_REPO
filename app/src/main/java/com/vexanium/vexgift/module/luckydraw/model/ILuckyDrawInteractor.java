package com.vexanium.vexgift.module.luckydraw.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface ILuckyDrawInteractor<T> {
    Subscription requestLuckyDraw(RequestCallback<T> callback, int id, int limit, int offset, int luckyDrawCategoryId, int memberTypeId, int paymentTypeId);
}
