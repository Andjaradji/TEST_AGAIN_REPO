package com.vexanium.vexgift.module.deposit.model;

import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.RxUtil;

import java.util.Collections;
import java.util.Comparator;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IDepositInteractorImpl<T> implements IDepositInteractor {


    @Override
    public Subscription requestDepositList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestDepositList(id).compose(RxUtil.<DepositListResponse>handleResult())
                .flatMap(new Func1<DepositListResponse, Observable<DepositListResponse>>() {
                    @Override
                    public Observable<DepositListResponse> call(DepositListResponse depositListResponse) {
                        return Observable.just(depositListResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

