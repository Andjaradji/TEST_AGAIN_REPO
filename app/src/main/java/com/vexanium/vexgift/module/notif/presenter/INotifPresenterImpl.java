package com.vexanium.vexgift.module.notif.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.notif.model.INotifInteractor;
import com.vexanium.vexgift.module.notif.model.INotifInteractorImpl;
import com.vexanium.vexgift.module.notif.view.INotifView;

import java.io.Serializable;

/**
 * Created by mac on 11/17/17.
 */

public class INotifPresenterImpl extends BasePresenterImpl<INotifView, Serializable> implements INotifPresenter {
    private INotifInteractor<Serializable> mNotifInteractor;
    private boolean mHasInit;

    public INotifPresenterImpl(INotifView view) {
        super(view);
        mNotifInteractor = new INotifInteractorImpl();
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
