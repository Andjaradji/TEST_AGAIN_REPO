package com.vexanium.vexgift.module.premium.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.profile.model.IProfileInteractor;
import com.vexanium.vexgift.module.profile.model.IProfileInteractorImpl;
import com.vexanium.vexgift.module.profile.view.IProfileView;

import java.io.Serializable;

import rx.Subscription;

public class IPremiumPresenterImpl extends BasePresenterImpl<IProfileView, Serializable> implements IPremiumPresenter {
    private IProfileInteractor<Serializable> mInteractor;
    private boolean mHasInit;

    public IPremiumPresenterImpl(IProfileView view) {
        super(view);
        mInteractor = new IProfileInteractorImpl();
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
    public void requestKyc(int id) {
        Subscription subscription = mInteractor.requestKyc(this, id);
        compositeSubscription.add(subscription);
    }

}