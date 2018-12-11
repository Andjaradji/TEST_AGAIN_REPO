package com.vexanium.vexgift.module.luckydraw.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface ILuckyDrawInteractor<T> {
    Subscription requestLuckyDraw(RequestCallback<T> callback, int id, int limit, int offset, int luckyDrawCategoryId, int memberTypeId, int paymentTypeId);
    Subscription requestLuckyDraw(RequestCallback<T> callback, int id, int luckyDrawId);
    Subscription requestUserLuckyDrawList(RequestCallback<T> callback, int id);
    Subscription buyLuckyDraw(RequestCallback<T> callback, int id, int luckyDrawId, int amount, String token);
    Subscription setUserLuckyDrawAddress(RequestCallback<T> callback, int id, int userLuckyDrawId, String address);
}
