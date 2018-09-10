package com.vexanium.vexgift.module.security.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.security.model.IGoogleAuthStateInteractor;
import com.vexanium.vexgift.module.security.model.IGoogleAuthStateInteractorImpl;
import com.vexanium.vexgift.module.security.view.IGoogleAuthStateView;

import java.io.Serializable;

import rx.Subscription;

public class IGoogleAuthStatePresenterImpl extends BasePresenterImpl<IGoogleAuthStateView, Serializable> implements IGoogleAuthStatePresenter {
    private IGoogleAuthStateInteractor<Serializable> mGoogleAuthStateInteractor;
    private boolean mHasInit;

    public IGoogleAuthStatePresenterImpl(IGoogleAuthStateView view) {
        super(view);
        mGoogleAuthStateInteractor = new IGoogleAuthStateInteractorImpl();
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
    public void updateGoogle2faState(int id, String key, String token, boolean isSetToEnable) {
        Subscription subscription = mGoogleAuthStateInteractor.updateGoogle2faState(this, id, key, token, isSetToEnable);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestCode(int id) {
        Subscription subscription = mGoogleAuthStateInteractor.requestCode(this, id);
        compositeSubscription.add(subscription);
    }
}