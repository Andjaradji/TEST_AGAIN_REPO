package com.vexanium.vexgift.module.box.model;

import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.VoucherCode;
import com.vexanium.vexgift.bean.response.UserVouchersResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.RxUtil;

import java.util.Collections;
import java.util.Comparator;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IBoxInteractorImpl implements IBoxInteractor {
    @Override
    public Subscription requestUserVoucherList(RequestCallback callback, int id) {

        return RetrofitManager.getInstance(HostType.COMMON_API).requestUserVoucherList(id).compose(RxUtil.<UserVouchersResponse>handleResult())
                .flatMap(new Func1<UserVouchersResponse, Observable<UserVouchersResponse>>() {
                    @Override
                    public Observable<UserVouchersResponse> call(UserVouchersResponse vouchersResponse) {
                        Collections.sort(vouchersResponse.getVoucherCodes(), new Comparator<VoucherCode>() {
                            @Override
                            public int compare(VoucherCode voucherCode, VoucherCode t1) {
                                return voucherCode.getCreatedAt().compareTo(t1.getCreatedAt());
                            }
                        });
                        // TODO: 15/08/18 sort token 
                        return Observable.just(vouchersResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
