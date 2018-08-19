package com.vexanium.vexgift.module.register.model;

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

public class IRegisterInteractorImpl implements IRegisterInteractor {
    @Override
    public Subscription requestLogin(RequestCallback callback, User user) {
        KLog.json("KIRIM", JsonUtil.toString(user));
        return RetrofitManager.getInstance(HostType.COMMON_API).requestLogin(user).compose(RxUtil.<UserLoginResponse>handleResult())
                .flatMap(new Func1<UserLoginResponse, Observable<UserLoginResponse>>() {
                    @Override
                    public Observable<UserLoginResponse> call(UserLoginResponse userLoginResponse) {

                        KLog.json("HPtes", JsonUtil.toString(userLoginResponse));
                        StaticGroup.checkPushToken(userLoginResponse.user);

                        return Observable.just(userLoginResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestRegister(RequestCallback callback, User user) {
        KLog.json("KIRIM", JsonUtil.toString(user));
        return RetrofitManager.getInstance(HostType.COMMON_API).requestRegister(user).compose(RxUtil.<EmptyResponse>handleResult())
                .flatMap(new Func1<EmptyResponse, Observable<EmptyResponse>>() {
                    @Override
                    public Observable<EmptyResponse> call(EmptyResponse userLoginResponse) {

                        return Observable.just(userLoginResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestEmailConfirmation(RequestCallback callback, int userId, String code) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestEmailConfirmation(userId, code).compose(RxUtil.<EmptyResponse>handleResult())
                .flatMap(new Func1<EmptyResponse, Observable<EmptyResponse>>() {
                    @Override
                    public Observable<EmptyResponse> call(EmptyResponse userLoginResponse) {

                        return Observable.just(userLoginResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

