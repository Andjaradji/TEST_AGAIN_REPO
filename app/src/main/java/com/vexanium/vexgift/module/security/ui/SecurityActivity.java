package com.vexanium.vexgift.module.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.profile.ui.ChangePasswordActivity;
import com.vexanium.vexgift.module.profile.ui.MyProfileActivity;
import com.vexanium.vexgift.util.AnimUtil;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;

import rx.Observable;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.ConstantGroup.KYC_NONE;

@ActivityFragmentInject(contentViewId = R.layout.activity_security, toolbarTitle = R.string.security_toolbar_title)
public class SecurityActivity extends BaseActivity {
    Boolean isGoogle2faEnable;
    private Observable<Boolean> mNotifObservable;
    private Observable<Boolean> mGoogle2faObservable;
    private View notifView;

    @Override
    protected void initView() {
        findViewById(R.id.security_change_password_button).setOnClickListener(this);
        findViewById(R.id.security_google_auth_button).setOnClickListener(this);

        notifView = findViewById(R.id.rl_notif_info);

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
                updateSecurityItemView(aBoolean);
            }
        });

        isGoogle2faEnable = User.isGoogle2faEnable();
        updateSecurityItemView(isGoogle2faEnable);

        if (!isGoogle2faEnable) {
            ViewUtil.setText(notifView, R.id.tv_notif_info, getString(R.string.security_note));
            CountDownTimer countDownTimer = new CountDownTimer(1500, 500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    RxBus.get().post("startSecNotifSlideDown", true);
                }
            };
            countDownTimer.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
                break;
//            case R.id.security_fingerprint:
//
//                break;

        }
    }

    private void updateSecurityItemView(boolean isGoogle2faEnable) {
        TextView tvStatus = findViewById(R.id.google_auth_status);
        if (isGoogle2faEnable) {
            tvStatus.setText(getString(R.string.security_on));
            tvStatus.setTypeface(App.bold);
            tvStatus.setTextColor(ColorUtil.getColor(App.getContext(), R.color.vex_orange));
        } else {
            notifView.setVisibility(View.GONE);
            tvStatus.setText(getString(R.string.security_off));
            tvStatus.setTypeface(App.regular);
            tvStatus.setTextColor(ColorUtil.getColor(App.getContext(), R.color.material_black_sub_text_color));
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
