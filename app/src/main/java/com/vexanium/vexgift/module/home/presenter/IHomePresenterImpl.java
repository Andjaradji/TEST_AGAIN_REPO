package com.vexanium.vexgift.module.home.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.home.model.IHomeInteractor;
import com.vexanium.vexgift.module.home.model.IHomeInteractorImpl;
import com.vexanium.vexgift.module.home.view.IHomeView;

import java.io.Serializable;

public class IHomePresenterImpl  extends BasePresenterImpl<IHomeView, Serializable> implements IHomePresenter {
    private IHomeInteractor<Serializable> mHomeInteractor;
    private boolean mHasInit;

    public IHomePresenterImpl(IHomeView view) {
        super(view);
        mHomeInteractor = new IHomeInteractorImpl();
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


}
