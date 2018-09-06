package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_forgot_password_reset)
public class ForgotPasswordResetActivity extends BaseActivity<IForgotPwPresenter> implements ILoginView {

    EditText mEtPass, mEtConfirmPass;

    String email, resetToken;

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
        findViewById(R.id.new_reset_password_button).setOnClickListener(this);

        mEtPass = findViewById(R.id.et_new_password);
        mEtConfirmPass = findViewById(R.id.et_new_password_confirmation);

        email = "";
        email = getIntent().getStringExtra("reset_password_email");

        resetToken = "";
        resetToken = getIntent().getStringExtra("reset_password_token");

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;

            case R.id.login_signup_button:
                Intent intent = new Intent(ForgotPasswordResetActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ForgotPasswordResetActivity.this.startActivity(intent);
                break;
            case R.id.new_reset_password_button:
                if (mEtPass.getText() == null && mEtPass.getText().toString().length() < 3) {
                    mEtPass.setError("Password field must be filled");
                } else if (mEtConfirmPass == null && mEtConfirmPass.getText().toString().length() < 3) {
                    mEtPass.setError("Password confirmation field must be filled");
                } else if (!mEtPass.getText().toString().equals(mEtConfirmPass.getText().toString())) {
                    mEtPass.setError("Password and pass confirmation must be the same");
                } else {
                    mPresenter.requestResetPasswordTokenValidation(email, resetToken, mEtPass.getText().toString(), mEtConfirmPass.getText().toString());
                }
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {

        } else if (errorResponse != null) {
            KLog.v("ForgotPwCodeActivity handleResult error " + errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
            if (errorResponse.getMeta() != null && errorResponse.getMeta().isRequestError()) {
                StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
            }
        } else {
            new VexDialog.Builder(this)
                    .title(getString(R.string.forgot_password_success_title))
                    .content(getString(R.string.forgot_password_success_body))
                    .optionType(DialogOptionType.OK)
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .autoDismiss(false)
                    .show();
        }
    }

}
