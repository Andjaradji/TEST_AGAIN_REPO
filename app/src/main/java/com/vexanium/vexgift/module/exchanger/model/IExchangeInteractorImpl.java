package com.vexanium.vexgift.module.exchanger.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.ExchangeResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IExchangeInteractorImpl implements IExchangeInteractor {
    @Override
    public Subscription requestExchangeList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestExchangeList(id).compose(RxUtil.<ExchangeResponse>handleResult())
                .flatMap(new Func1<ExchangeResponse, Observable<ExchangeResponse>>() {
                    @Override
                    public Observable<ExchangeResponse> call(ExchangeResponse exchangeResponse) {

                        KLog.json("HPtes", JsonUtil.toString(exchangeResponse));
                        return Observable.just(exchangeResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
