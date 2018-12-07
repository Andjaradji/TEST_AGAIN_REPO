package com.vexanium.vexgift.module.luckydraw.model;

import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.LuckyDrawListResponse;
import com.vexanium.vexgift.bean.response.LuckyDrawResponse;
import com.vexanium.vexgift.bean.response.UserLuckyDrawListResponse;
import com.vexanium.vexgift.bean.response.UserLuckyDrawResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class ILuckyDrawInteractorImpl implements ILuckyDrawInteractor {

    @Override
    public Subscription requestLuckyDraw(RequestCallback callback, int id, int limit, int offset, int luckyDrawCategoryId, int memberTypeId, int paymentTypeId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestLuckyDrawList(id, limit,offset,luckyDrawCategoryId,memberTypeId,paymentTypeId).compose(RxUtil.<LuckyDrawListResponse>handleResult())
                .flatMap(new Func1<LuckyDrawListResponse, Observable<LuckyDrawListResponse>>() {
                    @Override
                    public Observable<LuckyDrawListResponse> call(LuckyDrawListResponse luckyDrawListResponse) {
                        return Observable.just(luckyDrawListResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestLuckyDraw(RequestCallback callback, int id, int luckyDrawId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestLuckyDraw(id, luckyDrawId).compose(RxUtil.<LuckyDrawResponse>handleResult())
                .flatMap(new Func1<LuckyDrawResponse, Observable<LuckyDrawResponse>>() {
                    @Override
                    public Observable<LuckyDrawResponse> call(LuckyDrawResponse luckyDrawResponse) {
                        return Observable.just(luckyDrawResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestUserLuckyDrawList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestUserLuckyDrawList(id).compose(RxUtil.<UserLuckyDrawListResponse>handleResult())
                .flatMap(new Func1<UserLuckyDrawListResponse, Observable<UserLuckyDrawListResponse>>() {
                    @Override
                    public Observable<UserLuckyDrawListResponse> call(UserLuckyDrawListResponse userLuckyDrawListResponse) {
                        return Observable.just(userLuckyDrawListResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription buyLuckyDraw(RequestCallback callback, int id, int luckyDrawId, String token) {
        return RetrofitManager.getInstance(HostType.COMMON_API).buyLuckyDraw(id, luckyDrawId, token).compose(RxUtil.<EmptyResponse>handleResult())
                .flatMap(new Func1<EmptyResponse, Observable<EmptyResponse>>() {
                    @Override
                    public Observable<EmptyResponse> call(EmptyResponse emptyResponse) {
                        return Observable.just(emptyResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription setUserLuckyDrawAddress(RequestCallback callback, int id, int userLuckyDrawId, String address) {
        return RetrofitManager.getInstance(HostType.COMMON_API).setUserLuckyDrawAddress(id, userLuckyDrawId, address).compose(RxUtil.<UserLuckyDrawResponse>handleResult())
                .flatMap(new Func1<UserLuckyDrawResponse, Observable<UserLuckyDrawResponse>>() {
                    @Override
                    public Observable<UserLuckyDrawResponse> call(UserLuckyDrawResponse userLuckyDrawResponse) {
                        return Observable.just(userLuckyDrawResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
