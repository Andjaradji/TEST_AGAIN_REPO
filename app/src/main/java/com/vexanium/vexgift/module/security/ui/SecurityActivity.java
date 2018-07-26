package com.vexanium.vexgift.module.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.profile.ui.ChangePasswordActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.TpUtil;

@ActivityFragmentInject(contentViewId = R.layout.activity_security, toolbarTitle = R.string.security_toolbar_title)
public class SecurityActivity extends BaseActivity {
    Boolean isGoogle2faEnable;

    @Override
    protected void initView() {
        findViewById(R.id.security_change_password_button).setOnClickListener(this);
        findViewById(R.id.security_google_auth_button).setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TpUtil tpUtil = new TpUtil(this);
        isGoogle2faEnable =  tpUtil.getBoolean(TpUtil.KEY_GOOGLE2FA_STATE,false);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.security_change_password_button:
                intentToActivity(ChangePasswordActivity.class);
                break;
            case R.id.security_google_auth_button:
                if(!isGoogle2faEnable) {
                    intentToActivity(GoogleAuthSettingActivity.class);
                }else{
                    Intent intent = new Intent(SecurityActivity.this, GoogleAuthStateActivity.class);
                    intent.putExtra("state",true);
                    startActivity(intent);
                }
                break;
//            case R.id.security_fingerprint:
//
//                break;

        }
    }

    private void intentToActivity(Class<? extends Activity> activity) {
        Intent intent = new Intent(SecurityActivity.this, activity);
        startActivity(intent);
    }

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

}
