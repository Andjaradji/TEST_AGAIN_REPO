package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.register.ui.RegisterActivity;
import com.vexanium.vexgift.util.ClickUtil;

@ActivityFragmentInject(contentViewId = R.layout.activity_google_auth)
public class GoogleAuthActivity extends BaseActivity {

    @Override
    protected void initView() {
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.login_signup_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(ClickUtil.isFastDoubleClick())return;
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;

            case R.id.login_signup_button:
                toast("No implemented yet");
                break;
            case R.id.login_button:
                toast("No implemented yet");
                break;
        }
    }
}
