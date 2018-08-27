package com.vexanium.vexgift.module.voucher.presenter;

import com.vexanium.vexgift.base.BasePresenterImpl;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.voucher.model.IVoucherInteractor;
import com.vexanium.vexgift.module.voucher.model.IVoucherInteractorImpl;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;

import java.io.Serializable;

import rx.Subscription;

public class IVoucherPresenterImpl extends BasePresenterImpl<IVoucherView, Serializable> implements IVoucherPresenter {
    private IVoucherInteractor<Serializable> mInteractor;
    private boolean mHasInit;
    private boolean isLoadType = false;

    public IVoucherPresenterImpl(IVoucherView view) {
        super(view);
        mInteractor = new IVoucherInteractorImpl();
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
    public void requestComplete() {
        super.requestComplete();
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
    public void requestMemberType(int userId) {
        isLoadType = true;
        Subscription subscription = mInteractor.requestMemberType(this, userId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestVoucherType(int userId) {
        isLoadType = true;
        Subscription subscription = mInteractor.requestVoucherType(this, userId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestPaymentType(int userId) {
        isLoadType = true;
        Subscription subscription = mInteractor.requestPaymentType(this, userId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestLocation(int userId) {
        isLoadType = true;
        Subscription subscription = mInteractor.requestLocation(this, userId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestRedeeemVoucher(int userId, int voucherCodeId, String vendorCode, String voucherCode, int voucherId) {
        Subscription subscription = mInteractor.requestRedeemVoucher(this, userId, voucherCodeId, vendorCode, voucherCode, voucherId, "");
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestRedeeemVoucherWithAddress(int userId, int voucherCodeId, String address, String voucherCode, int voucherId) {
        Subscription subscription = mInteractor.requestRedeemVoucher(this, userId, voucherCodeId, "", voucherCode, voucherId, address);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestDeactivatedVoucher(int userId, int voucherCodeId) {
        Subscription subscription = mInteractor.requestDeactivatedVoucher(this, userId, voucherCodeId);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestGetGiftCode(int userId, int voucherCodeId, String token) {
        Subscription subscription = mInteractor.requestGetGiftCode(this, userId, voucherCodeId, token);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestClaimGiftCode(int userId, int voucherCodeId, String voucherGiftCode) {
        Subscription subscription = mInteractor.requestClaimGiftCode(this, userId, voucherCodeId, voucherGiftCode);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestCategories(int id) {
        Subscription subscription = mInteractor.requestCategories(this, id);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestVoucherList(int id) {
        Subscription subscription = mInteractor.requestVoucherList(this, id, 0);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestTokenList(int id) {
        Subscription subscription = mInteractor.requestVoucherList(this, id, 4);
        compositeSubscription.add(subscription);
    }

    @Override
    public void requestBuyVoucher(int userId, int voucherId, String token) {
        Subscription subscription = mInteractor.requestBuyVoucher(this, userId, voucherId, token);
        compositeSubscription.add(subscription);
    }


}