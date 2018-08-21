package com.vexanium.vexgift.module.voucher.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.CategoryResponse;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.MemberTypeResponse;
import com.vexanium.vexgift.bean.response.PaymentTypeResponse;
import com.vexanium.vexgift.bean.response.VoucherCodeResponse;
import com.vexanium.vexgift.bean.response.VoucherGiftCodeResponse;
import com.vexanium.vexgift.bean.response.VoucherTypeResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
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
        return RetrofitManager.getInstance(HostType.COMMON_API).requestMemberTypes(id).compose(RxUtil.<MemberTypeResponse>handleResult())
                .flatMap(new Func1<MemberTypeResponse, Observable<MemberTypeResponse>>() {
                    @Override
                    public Observable<MemberTypeResponse> call(MemberTypeResponse response) {
                        KLog.json("HPtes", JsonUtil.toString(response));
                        return Observable.just(response);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestPaymentType(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestPaymentTypes(id).compose(RxUtil.<PaymentTypeResponse>handleResult())
                .flatMap(new Func1<PaymentTypeResponse, Observable<PaymentTypeResponse>>() {
                    @Override
                    public Observable<PaymentTypeResponse> call(PaymentTypeResponse response) {
                        KLog.json("HPtes", JsonUtil.toString(response));
                        return Observable.just(response);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestVoucherType(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestVoucherTypes(id).compose(RxUtil.<VoucherTypeResponse>handleResult())
                .flatMap(new Func1<VoucherTypeResponse, Observable<VoucherTypeResponse>>() {
                    @Override
                    public Observable<VoucherTypeResponse> call(VoucherTypeResponse response) {
                        KLog.json("HPtes", JsonUtil.toString(response));
                        return Observable.just(response);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
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
        return RetrofitManager.getInstance(HostType.COMMON_API).requestGetGiftCode(userId, voucherCodeId, token).compose(RxUtil.<VoucherGiftCodeResponse>handleResult())
                .flatMap(new Func1<VoucherGiftCodeResponse, Observable<VoucherGiftCodeResponse>>() {
                    @Override
                    public Observable<VoucherGiftCodeResponse> call(VoucherGiftCodeResponse voucherGiftCode) {
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

    @Override
    public Subscription requestVoucherList(RequestCallback callback, int id, int voucherType) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestVoucherList(id, voucherType).compose(RxUtil.<VouchersResponse>handleResult())
                .flatMap(new Func1<VouchersResponse, Observable<VouchersResponse>>() {
                    @Override
                    public Observable<VouchersResponse> call(VouchersResponse vouchersResponse) {
                        return Observable.just(vouchersResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));

    }

    @Override
    public Subscription requestCategories(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestCategories(id).compose(RxUtil.<CategoryResponse>handleResult())
                .flatMap(new Func1<CategoryResponse, Observable<CategoryResponse>>() {
                    @Override
                    public Observable<CategoryResponse> call(CategoryResponse categoryResponse) {
                        return Observable.just(categoryResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));

    }

    @Override
    public Subscription requestBuyVoucher(RequestCallback callback, int userId, int voucherId, String token) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestBuyVoucher(userId, voucherId, token).compose(RxUtil.<EmptyResponse>handleResult())
                .flatMap(new Func1<EmptyResponse, Observable<EmptyResponse>>() {
                    @Override
                    public Observable<EmptyResponse> call(EmptyResponse emptyResponse) {

                        KLog.json("HPtes", JsonUtil.toString(emptyResponse));
                        return Observable.just(emptyResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}

