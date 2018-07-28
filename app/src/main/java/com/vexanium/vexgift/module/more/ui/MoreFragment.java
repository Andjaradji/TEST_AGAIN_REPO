package com.vexanium.vexgift.module.more.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.module.profile.ui.MyProfileActivity;
import com.vexanium.vexgift.module.security.ui.SecurityActivity;
import com.vexanium.vexgift.module.setting.ui.SettingActivity;
import com.vexanium.vexgift.util.AnimUtil;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;


import rx.Observable;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.ConstantGroup.KYC_ACCEPTED;
import static com.vexanium.vexgift.app.ConstantGroup.KYC_NONE;

@ActivityFragmentInject(contentViewId = R.layout.fragment_more)
public class MoreFragment extends BaseFragment {

    private Observable<Boolean> mNotifObservable;
    private View notifView;

    @Override
    protected void initView(final View fragmentRootView) {
        ViewUtil.setText(fragmentRootView, R.id.tv_toolbar_title, "MORE");

        fragmentRootView.findViewById(R.id.more_myprofile_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_setting_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_security_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_merchant_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_feedback_buttton).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_problem_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_logout_button).setOnClickListener(this);

        App.setTextViewStyle((ViewGroup) fragmentRootView);

        notifView = fragmentRootView.findViewById(R.id.rl_notif_info);
        notifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.isFastDoubleClick())return;
                intentToActivity(MyProfileActivity.class);
            }
        });

        mNotifObservable = RxBus.get().register("startNotifSlideDown", Boolean.class);
        mNotifObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                AnimUtil.transTopIn(notifView,true, 300);
            }
        });

        // TODO: 26/07/18 get KYC Status
        int kycStatus = StaticGroup.kycStatus;
        if(kycStatus == KYC_NONE){
            ViewUtil.setText(notifView, R.id.tv_notif_info,getString(R.string.notif_kyc));
            CountDownTimer countDownTimer = new CountDownTimer(2000,500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    RxBus.get().post("startNotifSlideDown", true);
                }
            };
            countDownTimer.start();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("NotifFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        super.onClick(v);
        switch (v.getId()) {
            case R.id.more_myprofile_button:
                intentToActivity(MyProfileActivity.class);
                break;
            case R.id.more_setting_button:
                intentToActivity(SettingActivity.class);
                break;
            case R.id.more_security_button:
                intentToActivity(SecurityActivity.class);
                break;
            case R.id.more_merchant_button:
                break;
            case R.id.more_feedback_buttton:
                break;
            case R.id.more_problem_button:
                break;
            case R.id.more_gp_button:
                break;
            case R.id.more_about_button:
                break;
            case R.id.more_privacy_policy:
                break;
            case R.id.more_logout_button:
                doLogout();
                break;
        }
    }

    private void intentToActivity(Class<? extends Activity> activity) {
        Intent intent = new Intent(MoreFragment.this.getActivity(), activity);
        startActivity(intent);
    }

    private void doLogout(){
        new VexDialog.Builder(MoreFragment.this.getActivity())
                .title(getString(R.string.logout_title))
                .content(getString(R.string.logout_desc))
                .optionType(DialogOptionType.YES_NO)
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        StaticGroup.logOutClear(MoreFragment.this.getActivity(), 0);
                    }
                })
                .autoDismiss(true).show();

    }


    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

}
