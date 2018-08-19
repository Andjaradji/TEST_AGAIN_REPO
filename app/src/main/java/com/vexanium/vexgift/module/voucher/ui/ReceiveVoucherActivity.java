package com.vexanium.vexgift.module.voucher.ui;

import android.os.Bundle;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenter;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenterImpl;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_receive_voucher, toolbarTitle = R.string.exchange_receive_voucher)
public class ReceiveVoucherActivity extends BaseActivity<IVoucherPresenter> implements IVoucherView {

    private User user;

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IVoucherPresenterImpl(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }


    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

}
