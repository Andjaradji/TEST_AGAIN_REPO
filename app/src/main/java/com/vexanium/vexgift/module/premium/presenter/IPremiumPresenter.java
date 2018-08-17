package com.vexanium.vexgift.module.premium.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.Kyc;

public interface IPremiumPresenter extends BasePresenter{
    void requestKyc(int id);
    void submitKyc(Kyc kyc);
    void changePass(int id, String oldPass, String newPass);
}
