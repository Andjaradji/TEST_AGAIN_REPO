package com.vexanium.vexgift.module.login.ui;

import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

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
