package com.vexanium.vexgift.module.deposit.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IDepositPresenter extends BasePresenter {
    void requestDepositList(int id);

    void requestUserDepositList(int id);

    void requestDeposit(int id, int depositId, int depositOptionId);
}
