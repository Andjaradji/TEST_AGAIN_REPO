package com.vexanium.vexgift.module.security.ui;

import android.os.Bundle;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_google_auth_enable, toolbarTitle = R.string.security_google_auth_label)
public class GoogleAuthEnableActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_next).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
