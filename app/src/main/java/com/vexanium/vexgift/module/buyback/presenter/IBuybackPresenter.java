package com.vexanium.vexgift.module.buyback.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IBuybackPresenter extends BasePresenter {
    void requestBuybackList(int id);

    void requestBuybackHistoryList(int id);

    void buyBuyback(int id, int buybackId, int buybackptionId, float amount);

    void updateDistributionAddress(int id, int buybackHistoryId, String address);

    void getBuybackPayment(int id, int buybackPaymentId);
}
