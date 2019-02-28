package com.vexanium.vexgift.module.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.dev21.fingerprintassistant.FingerprintHelper;
import com.dev21.fingerprintassistant.ResponseCode;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.profile.ui.ChangePasswordActivity;
import com.vexanium.vexgift.util.AnimUtil;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;

import rx.Observable;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_security, toolbarTitle = R.string.security_toolbar_title)
public class SecurityActivity extends BaseActivity {
    Boolean isGoogle2faEnable;
    boolean isFingerprintEnable;
    FingerprintHelper fingerPrintHelper;
    private Observable<Boolean> mNotifObservable;
    private Observable<Boolean> mGoogle2faObservable;
    private View notifView;
    private User user;

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);

        findViewById(R.id.security_change_password_button).setOnClickListener(this);
        findViewById(R.id.security_google_auth_button).setOnClickListener(this);
        findViewById(R.id.security_finger_button).setOnClickListener(this);

        notifView = findViewById(R.id.rl_notif_info);
        fingerPrintHelper = new FingerprintHelper(this, "vexgift-fingerprint-setting");

        mNotifObservable = RxBus.get().register("startSecNotifSlideDown", Boolean.class);
        mNotifObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                AnimUtil.transTopIn(notifView, true, 300);
            }
        });

        mGoogle2faObservable = RxBus.get().register("google2faUpdateState", Boolean.class);
        mGoogle2faObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                isGoogle2faEnable = aBoolean;
                updateSecurityItemView();
            }
        });

        isGoogle2faEnable = user.isAuthenticatorEnable();
        TpUtil tpUtil = new TpUtil(App.getContext());
        isFingerprintEnable = tpUtil.getBoolean(TpUtil.KEY_FINGERPRINT_LOCK, false);
        updateSecurityItemView();

        ViewUtil.setText(notifView, R.id.tv_notif_info, getString(R.string.security_note));
        CountDownTimer countDownTimer = new CountDownTimer(1500, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                if (!isGoogle2faEnable) {
                    RxBus.get().post("startSecNotifSlideDown", true);
                }
            }
        };

        countDownTimer.start();


        boolean isPassSet = User.getIsPasswordSet(this);

        if (isPassSet) {
            ViewUtil.setText(this, R.id.tv_change_pass, getString(R.string.security_change_password));
        } else {
            ViewUtil.setText(this, R.id.tv_change_pass, getString(R.string.security_add_password));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        user = User.getCurrentUser(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.security_change_password_button:
                intentToActivity(ChangePasswordActivity.class);
                break;
            case R.id.security_google_auth_button:
                if (!isGoogle2faEnable) {
                    intentToActivity(GoogleAuthSettingActivity.class);
                } else {
                    Intent intent = new Intent(SecurityActivity.this, GoogleAuthStateActivity.class);
                    intent.putExtra("state", true);
                    startActivity(intent);
                }
            case R.id.security_finger_button:
                initFingerprint();
                break;
        }
    }

    private void initFingerprint() {
        int responseCode = fingerPrintHelper.checkAndEnableFingerPrintService();
        switch (responseCode) {
            case ResponseCode.FINGERPRINT_SERVICE_INITIALISATION_SUCCESS:
//                if(isFingerprintEnable){
                isFingerprintEnable = !isFingerprintEnable;
                TpUtil tpUtil = new TpUtil(App.getContext());
                tpUtil.put(TpUtil.KEY_FINGERPRINT_LOCK, isFingerprintEnable);
                updateSecurityItemView();
//                }
                if (isFingerprintEnable) {
                    toast("Fingerprint activated");
                } else {
                    toast("Fingerprint deactivated");
                }
//                toast("Fingerprint sensor service initialisation success");
                break;
            case ResponseCode.OS_NOT_SUPPORTED:
                toast("OS doesn't support fingerprint api");
                break;
            case ResponseCode.FINGER_PRINT_SENSOR_UNAVAILABLE:
                toast("Fingerprint sensor not found");
                break;
            case ResponseCode.ENABLE_FINGER_PRINT_SENSOR_ACCESS:
                toast("Provide access to use fingerprint sensor");
                break;
            case ResponseCode.NO_FINGER_PRINTS_ARE_ENROLLED:
                toast("No fingerprints found");
                break;
            case ResponseCode.FINGERPRINT_SERVICE_INITIALISATION_FAILED:
                toast("Fingerprint service initialisation failed");
                break;
            case ResponseCode.DEVICE_NOT_KEY_GUARD_SECURED:
                toast("Device is not key guard protected");
                break;
        }
    }

    private void updateSecurityItemView() {
        TextView tvStatus = findViewById(R.id.google_auth_status);
        if (isGoogle2faEnable) {
            notifView.setVisibility(View.GONE);
            tvStatus.setText(getString(R.string.security_on));
            tvStatus.setTypeface(App.bold);
            tvStatus.setTextColor(ColorUtil.getColor(App.getContext(), R.color.vex_orange));
        } else {
            tvStatus.setText(getString(R.string.security_off));
            tvStatus.setTypeface(App.regular);
            tvStatus.setTextColor(ColorUtil.getColor(App.getContext(), R.color.material_black_sub_text_color));
        }

        TextView tvFingerprintStatus = findViewById(R.id.fingerprint_status);
        if (isFingerprintEnable) {
            notifView.setVisibility(View.GONE);
            tvFingerprintStatus.setText(getString(R.string.security_on));
            tvFingerprintStatus.setTypeface(App.bold);
            tvFingerprintStatus.setTextColor(ColorUtil.getColor(App.getContext(), R.color.vex_orange));
        } else {
            tvFingerprintStatus.setText(getString(R.string.security_off));
            tvFingerprintStatus.setTypeface(App.regular);
            tvFingerprintStatus.setTextColor(ColorUtil.getColor(App.getContext(), R.color.material_black_sub_text_color));
        }

    }

    private void intentToActivity(Class<? extends Activity> activity) {
        Intent intent = new Intent(SecurityActivity.this, activity);
        startActivity(intent);
    }

}
