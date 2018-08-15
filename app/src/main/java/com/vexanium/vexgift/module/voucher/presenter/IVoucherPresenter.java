package com.vexanium.vexgift.module.voucher.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IVoucherPresenter extends BasePresenter{
    void requestMemberType(int userId);
    void requestPaymentType(int userId);
    void requestLocation(int userId);
}
