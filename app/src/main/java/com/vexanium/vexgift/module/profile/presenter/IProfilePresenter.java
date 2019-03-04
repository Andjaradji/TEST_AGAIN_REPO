package com.vexanium.vexgift.module.profile.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.Kyc;

public interface IProfilePresenter extends BasePresenter {
    void requestKyc(int id);

    void submitKyc(Kyc kyc);

    void changePass(int id, String oldPass, String newPass);

    void getCountries();

    void updateUserProfile(int id, String username);
}
