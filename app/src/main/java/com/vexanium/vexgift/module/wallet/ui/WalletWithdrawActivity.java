package com.vexanium.vexgift.module.wallet.ui;

import android.os.Bundle;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Wallet;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenterImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.widget.CustomSeekBar;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_wallet_withdraw)
public class WalletWithdrawActivity extends BaseActivity<IWalletPresenter> implements IWalletView {

    User user;
    CustomSeekBar customSeekBar;
    WalletResponse walletResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IWalletPresenterImpl(this);
        user = User.getCurrentUser(this);

//        customSeekBar = findViewById(R.id.cs)

        walletResponse = TableContentDaoUtil.getInstance().getWallet();
        if(walletResponse != null && walletResponse.getWallet() != null){
            Wallet wallet = walletResponse.getWallet();


        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }
}
