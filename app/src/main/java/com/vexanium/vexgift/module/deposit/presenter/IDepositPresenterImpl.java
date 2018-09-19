package com.vexanium.vexgift.module.deposit.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.deposit.model.IDepositInteractorImpl;
import com.vexanium.vexgift.module.deposit.view.IDepositView;

import java.io.Serializable;

import rx.Subscription;

public class IDepositPresenterImpl extends BasePresenterImpl<IDepositView, Serializable> implements IDepositPresenter {
    private IDepositInteractorImpl<Serializable> mDepositInteractorImpl;
    private boolean mHasInit;

    public IDepositPresenterImpl(IDepositView view) {
        super(view);
        mDepositInteractorImpl = new IDepositInteractorImpl<>();
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
    public void requestDepositList(int id) {
        Subscription subscription = mDepositInteractorImpl.requestDepositList(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestUserDepositList(int id) {
        Subscription subscription = mDepositInteractorImpl.requestUserDepositList(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestDeposit(int id, int depositId, int depositOptionId) {
        Subscription subscription = mDepositInteractorImpl.requestDeposit(this, id, depositId, depositOptionId);
        compositeSubscription.add(subscription);
    }
}