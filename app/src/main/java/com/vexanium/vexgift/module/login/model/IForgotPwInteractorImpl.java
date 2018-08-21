package com.vexanium.vexgift.module.login.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IForgotPwInteractorImpl implements IForgotPwInteractor {

    @Override
    public Subscription requestResetPassword(RequestCallback callback, String email) {
        KLog.json("KIRIM", JsonUtil.toString(email));
        KLog.json("KIRIM", JsonUtil.toString(email));
        return RetrofitManager.getInstance(HostType.COMMON_API).requestResetPass(email).compose(RxUtil.<EmptyResponse>handleResult())
                .flatMap(new Func1<EmptyResponse, Observable<EmptyResponse>>() {
                    @Override
                    public Observable<EmptyResponse> call(EmptyResponse emptyResponse) {

                        return Observable.just(emptyResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

