package com.vexanium.vexgift.module.luckydraw.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface ILuckyDrawPresenter extends BasePresenter {
    void requestAllLuckyDrawList(int id);
    void requestLuckyDrawList(int id, int limit, int offset, int luckyDrawCategoryId, int memberTypeId, int paymentTypeId);

}