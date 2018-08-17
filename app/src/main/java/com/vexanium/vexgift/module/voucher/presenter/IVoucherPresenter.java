package com.vexanium.vexgift.module.voucher.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.callback.RequestCallback;

public interface IVoucherPresenter extends BasePresenter {
    void requestMemberType(int userId);

    void requestPaymentType(int userId);

    void requestLocation(int userId);

    void requestRedeeemVoucher(int userId, int voucherCodeId, String vendorCode, String voucherCode, int voucherId);

    void requestDeactivatedVoucher(int userId, int voucherCodeId);

    void requestGetGiftCode(int userId, int voucherCodeId, String token);

    void requestClaimGiftCode(int userId, int voucherCodeId, String token);
}
