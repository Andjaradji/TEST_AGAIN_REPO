package com.vexanium.vexgift.module.referral.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.referral.model.IReferralInteractor;
import com.vexanium.vexgift.module.referral.model.IReferralInteractorImpl;
import com.vexanium.vexgift.module.referral.view.IReferralView;
import com.vexanium.vexgift.module.register.model.IRegisterInteractor;
import com.vexanium.vexgift.module.register.model.IRegisterInteractorImpl;
import com.vexanium.vexgift.module.register.presenter.IRegisterPresenter;
import com.vexanium.vexgift.module.register.view.IRegisterView;

import java.io.Serializable;

import rx.Subscription;

public class IReferralPresenterImpl extends BasePresenterImpl<IReferralView, Serializable> implements IReferralPresenter {
    private IReferralInteractor<Serializable> mRegisterInteractor;
    private boolean mHasInit;

    public IReferralPresenterImpl(IReferralView view) {
        super(view);
        mRegisterInteractor = new IReferralInteractorImpl();
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
    public void requestUserReferral(int userId) {
        Subscription subscription = mRegisterInteractor.requestUserReferral(this, userId);
        compositeSubscription.add(subscription);
    }
}