package com.vexanium.vexgift.module.wallet.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.wallet.model.IWalletInteractor;
import com.vexanium.vexgift.module.wallet.model.IWalletInteractorImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;

import java.io.Serializable;

import rx.Subscription;

public class IWalletPresenterImpl extends BasePresenterImpl<IWalletView, Serializable> implements IWalletPresenter {
    private IWalletInteractor<Serializable> mInteractor;
    private boolean mHasInit;

    public IWalletPresenterImpl(IWalletView view) {
        super(view);
        mInteractor = new IWalletInteractorImpl();
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
    public void requestGetWallet(int id) {
        Subscription subscription = mInteractor.requestGetWallet(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestCreateWallet(int id) {
        Subscription subscription = mInteractor.requestCreateWallet(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestDoWithdraw(int id, int walletId) {
        Subscription subscription = mInteractor.requestDoWithdraw(this, id, walletId);
        compositeSubscription.add(subscription);
    }
}
