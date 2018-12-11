package com.vexanium.vexgift.module.luckydraw.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.home.model.IHomeInteractorImpl;
import com.vexanium.vexgift.module.home.view.IHomeView;
import com.vexanium.vexgift.module.luckydraw.model.ILuckyDrawInteractor;
import com.vexanium.vexgift.module.luckydraw.model.ILuckyDrawInteractorImpl;
import com.vexanium.vexgift.module.luckydraw.view.ILuckyDrawView;

import java.io.Serializable;

import rx.Subscription;

public class ILuckyDrawPresenterImpl extends BasePresenterImpl<ILuckyDrawView, Serializable> implements ILuckyDrawPresenter {
    private ILuckyDrawInteractor<Serializable> LuckyDrawInteractor;
    private boolean mHasInit;

    public ILuckyDrawPresenterImpl(ILuckyDrawView view) {
        super(view);
        LuckyDrawInteractor = new ILuckyDrawInteractorImpl();
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
    public void requestAllLuckyDrawList(int id) {
        Subscription subscription = LuckyDrawInteractor.requestLuckyDraw(this, id, -1, -1, -1, -1, -1);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestLuckyDrawList(int id, int limit, int offset, int luckyDrawCategoryId, int memberTypeId, int paymentTypeId) {
        Subscription subscription = LuckyDrawInteractor.requestLuckyDraw(this, id, limit, offset, luckyDrawCategoryId, memberTypeId, paymentTypeId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestLuckyDrawById(int id, int luckyDrawId) {
        Subscription subscription = LuckyDrawInteractor.requestLuckyDraw(this, id, luckyDrawId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestUserLuckyDrawList(int id) {
        Subscription subscription = LuckyDrawInteractor.requestUserLuckyDrawList(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void buyLuckyDraw(int id, int luckyDrawId,int amount, String token) {
        Subscription subscription = LuckyDrawInteractor.buyLuckyDraw(this, id, luckyDrawId, amount, token);
        compositeSubscription.add(subscription);
    }

    @Override
    public void setUserLuckyDrawAddress(int id, int userLuckyDrawId, String address) {
        Subscription subscription = LuckyDrawInteractor.setUserLuckyDrawAddress(this, id, userLuckyDrawId, address);
        compositeSubscription.add(subscription);
    }
}
