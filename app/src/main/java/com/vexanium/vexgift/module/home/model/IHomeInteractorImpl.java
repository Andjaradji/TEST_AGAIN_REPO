package com.vexanium.vexgift.module.home.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IHomeInteractorImpl implements IHomeInteractor{
    @Override
    public Subscription requestVoucherList(RequestCallback callback, int id) {

        return RetrofitManager.getInstance(HostType.COMMON_API).requestVoucherList(id).compose(RxUtil.<VouchersResponse>handleResult())
                .flatMap(new Func1<VouchersResponse, Observable<VouchersResponse>>() {
                    @Override
                    public Observable<VouchersResponse> call(VouchersResponse vouchersResponse) {

                        return Observable.just(vouchersResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
