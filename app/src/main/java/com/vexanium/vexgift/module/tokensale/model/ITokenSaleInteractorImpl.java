package com.vexanium.vexgift.module.tokensale.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.DepositListResponse;
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
}

