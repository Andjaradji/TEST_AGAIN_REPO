package com.vexanium.vexgift.module.buyback.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.BuybackHistoryResponse;
import com.vexanium.vexgift.bean.response.BuybackPaymentResponse;
import com.vexanium.vexgift.bean.response.BuybackResponse;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IBuybackInteractorImpl<T> implements IBuybackInteractor {

    @Override
    public Subscription requestBuybackList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestBuybackList(id).compose(RxUtil.<BuybackResponse>handleResult())
                .flatMap(new Func1<BuybackResponse, Observable<BuybackResponse>>() {
                    @Override
                    public Observable<BuybackResponse> call(BuybackResponse buybackResponse) {

                        KLog.json("HPtes", JsonUtil.toString(buybackResponse));
                        return Observable.just(buybackResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestBuybackHistoryList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestBuybackHistoryList(id).compose(RxUtil.<BuybackHistoryResponse>handleResult())
                .flatMap(new Func1<BuybackHistoryResponse, Observable<BuybackHistoryResponse>>() {
                    @Override
                    public Observable<BuybackHistoryResponse> call(BuybackHistoryResponse buybackHistoryResponse) {

                        KLog.json("HPtes", JsonUtil.toString(buybackHistoryResponse));
                        return Observable.just(buybackHistoryResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestBuyBuyback(RequestCallback callback, int id, int buybackId, int buybackOptionId, float amount) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestDoBuyback(id, buybackId, buybackOptionId, amount).compose(RxUtil.<BuybackPaymentResponse>handleResult())
                .flatMap(new Func1<BuybackPaymentResponse, Observable<BuybackPaymentResponse>>() {
                    @Override
                    public Observable<BuybackPaymentResponse> call(BuybackPaymentResponse buybackHistory) {

                        KLog.json("HPtes", JsonUtil.toString(buybackHistory));
                        return Observable.just(buybackHistory);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
//
//    @Override
//    public Subscription requestTokenSalePayment(RequestCallback callback, int id, int tokenSalePaymentId) {
//        return RetrofitManager.getInstance(HostType.COMMON_API).requestTokenSalePayment(id, tokenSalePaymentId).compose(RxUtil.<TokenSaleHistoryDetailResponse>handleResult())
//                .flatMap(new Func1<TokenSaleHistoryDetailResponse, Observable<TokenSaleHistoryDetailResponse>>() {
//                    @Override
//                    public Observable<TokenSaleHistoryDetailResponse> call(TokenSaleHistoryDetailResponse tokenSaleHistoryDetailResponse) {
//
//                        KLog.json("HPtes", JsonUtil.toString(tokenSaleHistoryDetailResponse));
//                        return Observable.just(tokenSaleHistoryDetailResponse);
//                    }
//                })
//                .subscribe(new BaseSubscriber<>(callback));
//    }
//
    @Override
    public Subscription requestUpdateDistributionAddress(RequestCallback callback, int id, int buybackPaymentId, String address) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestUpdateBuybackDistributionAddress(id, buybackPaymentId, address).compose(RxUtil.<EmptyResponse>handleResult())
                .flatMap(new Func1<EmptyResponse, Observable<EmptyResponse>>() {
                    @Override
                    public Observable<EmptyResponse> call(EmptyResponse emptyResponse) {

                        KLog.json("HPtes", JsonUtil.toString(emptyResponse));
                        return Observable.just(emptyResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

