package com.vexanium.vexgift.module.vexpoint.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IVexpointInteractorImpl implements IVexpointInteractor {
    @Override
    public Subscription requestGetActAddress(RequestCallback callback, int id) {
        return null;
    }

    @Override
    public Subscription requestSetActAddress(RequestCallback callback, int id, String address, String token) {
        return null;
    }
}
