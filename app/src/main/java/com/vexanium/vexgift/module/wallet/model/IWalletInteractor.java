package com.vexanium.vexgift.module.wallet.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IWalletInteractor<T> {
    Subscription requestGetWallet(RequestCallback<T> callback, int id);

    Subscription requestCreateWallet(RequestCallback<T> callback, int id);

    Subscription requestDoWithdraw(RequestCallback<T> callback, int id, int walletId, float amount, String destinationAddress);

    Subscription requestCancelWithdraw(RequestCallback<T> callback, int id, int walletWithdrawId);

    Subscription requestGetWalletReferral(RequestCallback<T> callback, int id);

}
