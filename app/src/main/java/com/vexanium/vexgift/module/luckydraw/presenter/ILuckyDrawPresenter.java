package com.vexanium.vexgift.module.luckydraw.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface ILuckyDrawPresenter extends BasePresenter {
    void requestAllLuckyDrawList(int id);

    void requestLuckyDrawList(int id, int limit, int offset, int luckyDrawCategoryId, int memberTypeId, int paymentTypeId);

    void requestLuckyDrawById(int id, int luckyDrawId);

    void requestUserLuckyDrawList(int id);

    void buyLuckyDraw(int id, int luckyDrawId, int amount, String token);

    void setUserLuckyDrawAddress(int id, int userLuckyDrawId, String address);

}