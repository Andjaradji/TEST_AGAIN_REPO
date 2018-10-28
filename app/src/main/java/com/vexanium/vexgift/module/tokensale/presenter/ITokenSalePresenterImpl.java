package com.vexanium.vexgift.module.tokensale.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.tokensale.model.ITokenSaleInteractorImpl;
import com.vexanium.vexgift.module.tokensale.view.ITokenSaleView;

import java.io.Serializable;

import rx.Subscription;

public class ITokenSalePresenterImpl extends BasePresenterImpl<ITokenSaleView, Serializable> implements ITokenSalePresenter {
    private ITokenSaleInteractorImpl<Serializable> mTokenSaleInteractorImpl;
    private boolean mHasInit;

    public ITokenSalePresenterImpl(ITokenSaleView view) {
        super(view);
        mTokenSaleInteractorImpl = new ITokenSaleInteractorImpl<>();
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
    public void requestTokenSaleList(int id) {
        Subscription subscription = mTokenSaleInteractorImpl.requestTokenSaleList(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestTokenSaleHistoryList(int id) {
        Subscription subscription = mTokenSaleInteractorImpl.requestTokenSaleHistoryList(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void buyTokenSale(int id, int tokenSaleId, int tokenSalePaymentOptionId, float amount) {
        Subscription subscription = mTokenSaleInteractorImpl.requestBuyTokenSale(this, id,tokenSaleId,tokenSalePaymentOptionId,amount);
        compositeSubscription.add(subscription);
    }

    @Override
    public void updateDistributionAddress(int id, int tokenSalePaymentId, String address) {
        Subscription subscription = mTokenSaleInteractorImpl.requestUpdateDistributionAddress(this, id,tokenSalePaymentId,address);
        compositeSubscription.add(subscription);
    }

    @Override
    public void getTokenSalePayment(int id, int tokenSalePaymentId) {
        Subscription subscription = mTokenSaleInteractorImpl.requestTokenSalePayment(this, id,tokenSalePaymentId);
        compositeSubscription.add(subscription);
    }

}