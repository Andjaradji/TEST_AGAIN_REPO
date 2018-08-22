package com.vexanium.vexgift.module.login.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.model.IForgotPwInteractor;
import com.vexanium.vexgift.module.login.model.IForgotPwInteractorImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;

import java.io.Serializable;

import rx.Subscription;

public class IForgotPwPresenterImpl extends BasePresenterImpl<ILoginView, Serializable> implements IForgotPwPresenter {
    private IForgotPwInteractor<Serializable> mResetPwInteractor;
    private boolean mHasInit;

    public IForgotPwPresenterImpl(ILoginView view) {
        super(view);
        mResetPwInteractor = new IForgotPwInteractorImpl();
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
    public void requestResetPassword(String email) {
        Subscription subscription = mResetPwInteractor.requestResetPassword(this, email);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestResetPasswordCodeValidation(String email, String code) {
        Subscription subscription = mResetPwInteractor.requestResetPasswordCodeValidation(this, email,code);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestResetPasswordTokenValidation(String email, String token, String pass, String confirmPass) {
        Subscription subscription = mResetPwInteractor.requestResetPasswordTokenValidation(this, email,token,pass,confirmPass);
        compositeSubscription.add(subscription);
    }
}