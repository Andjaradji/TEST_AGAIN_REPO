package com.vexanium.vexgift.module.login.model;

import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IGoogleAuthInteractorImpl implements IGoogleAuthInteractor {

    @Override
    public Subscription checkToken(RequestCallback callback, int id, String token) {
        return RetrofitManager.getInstance(HostType.COMMON_API).checkToken(id, token).compose(RxUtil.<EmptyResponse>handleResult())
                .flatMap(new Func1<EmptyResponse, Observable<EmptyResponse>>() {
                    @Override
                    public Observable<EmptyResponse> call(EmptyResponse userLoginResponse) {

                        return Observable.just(userLoginResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

