package com.vexanium.vexgift.module.premium.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.PremiumHistoryResponse;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.bean.response.PremiumPurchaseResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
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

    @Override
    public Subscription purchasePremium(RequestCallback callback, int userId, int duration, int price, String currency) {
        return RetrofitManager.getInstance(HostType.COMMON_API).purchasePremium(userId,duration,price,currency).compose(RxUtil.<PremiumPurchaseResponse>handleResult())
                .flatMap(new Func1<PremiumPurchaseResponse, Observable<PremiumPurchaseResponse>>() {
                    @Override
                    public Observable<PremiumPurchaseResponse> call(PremiumPurchaseResponse premiumPurchaseResponse) {

                        KLog.json("HPtes", JsonUtil.toString(premiumPurchaseResponse));
                        return Observable.just(premiumPurchaseResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestGetActAddress(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestGetActAddress(id).compose(RxUtil.<UserAddressResponse>handleResult())
                .flatMap(new Func1<UserAddressResponse, Observable<UserAddressResponse>>() {
                    @Override
                    public Observable<UserAddressResponse> call(UserAddressResponse userAddressResponse) {

                        KLog.json("HPtes", JsonUtil.toString(userAddressResponse));
                        return Observable.just(userAddressResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestPremiumHistoryList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestUserPremiumHistory(id).compose(RxUtil.<PremiumHistoryResponse>handleResult())
                .flatMap(new Func1<PremiumHistoryResponse, Observable<PremiumHistoryResponse>>() {
                    @Override
                    public Observable<PremiumHistoryResponse> call(PremiumHistoryResponse premiumHistoryResponse) {

                        KLog.json("HPtes", JsonUtil.toString(premiumHistoryResponse));
                        return Observable.just(premiumHistoryResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

