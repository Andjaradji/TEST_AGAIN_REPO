package com.vexanium.vexgift.module.profile.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.model.ILoginInteractor;
import com.vexanium.vexgift.module.login.model.ILoginInteractorImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.profile.model.IProfileInteractor;
import com.vexanium.vexgift.module.profile.model.IProfileInteractorImpl;
import com.vexanium.vexgift.module.profile.view.IProfileView;

import java.io.Serializable;

import rx.Subscription;

public class IProfilePresenterImpl extends BasePresenterImpl<IProfileView, Serializable> implements IProfilePresenter {
    private IProfileInteractor<Serializable> mInteractor;
    private boolean mHasInit;

    public IProfilePresenterImpl(IProfileView view) {
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

    @Override
    public void submitKyc(Kyc kyc) {
        Subscription subscription = mInteractor.submitKyc(this, kyc);
        compositeSubscription.add(subscription);
    }
}