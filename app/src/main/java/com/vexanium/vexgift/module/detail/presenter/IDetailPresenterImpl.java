package com.vexanium.vexgift.module.detail.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.detail.model.IDetailInteractor;
import com.vexanium.vexgift.module.detail.model.IDetailInteractorImpl;
import com.vexanium.vexgift.module.detail.view.IDetailView;

import java.io.Serializable;

import rx.Subscription;

public class IDetailPresenterImpl extends BasePresenterImpl<IDetailView, Serializable> implements IDetailPresenter {
    private IDetailInteractor<Serializable> mInteractor;
    private boolean mHasInit;

    public IDetailPresenterImpl(IDetailView view) {
        super(view);
        mInteractor = new IDetailInteractorImpl();
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
    public void requestBuyVoucher(int userId, int voucherId, String token) {
        Subscription subscription = mInteractor.requestBuyVoucher(this, userId, voucherId, token);
        compositeSubscription.add(subscription);
    }

}