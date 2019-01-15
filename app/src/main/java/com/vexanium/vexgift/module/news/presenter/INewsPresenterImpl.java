package com.vexanium.vexgift.module.news.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.news.model.INewsInteractor;
import com.vexanium.vexgift.module.news.model.INewsInteractorImpl;
import com.vexanium.vexgift.module.news.view.INewsView;

import java.io.Serializable;

import rx.Subscription;

public class INewsPresenterImpl extends BasePresenterImpl<INewsView, Serializable> implements INewsPresenter {
    private INewsInteractor<Serializable> mNewsInteractor;
    private boolean mHasInit;


    public INewsPresenterImpl(INewsView view) {
        super(view);
        mNewsInteractor = new INewsInteractorImpl();
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
    public void requestNews(User user) {
        Subscription subscription = mNewsInteractor.requestNews(this, user.getId());
        compositeSubscription.add(subscription);
    }
}
