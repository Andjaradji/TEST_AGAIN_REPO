package com.vexanium.vexgift.module.login.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IForgotPwPresenter extends BasePresenter {
    void requestResetPassword(String email);
    void requestResetPasswordCodeValidation(String email, String code);
    void requestResetPasswordTokenValidation(String email, String token, String pass, String confirmPass);
}
