package com.vexanium.vexgift.module.vexpoint.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IVexpointPresenter extends BasePresenter{
    void requestGetActAddress(int id);
    void requestSetActAddress(int id, String address, String token);
}
