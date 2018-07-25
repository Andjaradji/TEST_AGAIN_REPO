package com.vexanium.vexgift.module.security.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.security.model.IGoogleAuthSettingInteractor;
import com.vexanium.vexgift.module.security.model.IGoogleAuthSettingInteractorImpl;
import com.vexanium.vexgift.module.security.view.IGoogleAuthSettingView;

import java.io.Serializable;

import rx.Subscription;

public class IGoogleAuthSettingPresenterImpl extends BasePresenterImpl<IGoogleAuthSettingView, Serializable> implements IGoogleAuthSettingPresenter {
    private IGoogleAuthSettingInteractor<Serializable> mGoogleAuthSettingInteractor;
    private boolean mHasInit;

    public IGoogleAuthSettingPresenterImpl(IGoogleAuthSettingView view) {
        super(view);
        mGoogleAuthSettingInteractor = new IGoogleAuthSettingInteractorImpl();
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
    public void requestCode(int id) {
        Subscription subscription = mGoogleAuthSettingInteractor.requestCode(this, id);
        compositeSubscription.add(subscription);
    }
}