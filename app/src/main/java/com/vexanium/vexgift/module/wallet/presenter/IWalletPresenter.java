package com.vexanium.vexgift.module.wallet.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IWalletPresenter extends BasePresenter {
    void requestGetWallet(int id);

    void requestCreateWallet(int id);

    void requestDoWithdraw(int id, int walletId);
}