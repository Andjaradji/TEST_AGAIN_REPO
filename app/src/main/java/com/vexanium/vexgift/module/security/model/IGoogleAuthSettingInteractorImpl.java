package com.vexanium.vexgift.module.security.model;

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

public class IGoogleAuthSettingInteractorImpl implements IGoogleAuthSettingInteractor {
    @Override
    public Subscription requestCode(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestGoogle2faCode(id).compose(RxUtil.<Google2faResponse>handleResult())
                .flatMap(new Func1<Google2faResponse, Observable<Google2faResponse>>() {
                    @Override
                    public Observable<Google2faResponse> call(Google2faResponse google2faResponse) {

                        KLog.json("HPtes", JsonUtil.toString(google2faResponse));
                        return Observable.just(google2faResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
