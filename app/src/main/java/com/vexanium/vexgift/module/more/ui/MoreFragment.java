package com.vexanium.vexgift.module.more.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.socks.library.KLog;
import com.vexanium.vexgift.BuildConfig;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.premium.ui.PremiumMemberActivity;
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

import java.util.Locale;

import rx.Observable;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.ConstantGroup.KYC_ACCEPTED;
import static com.vexanium.vexgift.app.ConstantGroup.KYC_NONE;
import static com.vexanium.vexgift.app.ConstantGroup.SUPPORT_EMAIL;

@ActivityFragmentInject(contentViewId = R.layout.fragment_more)
public class MoreFragment extends BaseFragment {

    private Observable<Boolean> mNotifObservable;
    private View notifView;
    private User user;

    public MoreFragment() {
    }

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Override
    protected void initView(final View fragmentRootView) {
        user = User.getCurrentUser(this.getActivity());
        ViewUtil.setText(fragmentRootView, R.id.tv_toolbar_title, "MORE");

        fragmentRootView.findViewById(R.id.more_myprofile_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_setting_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_security_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_premium_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_merchant_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_feedback_buttton).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_problem_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_about_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_privacy_policy).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_terms_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_logout_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_gp_button).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_faq).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_youtube).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_instagram).setOnClickListener(this);
        fragmentRootView.findViewById(R.id.more_guide).setOnClickListener(this);

        App.setTextViewStyle((ViewGroup) fragmentRootView);

        notifView = fragmentRootView.findViewById(R.id.rl_notif_info);
        notifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                intentToActivity(MyProfileActivity.class);
            }
        });

        mNotifObservable = RxBus.get().register("startNotifSlideDown", Boolean.class);
        mNotifObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                AnimUtil.transTopIn(notifView, true, 300);
            }
        });

        // TODO: 26/07/18 get KYC Status
        int kycStatus = StaticGroup.kycStatus;
        if (user.isKycApprove()) {
            kycStatus = KYC_ACCEPTED;
        }
        if (kycStatus == KYC_NONE) {
            ViewUtil.setText(notifView, R.id.tv_notif_info, getString(R.string.notif_kyc));
            CountDownTimer countDownTimer = new CountDownTimer(2000, 500) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    if (!user.isSubmitKyc())
                        RxBus.get().post("startNotifSlideDown", true);
                }
            };
            countDownTimer.start();
        }

        if (user != null) {
            if (user.isPremiumMember()) {
                fragmentRootView.findViewById(R.id.iv_premium_crown).setVisibility(View.VISIBLE);
            } else {
                fragmentRootView.findViewById(R.id.iv_premium_crown).setVisibility(View.GONE);
            }
        }

        ViewUtil.setText(fragmentRootView, R.id.tv_version,
                String.format(getString(R.string.appversion_need_update_version_current), BuildConfig.VERSION_NAME));

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("More Fragment View")
                .putContentType("More")
                .putContentId("more"));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("NotifFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        user = User.getCurrentUser(this.getActivity());
        if (user.isKycApprove()) {
            notifView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNotifObservable != null) {
            RxBus.get().unregister("startNotifSlideDown", mNotifObservable);
            mNotifObservable = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        String subject;
        String message;

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
            case R.id.more_premium_button:
                intentToActivity(PremiumMemberActivity.class);
                break;
            case R.id.more_merchant_button:
                toast("Merchant list is not available");
                break;
            case R.id.more_feedback_buttton:
                subject = String.format(Locale.getDefault(), "[FEEDBACK] #%04d", user.getId());
                message = "Hi Vexgift Support!\nI've feedback with ...";
                StaticGroup.shareWithEmail(this.getActivity(), SUPPORT_EMAIL, subject, message);
                break;
            case R.id.more_problem_button:
//                subject = String.format(Locale.getDefault(), "[PROBLEM] #%04d", user.getId());
//                message = "Hi Vexgift Support!\nI've problem with...";
//                StaticGroup.shareWithEmail(this.getActivity(), SUPPORT_EMAIL, subject, message);
                intentToActivity(SupportActivity.class);
                break;
            case R.id.more_gp_button:
                StaticGroup.openVexgiftGooglePlay(MoreFragment.this.getActivity());
                break;
            case R.id.more_about_button:
                intentToActivity(AboutActivity.class);
                break;
            case R.id.more_privacy_policy:
                intentToActivity(PrivacyActivity.class);
                break;
            case R.id.more_terms_button:
                intentToActivity(TermActivity.class);
                break;
            case R.id.more_youtube:
                StaticGroup.openAndroidBrowser(this.getActivity(), "https://www.youtube.com/channel/UC39E4RaDoa45RZ4h6TEatwg/videos");
                break;
            case R.id.more_instagram:
                StaticGroup.openAndroidBrowser(this.getActivity(), "https://www.instagram.com/vexgift");
                break;
            case R.id.more_faq:
                intentToActivity(FaqActivity.class);
                break;
            case R.id.more_guide:
                String url = "http://www.vexgift.com/guide/en";
                if (StaticGroup.isInIDLocale()) {
                    url = "http://www.vexgift.com/guide/id";
                }
                ((MainActivity) getActivity()).openDeepLink("in.app.web?t="+Uri.encode("Vexgift Guide","UTF-8")+"&l=" + Uri.encode(url, "UTF-8"));

                break;
            case R.id.more_logout_button:
                doLogout();
                break;
            default:
                super.onClick(v);
        }
    }

    private void intentToActivity(Class<? extends Activity> activity) {
        Intent intent = new Intent(MoreFragment.this.getActivity(), activity);
        startActivity(intent);
    }

    private void doLogout() {
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

}
