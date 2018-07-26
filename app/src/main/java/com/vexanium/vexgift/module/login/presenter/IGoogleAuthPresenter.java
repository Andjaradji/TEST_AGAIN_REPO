package com.vexanium.vexgift.module.login.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IGoogleAuthPresenter extends BasePresenter{
    void checkToken(int id, String token);
}
