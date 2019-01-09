package com.vexanium.vexgift.module.misc.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.DigifinexReferralListResponse;
import com.vexanium.vexgift.bean.response.DigifinexReferralResponse;
import com.vexanium.vexgift.bean.response.SettingResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.module.login.model.ILoginInteractor;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IDigifinexEmailInteractorImpl implements IDigifinexEmailInteractor {
    @Override
    public Subscription requestSubmitEmailReferral(RequestCallback callback, int userId, String email) {
        return RetrofitManager.getInstance(HostType.COMMON_API).setDigifinexEmailReferral(userId, email).compose(RxUtil.<DigifinexReferralResponse>handleResult())
                .flatMap(new Func1<DigifinexReferralResponse, Observable<DigifinexReferralResponse>>() {
                    @Override
                    public Observable<DigifinexReferralResponse> call(DigifinexReferralResponse digifinexReferralResponse) {

                        KLog.json("HPtes", JsonUtil.toString(digifinexReferralResponse));

                        return Observable.just(digifinexReferralResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestEmailReferredList(RequestCallback callback, int userId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).getDigifinexEmailReferralList(userId).compose(RxUtil.<DigifinexReferralListResponse>handleResult())
                .flatMap(new Func1<DigifinexReferralListResponse, Observable<DigifinexReferralListResponse>>() {
                    @Override
                    public Observable<DigifinexReferralListResponse> call(DigifinexReferralListResponse digifinexReferralResponse) {

                        KLog.json("HPtes", JsonUtil.toString(digifinexReferralResponse));

                        return Observable.just(digifinexReferralResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

