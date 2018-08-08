package com.vexanium.vexgift.module.voucher.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IVoucherPresenter extends BasePresenter{
    void requestLogin(User user);
    void requestRegister(User user);
}
