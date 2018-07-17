package com.vexanium.vexgift.module.login.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.model.ILoginInteractor;
import com.vexanium.vexgift.module.login.model.ILoginInteractorImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;

import java.io.Serializable;

import rx.Subscription;

public class ILoginPresenterImpl extends BasePresenterImpl<ILoginView, Serializable> implements ILoginPresenter {
    private ILoginInteractor<Serializable> mLoginInteractor;
    private boolean mHasInit;

    public ILoginPresenterImpl(ILoginView view) {
        super(view);
        mLoginInteractor = new ILoginInteractorImpl();
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
        Subscription subscription = mLoginInteractor.requestLogin(this, user);
        compositeSubscription.add(subscription);
    }
}