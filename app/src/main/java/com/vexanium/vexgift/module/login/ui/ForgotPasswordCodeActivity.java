package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.ResetPasswordCodeResponse;
import com.vexanium.vexgift.module.login.presenter.IForgotPwPresenter;
import com.vexanium.vexgift.module.login.presenter.IForgotPwPresenterImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.register.ui.RegisterActivity;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_forgot_password_code)
public class ForgotPasswordCodeActivity extends BaseActivity<IForgotPwPresenter> implements ILoginView {

    TextView mTvTitle, mTvBody;
    EditText mEtCode;
    String email, resetToken;
    private Subscription timeSubsription;

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
        findViewById(R.id.resend_email_button).setOnClickListener(this);
        findViewById(R.id.confirm_code_button).setOnClickListener(this);

        mEtCode = findViewById(R.id.et_code);

        email = "";
        email = getIntent().getStringExtra("reset_password_email");

        resetToken = "";

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;

            case R.id.login_signup_button:
                Intent intent = new Intent(ForgotPasswordCodeActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ForgotPasswordCodeActivity.this.startActivity(intent);
                break;
            case R.id.resend_email_button:
                if (isAbleToResend()) {
                    mPresenter.requestResetPassword(email);
                }
                break;
            case R.id.confirm_code_button:
                if (mEtCode.getText() == null) {
                    mEtCode.setError("Invalid code");
                } else {
                    mPresenter.requestResetPasswordCodeValidation(email, mEtCode.getText().toString());
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        KLog.v("ForgotPasswordCodeActivity", "onPause: ");
        super.onPause();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
            timeSubsription = null;
        }
    }

    @Override
    protected void onStart() {
        KLog.v("ForgotPasswordCodeActivity", "onStart: ");
        super.onStart();
        startDateTimer();
    }

    @Override
    protected void onDestroy() {
        KLog.v("ForgotPasswordCodeActivity", "onDestroy: ");
        super.onDestroy();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        KLog.v("ForgotPasswordCodeActivity", "onResume: ");
        super.onResume();
        startDateTimer();
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof ResetPasswordCodeResponse) {
                if (((ResetPasswordCodeResponse) data).getResetPasswordToken() != null) {
                    resetToken = ((ResetPasswordCodeResponse) data).getResetPasswordToken();
                    //setCurrentView(1);
                    Intent intent = new Intent(this, ForgotPasswordResetActivity.class);
                    intent.putExtra("reset_password_token", resetToken);
                    intent.putExtra("reset_password_email", email);
                    startActivity(intent);
                    finish();
                }
            }

        } else if (errorResponse != null) {
            KLog.v("ForgotPwCodeActivity handleResult error " + errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
            if (errorResponse.getMeta() != null && errorResponse.getMeta().isRequestError()) {
                StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
            }
        } else {
            new VexDialog.Builder(this)
                    .title("Email sent")
                    .content("Check your email for validation code")
                    .optionType(DialogOptionType.OK)
                    .canceledOnTouchOutside(true)
                    .cancelable(true)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            setLastEmailSendTime();
                        }
                    })
                    .autoDismiss(true)
                    .show();
        }
    }

    public void setLastEmailSendTime() {
        TpUtil tpUtil = new TpUtil(this);
        tpUtil.put(TpUtil.KEY_LAST_EMAIL_SEND_TIME, System.currentTimeMillis());
    }

    public long getLastEmailSendTime() {
        TpUtil tpUtil = new TpUtil(this);
        return tpUtil.getLong(TpUtil.KEY_LAST_EMAIL_SEND_TIME, 0);
    }

    public void setLastEmailSendTime(long l) {
        TpUtil tpUtil = new TpUtil(this);
        tpUtil.put(TpUtil.KEY_LAST_EMAIL_SEND_TIME, l);
    }

    public boolean isAbleToResend() {
        TpUtil tpUtil = new TpUtil(this);
        long lastEmailSendTime = tpUtil.getLong(TpUtil.KEY_LAST_EMAIL_SEND_TIME, 0);
        return ((System.currentTimeMillis() - lastEmailSendTime) > StaticGroup.EMAIL_RESEND_TIME);
    }

    private void startDateTimer() {
        if (timeSubsription == null && StaticGroup.isScreenOn(this, true)) {
            timeSubsription = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            //KLog.v("Date Time called");
                            if (!StaticGroup.isScreenOn(ForgotPasswordCodeActivity.this, true)) {
                                if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
                                    timeSubsription.unsubscribe();
                                }
                            } else {
                                setWatchText();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                        }
                    });
        }
    }

    private void setWatchText() {

        if (getLastEmailSendTime() != 0) {

            TextView mTvCountdownVp = findViewById(R.id.tv_resend);

            TpUtil tpUtil = new TpUtil(this);
            long lastEmailSendTime = tpUtil.getLong(TpUtil.KEY_LAST_EMAIL_SEND_TIME, 0);

            Calendar now = Calendar.getInstance();
            Calendar next = Calendar.getInstance();

            next.setTimeInMillis(lastEmailSendTime + StaticGroup.EMAIL_RESEND_TIME);

            long remainTime = next.getTimeInMillis() - now.getTimeInMillis();

            if (remainTime < 0) {
                mTvCountdownVp.setText(getString(R.string.register_confirmation_resend));
                setLastEmailSendTime(0);

            } else {
                String time = String.format(Locale.getDefault(), "(%ds)",
                        TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));

                String sBtn = String.format(getString(R.string.register_confirmation_resend_time), time);
                mTvCountdownVp.setText(sBtn);
            }
        }
    }
}
