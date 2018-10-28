package com.vexanium.vexgift.module.tokensale.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface ITokenSaleInteractor<T> {
    Subscription requestTokenSaleList(RequestCallback<T> callback, int id);
    Subscription requestTokenSaleHistoryList(RequestCallback<T> callback, int id);
    Subscription requestBuyTokenSale(RequestCallback<T> callback, int id, int tokenSaleId, int tokenSalePaymentOptionId, float amount);
    Subscription requestTokenSalePayment(RequestCallback<T> callback, int id, int tokenSalePaymentId);
    Subscription requestUpdateDistributionAddress(RequestCallback<T> callback, int id, int tokenSalePaymentId, String address);
}
