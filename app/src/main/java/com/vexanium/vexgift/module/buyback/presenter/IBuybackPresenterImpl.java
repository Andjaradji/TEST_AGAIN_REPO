package com.vexanium.vexgift.module.buyback.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.buyback.model.IBuybackInteractorImpl;
import com.vexanium.vexgift.module.buyback.view.IBuybackView;

import java.io.Serializable;

import rx.Subscription;

public class IBuybackPresenterImpl extends BasePresenterImpl<IBuybackView, Serializable> implements IBuybackPresenter {
    private IBuybackInteractorImpl<Serializable> mBuybackInteractorImpl;
    private boolean mHasInit;

    public IBuybackPresenterImpl(IBuybackView view) {
        super(view);
        mBuybackInteractorImpl = new IBuybackInteractorImpl<>();
    }

    @Override
    public void beforeRequest() {
        if (!mHasInit) {
            mHasInit = true;
            if (mView != null) {
                mView.showProgress();
            }
        }
    }

    @Override
    public void requestError(HttpResponse response) {
        if (response != null) {
            super.requestError(response);
            if (mView != null) {
                mView.handleResult(null, response);
            }
        }
    }

    @Override
    public void requestSuccess(Serializable data) {
        if (mView != null) {
            mView.handleResult(data, null);
        }
    }

    @Override
    public void requestBuybackList(int id) {
        Subscription subscription = mBuybackInteractorImpl.requestBuybackList(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestBuybackHistoryList(int id) {
        Subscription subscription = mBuybackInteractorImpl.requestBuybackHistoryList(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void buyBuyback(int id, int buybackId, int buybackptionId, float amount) {
        Subscription subscription = mBuybackInteractorImpl.requestBuyBuyback(this, id, buybackId, buybackptionId, amount);
        compositeSubscription.add(subscription);
    }

    @Override
    public void updateDistributionAddress(int id, int buybackHistoryId, String address) {
        Subscription subscription = mBuybackInteractorImpl.requestUpdateDistributionAddress(this, id, buybackHistoryId, address);
        compositeSubscription.add(subscription);
    }

    @Override
    public void getBuybackPayment(int id, int buybackPaymentId) {

    }
}