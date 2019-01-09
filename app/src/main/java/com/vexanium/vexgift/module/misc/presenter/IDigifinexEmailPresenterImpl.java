package com.vexanium.vexgift.module.misc.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.model.ILoginInteractorImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.misc.model.IDigifinexEmailInteractor;
import com.vexanium.vexgift.module.misc.model.IDigifinexEmailInteractorImpl;
import com.vexanium.vexgift.module.misc.view.IDigifinexEmailView;

import java.io.Serializable;

import rx.Subscription;

public class IDigifinexEmailPresenterImpl extends BasePresenterImpl<IDigifinexEmailView, Serializable> implements IDigifinexEmailPresenter {
    private IDigifinexEmailInteractor<Serializable> mDigifinexEmailInteractor;
    private boolean mHasInit;

    public IDigifinexEmailPresenterImpl(IDigifinexEmailView view) {
        super(view);
        mDigifinexEmailInteractor = new IDigifinexEmailInteractorImpl();
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
    public void requestSubmitDigifinexEmailReferral(int userId, String email) {
        Subscription subscription = mDigifinexEmailInteractor.requestSubmitEmailReferral(this,userId, email);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestUserDifinexEmailReferred(int userId) {
        Subscription subscription = mDigifinexEmailInteractor.requestEmailReferredList(this,userId);
        compositeSubscription.add(subscription);
    }
}