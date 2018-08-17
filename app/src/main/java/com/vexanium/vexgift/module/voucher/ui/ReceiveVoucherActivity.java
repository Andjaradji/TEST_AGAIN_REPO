package com.vexanium.vexgift.module.voucher.ui;

import android.os.Bundle;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.more.ui.MoreFragment;

@ActivityFragmentInject(contentViewId = R.layout.activity_receive_voucher, toolbarTitle = R.string.exchange_receive_voucher)
public class ReceiveVoucherActivity extends BaseActivity {


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
