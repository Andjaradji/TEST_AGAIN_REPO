package com.vexanium.vexgift.module.detail.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IDetailPresenter extends BasePresenter{
    void requestBuyVoucher(int userId, int voucherId, String token);
}
