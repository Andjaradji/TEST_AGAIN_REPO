package com.vexanium.vexgift.module.premium.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IPremiumInteractorImpl<T> implements IPremiumInteractor {

    @Override
    public Subscription requestPremiumList(RequestCallback callback, int userId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestPremiumList(userId).compose(RxUtil.<PremiumListResponse>handleResult())
                .flatMap(new Func1<PremiumListResponse, Observable<PremiumListResponse>>() {
                    @Override
                    public Observable<PremiumListResponse> call(PremiumListResponse premiumListResponse) {

                        KLog.json("HPtes", JsonUtil.toString(premiumListResponse));
                        return Observable.just(premiumListResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

