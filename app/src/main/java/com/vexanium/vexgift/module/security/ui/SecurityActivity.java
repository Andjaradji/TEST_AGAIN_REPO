package com.vexanium.vexgift.module.security.ui;

import android.app.Activity;
import android.content.Intent;
<<<<<<< HEAD
=======
import android.support.v7.app.AppCompatActivity;
>>>>>>> d911e6baaf9fd6c66e8190d0a6a85d81352d07dc
import android.os.Bundle;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
<<<<<<< HEAD
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.profile.ui.ChangePasswordActivity;
=======
import com.vexanium.vexgift.module.login.ui.GoogleAuthActivity;
import com.vexanium.vexgift.module.profile.ui.ChangePasswordActivity;
import com.vexanium.vexgift.util.ClickUtil;
>>>>>>> d911e6baaf9fd6c66e8190d0a6a85d81352d07dc

@ActivityFragmentInject(contentViewId = R.layout.activity_security, toolbarTitle = R.string.security_toolbar_title)
public class SecurityActivity extends BaseActivity {

    @Override
    protected void initView() {
        findViewById(R.id.security_change_password_button).setOnClickListener(this);
        findViewById(R.id.security_google_auth_button).setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
<<<<<<< HEAD
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){

=======
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
>>>>>>> d911e6baaf9fd6c66e8190d0a6a85d81352d07dc
            case R.id.security_change_password_button:
                intentToActivity(ChangePasswordActivity.class);
                break;
            case R.id.security_google_auth_button:
<<<<<<< HEAD
                intentToActivity(GoogleAuthSettingActivity.class);
                break;

        }
=======
                intentToActivity(GoogleAuthActivity.class);
                break;
            case R.id.security_fingerprint:

                break;

        }
    }

    private void intentToActivity(Class<? extends Activity> activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
>>>>>>> d911e6baaf9fd6c66e8190d0a6a85d81352d07dc
    }

    private void intentToActivity(Class<? extends Activity> activity){
        Intent intent = new Intent(SecurityActivity.this, activity);
        startActivity(intent);
    }

    public static MoreFragment newInstance(){
        return new MoreFragment();
    }

}
