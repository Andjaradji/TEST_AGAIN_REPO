package com.vexanium.vexgift.module.premium.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.Kyc;

public interface IPremiumPresenter extends BasePresenter{
    void requestPremiumList(int userId);
    void purchasePremium(int userId, int duration, int price, String currency);
    void getActAddress(int userId);
   
}
