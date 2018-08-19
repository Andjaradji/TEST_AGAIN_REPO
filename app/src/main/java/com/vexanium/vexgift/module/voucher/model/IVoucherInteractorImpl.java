package com.vexanium.vexgift.module.voucher.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.VoucherGiftCode;
import com.vexanium.vexgift.bean.response.VoucherCodeResponse;
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

    @Override
    public Subscription requestRedeemVoucher(RequestCallback callback, int userId, int voucherCodeId, String vendorCode, String voucherCode, int voucherId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestRedeemVoucher(userId, voucherCodeId, vendorCode, voucherCode, voucherId).compose(RxUtil.<VoucherCodeResponse>handleResult())
                .flatMap(new Func1<VoucherCodeResponse, Observable<VoucherCodeResponse>>() {
                    @Override
                    public Observable<VoucherCodeResponse> call(VoucherCodeResponse userAddressResponse) {
                        KLog.json("HPtes", JsonUtil.toString(userAddressResponse));
                        return Observable.just(userAddressResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestDeactivatedVoucher(RequestCallback callback, int userId, int voucherCodeId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestDeactivateVoucher(userId, voucherCodeId).compose(RxUtil.<VoucherCodeResponse>handleResult())
                .flatMap(new Func1<VoucherCodeResponse, Observable<VoucherCodeResponse>>() {
                    @Override
                    public Observable<VoucherCodeResponse> call(VoucherCodeResponse userAddressResponse) {
                        KLog.json("HPtes", JsonUtil.toString(userAddressResponse));
                        return Observable.just(userAddressResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestGetGiftCode(RequestCallback callback, int userId, int voucherCodeId, String token) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestGetGiftCode(userId, voucherCodeId, token).compose(RxUtil.<VoucherGiftCode>handleResult())
                .flatMap(new Func1<VoucherGiftCode, Observable<VoucherGiftCode>>() {
                    @Override
                    public Observable<VoucherGiftCode> call(VoucherGiftCode voucherGiftCode) {
                        KLog.json("HPtes", JsonUtil.toString(voucherGiftCode));
                        return Observable.just(voucherGiftCode);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestClaimGiftCode(RequestCallback callback, int userId, int voucherCodeId, String voucherGiftCode) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestClaimGiftCode(userId, voucherCodeId, voucherGiftCode).compose(RxUtil.<VoucherCodeResponse>handleResult())
                .flatMap(new Func1<VoucherCodeResponse, Observable<VoucherCodeResponse>>() {
                    @Override
                    public Observable<VoucherCodeResponse> call(VoucherCodeResponse userAddressResponse) {
                        KLog.json("HPtes", JsonUtil.toString(userAddressResponse));
                        return Observable.just(userAddressResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

