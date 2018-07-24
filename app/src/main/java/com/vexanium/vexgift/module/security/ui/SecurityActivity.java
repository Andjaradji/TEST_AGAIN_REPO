package com.vexanium.vexgift.module.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.login.ui.GoogleAuthActivity;
import com.vexanium.vexgift.module.profile.ui.ChangePasswordActivity;
import com.vexanium.vexgift.util.ClickUtil;

@ActivityFragmentInject(contentViewId = R.layout.activity_security, toolbarTitle = R.string.security_toolbar_title)
public class SecurityActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        findViewById(R.id.security_change_password_button).setOnClickListener(this);
        findViewById(R.id.security_google_auth_button).setOnClickListener(this);
        findViewById(R.id.security_fingerprint).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(ClickUtil.isFastDoubleClick()){
            return;
        }
        super.onClick(v);
        switch (v.getId()){
            case R.id.security_change_password_button:
                intentToActivity(ChangePasswordActivity.class);
                break;
            case R.id.security_google_auth_button:
                intentToActivity(GoogleAuthActivity.class);
                break;
            case R.id.security_fingerprint:

                break;

        }
    }

    private void intentToActivity(Class<? extends Activity> activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
