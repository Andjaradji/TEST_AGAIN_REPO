package com.vexanium.vexgift.module.login.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IGoogleAuthPresenter extends BasePresenter{
    void checkToken(int id, String token);
}
