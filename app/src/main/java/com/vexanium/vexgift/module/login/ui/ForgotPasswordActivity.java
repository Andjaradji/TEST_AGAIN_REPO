package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.presenter.IForgotPwPresenter;
import com.vexanium.vexgift.module.login.presenter.IForgotPwPresenterImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.register.ui.RegisterActivity;
import com.vexanium.vexgift.util.JsonUtil;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_forgot_password)
public class ForgotPasswordActivity extends BaseActivity<IForgotPwPresenter> implements ILoginView {

    EditText mEtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IForgotPwPresenterImpl(this);

        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.login_signup_button).setOnClickListener(this);
        findViewById(R.id.reset_password_button).setOnClickListener(this);

        mEtEmail = findViewById(R.id.et_email);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;

            case R.id.login_signup_button:
                Intent intent = new Intent(ForgotPasswordActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ForgotPasswordActivity.this.startActivity(intent);
                break;
            case R.id.reset_password_button:
                if(mEtEmail.getText()==null || !(mEtEmail.getText().toString().length() >= 5) || !Patterns.EMAIL_ADDRESS.matcher(mEtEmail.getText().toString()).matches()){

                    ((EditText) findViewById(R.id.et_email)).setError("This is not valid email");
                }else {
                    mPresenter.requestResetPassword(mEtEmail.getText().toString());

                }
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("ForgotPwActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {

        } else if(errorResponse != null){
            KLog.v("ForgotPwActivity handleResult error " + errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
            if (errorResponse.getMeta() != null && errorResponse.getMeta().isRequestError()) {
                StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
            }


        }else{
            Intent intent = new Intent(this,ForgotPasswordCodeActivity.class);
            intent.putExtra("reset_password_email",mEtEmail.getText().toString());
            startActivity(intent);
            finish();
        }
    }


}
