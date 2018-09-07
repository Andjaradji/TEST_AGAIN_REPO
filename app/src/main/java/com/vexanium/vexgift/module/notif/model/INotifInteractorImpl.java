package com.vexanium.vexgift.module.notif.model;


import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.NotificationResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class INotifInteractorImpl implements INotifInteractor {
    @Override
    public Subscription requestNotifList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestUserNotification(id).compose(RxUtil.<NotificationResponse>handleResult())
                .flatMap(new Func1<NotificationResponse, Observable<NotificationResponse>>() {
                    @Override
                    public Observable<NotificationResponse> call(NotificationResponse notificationResponse) {

                        return Observable.just(notificationResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
