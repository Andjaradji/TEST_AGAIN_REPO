package com.vexanium.vexgift.module.voucher.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public class IVoucherInteractorImpl implements IVoucherInteractor {
    @Override
    public Subscription requestMemberType(RequestCallback callback, int id) {
        return null;
    }

    @Override
    public Subscription requestPaymentType(RequestCallback callback, int id) {
        return null;
    }

    @Override
    public Subscription requestLocation(RequestCallback callback, int id) {
        return null;
    }
}

