package com.vexanium.vexgift.module.voucher.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IVoucherPresenter extends BasePresenter{
    void requestMemberType(int userId);
    void requestPaymentType(int userId);
    void requestLocation(int userId);
    void requestRedeeemVoucher(int userId, int voucherCodeId, String vendorCode, String voucherCode, int voucherId);
}
