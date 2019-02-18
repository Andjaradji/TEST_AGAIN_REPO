package com.vexanium.vexgift.module.main.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.main.model.IMainInteractor;
import com.vexanium.vexgift.module.main.model.IMainInteractorImpl;
import com.vexanium.vexgift.module.main.view.IMainView;

import java.io.Serializable;

import rx.Subscription;

public class IMainPresenterImpl extends BasePresenterImpl<IMainView, Serializable> implements IMainPresenter {
    private IMainInteractor<Serializable> mMainInteractor;
    private boolean mHasInit;


    public IMainPresenterImpl(IMainView view) {
        super(view);
        mMainInteractor = new IMainInteractorImpl();
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
    public void submitAffiliateProgramEntry(int userId, int affiliateProgramId, String vals) {
        Subscription subscription = mMainInteractor.submitAffiliateProgramEntry(this, userId, affiliateProgramId, vals);
        compositeSubscription.add(subscription);
    }

    @Override
    public void getAffiliateProgramEntries(int userId, int affiliateProgramId) {
        Subscription subscription = mMainInteractor.getAffilateProgramEntries(this, userId, affiliateProgramId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void getAffiliateProgram(int userId, int affiliateProgramId) {
        Subscription subscription = mMainInteractor.getAffilateProgram(this, userId, affiliateProgramId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void getAffiliatePrograms(int userId) {
        Subscription subscription = mMainInteractor.getAffilatePrograms(this, userId);
        compositeSubscription.add(subscription);
    }
}
