package com.vexanium.vexgift.module.voucher.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IVoucherInteractor<T> {
    Subscription requestMemberType(RequestCallback<T> callback, int id);

    Subscription requestPaymentType(RequestCallback<T> callback, int id);

    Subscription requestLocation(RequestCallback<T> callback, int id);

    Subscription requestRedeemVoucher(RequestCallback<T> callback, int userId, int voucherCodeId, String vendorCode, String voucherCode, int voucherId);

    Subscription requestDeactivatedVoucher(RequestCallback<T> callback, int userId, int voucherCodeId);

    Subscription requestGetGiftCode(RequestCallback<T> callback, int userId, int voucherCodeId, String token);

    Subscription requestClaimGiftCode(RequestCallback<T> callback, int userId, int voucherCodeId, String token);
}
