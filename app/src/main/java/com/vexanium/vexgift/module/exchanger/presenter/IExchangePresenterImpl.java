package com.vexanium.vexgift.module.exchanger.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.exchanger.model.IExchangeInteractor;
import com.vexanium.vexgift.module.exchanger.model.IExchangeInteractorImpl;
import com.vexanium.vexgift.module.exchanger.view.IExchangeView;

import java.io.Serializable;

import rx.Subscription;

/**
 * Created by mac on 11/17/17.
 */

public class IExchangePresenterImpl extends BasePresenterImpl<IExchangeView, Serializable> implements IExchangePresenter {
    private IExchangeInteractor<Serializable> mExchangeInteractor;
    private boolean mHasInit;

    public IExchangePresenterImpl(IExchangeView view) {
        super(view);
        mExchangeInteractor = new IExchangeInteractorImpl();
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
    public void requestExchangeList(int userId) {
        Subscription subscription = mExchangeInteractor.requestExchangeList(this, userId);
        compositeSubscription.add(subscription);
    }
}
