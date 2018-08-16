package com.vexanium.vexgift.module.voucher.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.voucher.model.IVoucherInteractor;
import com.vexanium.vexgift.module.voucher.model.IVoucherInteractorImpl;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;

import java.io.Serializable;

import rx.Subscription;

public class IVoucherPresenterImpl extends BasePresenterImpl<IVoucherView, Serializable> implements IVoucherPresenter {
    private IVoucherInteractor<Serializable> mInteractor;
    private boolean mHasInit;

    public IVoucherPresenterImpl(IVoucherView view) {
        super(view);
        mInteractor = new IVoucherInteractorImpl();
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
    public void requestMemberType(int userId) {
        Subscription subscription = mInteractor.requestMemberType(this, userId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestPaymentType(int userId) {
        Subscription subscription = mInteractor.requestPaymentType(this, userId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestLocation(int userId) {
        Subscription subscription = mInteractor.requestLocation(this, userId);
        compositeSubscription.add(subscription);
    }


}