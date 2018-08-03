package com.vexanium.vexgift.module.exchange.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.profile.ui.ChangePasswordActivity;
import com.vexanium.vexgift.module.security.ui.GoogleAuthSettingActivity;
import com.vexanium.vexgift.module.security.ui.GoogleAuthStateActivity;
import com.vexanium.vexgift.util.AnimUtil;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;

import rx.Observable;
import rx.functions.Action1;

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
