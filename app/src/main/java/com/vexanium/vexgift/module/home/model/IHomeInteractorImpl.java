package com.vexanium.vexgift.module.home.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import java.util.Collections;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IHomeInteractorImpl implements IHomeInteractor {
    @Override
    public Subscription requestVoucherList(RequestCallback callback, int id) {

        return RetrofitManager.getInstance(HostType.COMMON_API).requestVoucherList(id, 0).compose(RxUtil.<VouchersResponse>handleResult())
                .flatMap(new Func1<VouchersResponse, Observable<VouchersResponse>>() {
                    @Override
                    public Observable<VouchersResponse> call(VouchersResponse vouchersResponse) {
                        Collections.shuffle(vouchersResponse.getVouchers());
                        return Observable.just(vouchersResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

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


}
