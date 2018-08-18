package com.vexanium.vexgift.module.premium.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IPremiumPresenter extends BasePresenter{
    void requestPremiumList(int userId);
    void purchasePremium(int userId, int duration, int price, String currency);
    void getActAddress(int userId);
   
}
