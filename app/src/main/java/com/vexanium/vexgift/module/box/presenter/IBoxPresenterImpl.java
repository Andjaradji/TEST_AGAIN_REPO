package com.vexanium.vexgift.module.box.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.box.model.IBoxInteractor;
import com.vexanium.vexgift.module.box.model.IBoxInteractorImpl;
import com.vexanium.vexgift.module.box.view.IBoxView;

import java.io.Serializable;

import rx.Subscription;

public class IBoxPresenterImpl extends BasePresenterImpl<IBoxView, Serializable> implements IBoxPresenter {
    private IBoxInteractor<Serializable> mInteractor;
    private boolean mHasInit;

    public IBoxPresenterImpl(IBoxView view) {
        super(view);
        mInteractor = new IBoxInteractorImpl();
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
    public void requestUserVoucherList(int id) {
        Subscription subscription = mInteractor.requestUserVoucherList(this, id);
        compositeSubscription.add(subscription);
    }
}
