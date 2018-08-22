package com.vexanium.vexgift.module.referral.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.bean.response.UserReferralResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.module.register.model.IRegisterInteractor;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IReferralInteractorImpl implements IReferralInteractor {

    @Override
    public Subscription requestUserReferral(RequestCallback callback, int userId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestUserReferrals(userId).compose(RxUtil.<UserReferralResponse>handleResult())
                .flatMap(new Func1<UserReferralResponse, Observable<UserReferralResponse>>() {
                    @Override
                    public Observable<UserReferralResponse> call(UserReferralResponse userReferralResponse) {

                        return Observable.just(userReferralResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

