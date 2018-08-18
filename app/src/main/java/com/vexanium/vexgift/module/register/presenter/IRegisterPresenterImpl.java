package com.vexanium.vexgift.module.register.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.register.model.IRegisterInteractor;
import com.vexanium.vexgift.module.register.model.IRegisterInteractorImpl;
import com.vexanium.vexgift.module.register.view.IRegisterView;

import java.io.Serializable;

import rx.Subscription;

public class IRegisterPresenterImpl extends BasePresenterImpl<IRegisterView, Serializable> implements IRegisterPresenter {
    private IRegisterInteractor<Serializable> mRegisterInteractor;
    private boolean mHasInit;

    public IRegisterPresenterImpl(IRegisterView view) {
        super(view);
        mRegisterInteractor = new IRegisterInteractorImpl();
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
    public void requestLogin(User user) {
        Subscription subscription = mRegisterInteractor.requestRegister(this, user);
        compositeSubscription.add(subscription);
    }
    @Override
    public void requestRegister(User user) {
        Subscription subscription = mRegisterInteractor.requestRegister(this, user);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestEmailConfirmation(int userId, String code) {
        Subscription subscription = mRegisterInteractor.requestEmailConfirmation(this, userId, code);
        compositeSubscription.add(subscription);
    }
}