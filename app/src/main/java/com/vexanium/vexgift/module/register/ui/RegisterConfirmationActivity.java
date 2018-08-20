package com.vexanium.vexgift.module.register.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.module.login.ui.GoogleAuthActivity;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.register.presenter.IRegisterPresenter;
import com.vexanium.vexgift.module.register.presenter.IRegisterPresenterImpl;
import com.vexanium.vexgift.module.register.view.IRegisterView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;
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

@ActivityFragmentInject(contentViewId = R.layout.activity_register_confirmation, withLoadingAnim = true)
public class RegisterConfirmationActivity extends BaseActivity<IRegisterPresenter> implements IRegisterView {

    private String email;
    private Subscription timeSubsription;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IRegisterPresenterImpl(this);

        if (getIntent().hasExtra("user")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("user"))) {
                user = (User) JsonUtil.toObject(getIntent().getStringExtra("user"), User.class);

                email = user.getEmail();

                String message = String.format(getString(R.string.register_confirmation_text), email);
                ViewUtil.setText(this, R.id.tv_confirmation_text, message);
            }
        }

        ViewUtil.setOnClickListener(this, this,
                R.id.resend_button,
                R.id.confirmation_button);

    }

    @Override
    public void handleResult(Serializable data, final HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof UserLoginResponse) {
                UserLoginResponse response = (UserLoginResponse) data;

                StaticGroup.removeReferrerData();
                StaticGroup.userSession = response.user.getSessionKey();
                StaticGroup.isPasswordSet = response.isPasswordSet;

                User.setIsPasswordSet(this.getApplicationContext(), response.isPasswordSet);
                User.updateCurrentUser(this.getApplicationContext(), response.user);
                User.google2faLock(response.user);

                new VexDialog.Builder(this)
                        .title(getString(R.string.succsess))
                        .content(getString(R.string.register_confirmation_success))
                        .optionType(DialogOptionType.OK)
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                executeMain(false);
                            }
                        })
                        .autoDismiss(false)
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .show();
            }
            KLog.v("RegisterConfirmationActivity", "handleResult: Success" + data);
        } else if (errorResponse != null) {
            if (errorResponse.getMeta() != null) {
                if (errorResponse.getMeta().getStatus() == 200) {
                    new VexDialog.Builder(this)
                            .title(getString(R.string.succsess))
                            .content(errorResponse.getMeta().getMessage())
                            .optionType(DialogOptionType.OK)
                            .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                @Override
                                public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    if (errorResponse.getMeta().getMessage().contains("resend")) {
                                        setLastEmailSendTime();
                                    } else {
                                        finish();
                                    }
                                }
                            })
                            .autoDismiss(false)
                            .cancelable(false)
                            .canceledOnTouchOutside(false)
                            .show();
                } else if (errorResponse.getMeta().isRequestError()) {
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
                } else {
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
                }
            }
        } else {
            new VexDialog.Builder(this)
                    .title(getString(R.string.succsess))
                    .content(getString(R.string.register_confirmation_resend_success))
                    .optionType(DialogOptionType.OK)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            setLastEmailSendTime();
                        }
                    })
                    .autoDismiss(false)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .show();
        }
    }


    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) return;
        switch (v.getId()) {
            case R.id.resend_button:
                if (isAbleToResend()) {
                    mPresenter.requestResendEmail(user.getId());
                }
                break;
            case R.id.confirmation_button:
                if (user != null) {
                    String code = ((EditText) findViewById(R.id.et_code)).getText().toString();
                    if (!TextUtils.isEmpty(code)) {
                        mPresenter.requestEmailConfirmation(user.getId(), code);

                    } else {
                        ((EditText) findViewById(R.id.et_code)).setError(getString(R.string.validate_empty_field));
                    }
                }
                break;
            default:
        }
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        KLog.v("RegisterConfirmationActivity", "onPause: ");
        super.onPause();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
            timeSubsription = null;
        }
    }

    @Override
    protected void onStart() {
        KLog.v("RegisterConfirmationActivity", "onStart: ");
        super.onStart();
        startDateTimer();
    }

    @Override
    protected void onDestroy() {
        KLog.v("RegisterConfirmationActivity", "onDestroy: ");
        super.onDestroy();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        KLog.v("RegisterConfirmationActivity", "onResume: ");
        super.onResume();
        startDateTimer();
    }

    private void executeMain(boolean isAlreadyLogin) {
        User user = User.getCurrentUser(this);
        if ((User.isLocalSessionEnded() || User.isGoogle2faLocked() || !isAlreadyLogin) && user.isAuthenticatorEnable()) {
            Intent intent = new Intent(getApplicationContext(), GoogleAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        finish();
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
                            if (!StaticGroup.isScreenOn(RegisterConfirmationActivity.this, true)) {
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
