package com.vexanium.vexgift.module.profile.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IProfileInteractorImpl implements IProfileInteractor {
    @Override
    public Subscription requestKyc(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestKyc(id).compose(RxUtil.<Kyc>handleResult())
                .flatMap(new Func1<Kyc, Observable<Kyc>>() {
                    @Override
                    public Observable<Kyc> call(Kyc kyc) {

                        KLog.json("HPtes", JsonUtil.toString(kyc));
                        return Observable.just(kyc);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription submitKyc(RequestCallback callback, Kyc kyc) {
        return RetrofitManager.getInstance(HostType.COMMON_API).submitKyc(kyc).compose(RxUtil.<Kyc>handleResult())
                .flatMap(new Func1<Kyc, Observable<Kyc>>() {
                    @Override
                    public Observable<Kyc> call(Kyc kyc) {

                        KLog.json("HPtes", JsonUtil.toString(kyc));
                        return Observable.just(kyc);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

