package com.vexanium.vexgift.module.vexpoint.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.model.ILoginInteractor;
import com.vexanium.vexgift.module.login.model.ILoginInteractorImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.vexpoint.model.IVexpointInteractor;
import com.vexanium.vexgift.module.vexpoint.model.IVexpointInteractorImpl;
import com.vexanium.vexgift.module.vexpoint.view.IVexpointView;

import java.io.Serializable;

import rx.Subscription;

public class IVexpointPresenterImpl extends BasePresenterImpl<IVexpointView, Serializable> implements IVexpointPresenter {
    private IVexpointInteractor<Serializable> mInteractor;
    private boolean mHasInit;

    public IVexpointPresenterImpl(IVexpointView view) {
        super(view);
        mInteractor = new IVexpointInteractorImpl();
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
    public void requestSetActAddress(int id, String address, String token) {
        Subscription subscription = mInteractor.requestSetActAddress(this, id, address, token);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestGetActAddress(int id) {
        Subscription subscription = mInteractor.requestGetActAddress(this, id);
        compositeSubscription.add(subscription);
    }
}