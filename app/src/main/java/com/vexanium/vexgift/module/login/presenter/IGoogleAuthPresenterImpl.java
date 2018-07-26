package com.vexanium.vexgift.module.login.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.model.IGoogleAuthInteractor;
import com.vexanium.vexgift.module.login.model.IGoogleAuthInteractorImpl;
import com.vexanium.vexgift.module.login.view.IGoogleAuthView;
import com.vexanium.vexgift.module.login.view.ILoginView;

import java.io.Serializable;

import rx.Subscription;

public class IGoogleAuthPresenterImpl extends BasePresenterImpl<IGoogleAuthView, Serializable> implements IGoogleAuthPresenter {
    private IGoogleAuthInteractor<Serializable> mInteractor;
    private boolean mHasInit;

    public IGoogleAuthPresenterImpl(IGoogleAuthView view) {
        super(view);
        mInteractor = new IGoogleAuthInteractorImpl();
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
    public void checkToken(int id, String token) {
        Subscription subscription = mInteractor.checkToken(this, id, token);
        compositeSubscription.add(subscription);
    }


}