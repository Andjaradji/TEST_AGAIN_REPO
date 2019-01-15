package com.vexanium.vexgift.module.news.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface INewsInteractor<T> {
    Subscription requestNews(RequestCallback<T> callback, int id);
}
