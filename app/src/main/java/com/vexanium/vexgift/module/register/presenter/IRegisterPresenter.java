package com.vexanium.vexgift.module.register.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IRegisterPresenter extends BasePresenter{
    void requestLogin(User user);
}
