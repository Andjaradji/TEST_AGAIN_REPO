package com.vexanium.vexgift.module.login.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface ILoginPresenter extends BasePresenter{
    void requestLogin(User user);
}
