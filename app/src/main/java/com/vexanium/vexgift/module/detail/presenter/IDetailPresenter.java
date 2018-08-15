package com.vexanium.vexgift.module.detail.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IDetailPresenter extends BasePresenter{
    void requestBuyVoucher(int userId, int voucherId, String token);
}
