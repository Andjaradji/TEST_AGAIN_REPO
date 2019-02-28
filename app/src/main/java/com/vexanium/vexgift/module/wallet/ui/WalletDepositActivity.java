package com.vexanium.vexgift.module.wallet.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenterImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.util.ViewUtil;

import net.glxn.qrgen.android.QRCode;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_wallet_deposit, toolbarTitle = R.string.wallet_deposit)
public class WalletDepositActivity extends BaseActivity<IWalletPresenter> implements IWalletView {

    User user;
    WalletResponse walletResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IWalletPresenterImpl(this);
        user = User.getCurrentUser(this);

        walletResponse = TableContentDaoUtil.getInstance().getWallet();
        if (walletResponse != null) {
            setCode(walletResponse.getWallet().getAddress());

            findViewById(R.id.iv_qr_code).setOnClickListener(this);
            findViewById(R.id.ll_code).setOnClickListener(this);
            findViewById(R.id.btn_copy).setOnClickListener(this);
        }

        ViewUtil.setOnClickListener(this, this,
                R.id.btn_copy, R.id.iv_qr_code, R.id.ll_code);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_qr_code:
            case R.id.ll_code:
            case R.id.btn_copy:
                if (walletResponse != null && walletResponse.getWallet() != null && !TextUtils.isEmpty(walletResponse.getWallet().getAddress())) {
                    StaticGroup.copyToClipboard(WalletDepositActivity.this, walletResponse.getWallet().getAddress());
                }
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
//            if(data instanceof)
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }

    }

    private void setCode(String code) {
        ((TextView) findViewById(R.id.tv_code)).setText(code);
        Bitmap bitmap = QRCode.from(code).withSize(150, 150).bitmap();
        ImageView view = findViewById(R.id.iv_qr_code);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .centerCrop()
                )
                .load(bitmap)
                .into(view);
    }

}
