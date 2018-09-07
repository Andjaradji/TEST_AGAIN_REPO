package com.vexanium.vexgift.module.vexpoint.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IVexpointPresenter extends BasePresenter {
    void requestGetActAddress(int id);

    void requestSetActAddress(int id, String address, String token);

    void requestVpLog(int id);
}
