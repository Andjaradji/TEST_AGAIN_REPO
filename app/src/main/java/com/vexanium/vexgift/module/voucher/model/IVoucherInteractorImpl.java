package com.vexanium.vexgift.module.voucher.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

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

