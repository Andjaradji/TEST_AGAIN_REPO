package com.vexanium.vexgift.module.tokensale.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.TokenSaleHistoryResponse;
import com.vexanium.vexgift.bean.response.TokenSalePaymentResponse;
import com.vexanium.vexgift.bean.response.TokenSaleResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.bean.response.UserDepositResponse;
import com.vexanium.vexgift.bean.response.UserDepositSingleResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.module.deposit.model.IDepositInteractor;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class ITokenSaleInteractorImpl<T> implements ITokenSaleInteractor {

    @Override
    public Subscription requestTokenSaleList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestTokenSaleList(id).compose(RxUtil.<TokenSaleResponse>handleResult())
                .flatMap(new Func1<TokenSaleResponse, Observable<TokenSaleResponse>>() {
                    @Override
                    public Observable<TokenSaleResponse> call(TokenSaleResponse tokenSaleResponse) {

                        KLog.json("HPtes", JsonUtil.toString(tokenSaleResponse));
                        return Observable.just(tokenSaleResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestTokenSaleHistoryList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestTokenSaleHistoryList(id).compose(RxUtil.<TokenSaleHistoryResponse>handleResult())
                .flatMap(new Func1<TokenSaleHistoryResponse, Observable<TokenSaleHistoryResponse>>() {
                    @Override
                    public Observable<TokenSaleHistoryResponse> call(TokenSaleHistoryResponse tokenSaleHistoryResponse) {

                        KLog.json("HPtes", JsonUtil.toString(tokenSaleHistoryResponse));
                        return Observable.just(tokenSaleHistoryResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestBuyTokenSale(RequestCallback callback, int id, int tokenSaleId, int tokenSalePaymentOptionId, float amount) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestBuyTokenSale(id,tokenSaleId,tokenSalePaymentOptionId,amount).compose(RxUtil.<TokenSalePaymentResponse>handleResult())
                .flatMap(new Func1<TokenSalePaymentResponse, Observable<TokenSalePaymentResponse>>() {
                    @Override
                    public Observable<TokenSalePaymentResponse> call(TokenSalePaymentResponse tokenSalePaymentResponse) {

                        KLog.json("HPtes", JsonUtil.toString(tokenSalePaymentResponse));
                        return Observable.just(tokenSalePaymentResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestUpdateDistributionAddress(RequestCallback callback, int id, int tokenSalePaymentId, String address) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestUpdateDistributionAddress(id,tokenSalePaymentId,address).compose(RxUtil.<EmptyResponse>handleResult())
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

