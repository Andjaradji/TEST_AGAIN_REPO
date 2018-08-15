package com.vexanium.vexgift.module.detail.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IDetailInteractorImpl implements IDetailInteractor {
    @Override
    public Subscription requestBuyVoucher(RequestCallback callback, int userId, int voucherId, String token) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestBuyVoucher(userId, voucherId, token).compose(RxUtil.<EmptyResponse>handleResult())
                .flatMap(new Func1<EmptyResponse, Observable<EmptyResponse>>() {
                    @Override
                    public Observable<EmptyResponse> call(EmptyResponse emptyResponse) {

                        KLog.json("HPtes", JsonUtil.toString(emptyResponse));
                        return Observable.just(emptyResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

