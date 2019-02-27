package com.vexanium.vexgift.module.wallet.model;

import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.bean.response.WithdrawResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IWalletInteractorImpl implements IWalletInteractor {
    @Override
    public Subscription requestGetWallet(RequestCallback callback, int id) {

        return RetrofitManager.getInstance(HostType.COMMON_API).getWallet(id).compose(RxUtil.<WalletResponse>handleResult())
                .flatMap(new Func1<WalletResponse, Observable<WalletResponse>>() {
                    @Override
                    public Observable<WalletResponse> call(WalletResponse response) {

                        return Observable.just(response);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestCreateWallet(RequestCallback callback, int id) {

        return RetrofitManager.getInstance(HostType.COMMON_API).createWallet(id).compose(RxUtil.<WalletResponse>handleResult())
                .flatMap(new Func1<WalletResponse, Observable<WalletResponse>>() {
                    @Override
                    public Observable<WalletResponse> call(WalletResponse response) {

                        return Observable.just(response);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestDoWithdraw(RequestCallback callback, int id, int walletId) {

        return RetrofitManager.getInstance(HostType.COMMON_API).doWithdraw(id, walletId).compose(RxUtil.<WithdrawResponse>handleResult())
                .flatMap(new Func1<WithdrawResponse, Observable<WithdrawResponse>>() {
                    @Override
                    public Observable<WithdrawResponse> call(WithdrawResponse response) {

                        return Observable.just(response);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
