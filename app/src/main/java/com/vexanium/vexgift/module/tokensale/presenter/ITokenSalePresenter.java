package com.vexanium.vexgift.module.tokensale.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface ITokenSalePresenter extends BasePresenter {
    void requestTokenSaleList(int id);

    void requestTokenSaleHistoryList(int id);

    void buyTokenSale(int id, int tokenSaleId, int tokenSalePaymentOptionId, float amount);

    void updateDistributionAddress(int id, int tokenSalePaymentId, String address);

    void getTokenSalePayment(int id, int tokenSalePaymentId);
}
