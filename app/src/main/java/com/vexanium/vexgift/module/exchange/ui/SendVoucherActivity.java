package com.vexanium.vexgift.module.exchange.ui;

import android.os.Bundle;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.more.ui.MoreFragment;

@ActivityFragmentInject(contentViewId = R.layout.activity_send_voucher, toolbarTitle = R.string.exchange_send_voucher)
public class SendVoucherActivity extends BaseActivity {


    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }


    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

}
