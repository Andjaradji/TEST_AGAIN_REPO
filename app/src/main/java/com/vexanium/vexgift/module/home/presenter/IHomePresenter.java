package com.vexanium.vexgift.module.home.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IHomePresenter extends BasePresenter {
    void requestVoucherList(int id);

    void requestKyc(int id);

}