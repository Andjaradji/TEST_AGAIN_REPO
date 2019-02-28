package com.vexanium.vexgift.module.referral.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.WalletReferralResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenter;
import com.vexanium.vexgift.module.wallet.presenter.IWalletPresenterImpl;
import com.vexanium.vexgift.module.wallet.view.IWalletView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.LocaleUtil;

import java.io.Serializable;

import static com.vexanium.vexgift.app.ConstantGroup.DEFAULT_REF_GUIDANCE_LINK;
import static com.vexanium.vexgift.app.StaticGroup.isAppAvailable;

@ActivityFragmentInject(contentViewId = R.layout.activity_referral_special_event, toolbarTitle = R.string.referral_invite_others)
public class ReferralSpecialEventActivity extends BaseActivity<IWalletPresenter> implements IWalletView {

    TextView mTvInvitedCount, mTvInviteLink, mTvReferralCode, mTvReferralTitle;
    ImageView mIvCopy, mIvCodeCopy, mIvWhatsapp, mIvTelegram, mIvLine, mIvTwitter, mIvFb, mIvShare;
    String mPlaystoreLink;
    String mShareText;
    String refGuidanceUrl = "";
    User user;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void initView() {
        mPresenter = new IWalletPresenterImpl(this);
        user = User.getCurrentUser(this);
        mTvInvitedCount = findViewById(R.id.tv_referral_invited_user_count);
        mTvInviteLink = findViewById(R.id.tv_referral_link);
        mTvReferralCode = findViewById(R.id.tv_referral_code);
        mTvReferralTitle = findViewById(R.id.tv_referral_title);
        mIvCopy = findViewById(R.id.iv_referral_copy);
        mIvCodeCopy = findViewById(R.id.iv_referral_code_copy);
        mIvWhatsapp = findViewById(R.id.referral_share_whatsapp_button);
        mIvTelegram = findViewById(R.id.referral_share_telegram_button);
        mIvLine = findViewById(R.id.referral_share_line_button);
        mIvTwitter = findViewById(R.id.referral_share_twitter_button);
        mIvFb = findViewById(R.id.referral_share_fb_button);
        mIvShare = findViewById(R.id.referral_share_sm_button);

        mIvCopy.setOnClickListener(this);
        mIvCodeCopy.setOnClickListener(this);
        mIvWhatsapp.setOnClickListener(this);
        mIvTelegram.setOnClickListener(this);
        mIvLine.setOnClickListener(this);
        mIvTwitter.setOnClickListener(this);
        mIvFb.setOnClickListener(this);
        mIvShare.setOnClickListener(this);

        findViewById(R.id.tv_referral_note).setOnClickListener(this);
        findViewById(R.id.cv_invited_user).setOnClickListener(this);

        mTvInvitedCount.setText("" + 0);

        mPlaystoreLink = "https://play.google.com/store/apps/details?id=com.vexanium.vexgift&referrer=utm_source%3Dvexgift%26utm_medium%3Dinvite%26i%3D" + user.getReferralCode();

        mTvInviteLink.setText(mPlaystoreLink);
        mTvReferralCode.setText(user.getReferralCode());
        mPresenter.requestGetWalletReferral(user.getId());

        String defaultShareText = "VexGift is a great way to get free vouchers. Check it out here \n";
        mShareText = defaultShareText + mPlaystoreLink;

        refGuidanceUrl = StaticGroup.getStringValFromSettingKey("referral_guidance_link");
        if (TextUtils.isEmpty(refGuidanceUrl)) {
            refGuidanceUrl = DEFAULT_REF_GUIDANCE_LINK;
        }

        if (LocaleUtil.getLanguage(this).equalsIgnoreCase("zh")) {
            findViewById(R.id.tv_referral_share_on_sm).setVisibility(View.GONE);
            findViewById(R.id.rl_referral_socmed_share).setVisibility(View.GONE);
            defaultShareText = "VexGift is a great way to get free vouchers. Check it out here \n";
            mShareText = defaultShareText + " " + ConstantGroup.CHINA_WEB_LINK + "\nMy code : " + user.getReferralCode();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_referral_copy:
                StaticGroup.copyToClipboard(this, mShareText);
                break;
            case R.id.iv_referral_code_copy:
                StaticGroup.copyToClipboard(this, mTvReferralCode.getText().toString());
                break;
            case R.id.referral_share_whatsapp_button:
                shareWhatsApp(mShareText);
                break;
            case R.id.referral_share_telegram_button:
                shareTelegram(mShareText);
                break;
            case R.id.referral_share_line_button:
                shareLine(mShareText);
                break;
            case R.id.referral_share_twitter_button:
                shareTwitter(mShareText);
                break;
            case R.id.referral_share_fb_button:
                shareFacebook(mShareText);
                break;
            case R.id.referral_share_sm_button:
                shareSm(mShareText);
                break;
            case R.id.cv_invited_user:
                intent = new Intent(ReferralSpecialEventActivity.this, ReferralSpecialEventDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_referral_note:
                StaticGroup.openAndroidBrowser(ReferralSpecialEventActivity.this, refGuidanceUrl);
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof WalletReferralResponse) {
                WalletReferralResponse walletReferralResponse = (WalletReferralResponse) data;
                TableContentDaoUtil.getInstance().saveWalletReferralsToDb(JsonUtil.toString(walletReferralResponse));

                int referralsCount = walletReferralResponse.getReferralsCount();
                mTvInvitedCount.setText(String.valueOf(referralsCount));
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    private void shareSm(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                text);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void shareFacebook(String text) {
        String fullUrl = "https://m.facebook.com/sharer.php?u=..";
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.facebook.katana",
                    "com.facebook.katana.ShareLinkActivity");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(sharingIntent);

        } catch (Exception e) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.setPackage("com.facebook.lite");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
            } catch (Exception er) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra(Intent.EXTRA_TEXT, text);
                i.setData(Uri.parse(fullUrl));
                startActivity(i);
            }

        }
    }

    private void shareTwitter(String text) {
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.twitter.android", "com.twitter.android.PostActivity");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(sharingIntent);
        } catch (Exception e) {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://mobile.twitter.com/compose/tweet"));
            startActivity(i);
        }
    }

    void shareLine(String text) {
        final String appName = "jp.naver.line.android";
        final boolean isAppInstalled = isAppAvailable(this, appName);
        if (isAppInstalled) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.setPackage(appName);
            myIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(myIntent, "Share with"));
        } else {
            Toast.makeText(this, "Line not Installed", Toast.LENGTH_SHORT).show();
        }
    }

    void shareTelegram(String text) {
        final String appName = "org.telegram.messenger";
        final boolean isAppInstalled = isAppAvailable(this, appName);
        if (isAppInstalled) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.setPackage(appName);
            myIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(myIntent, "Share with"));
        } else {
            Toast.makeText(this, "Telegram not Installed", Toast.LENGTH_SHORT).show();
        }
    }

    void shareWhatsApp(String text) {
        final String appName = "com.whatsapp";
        final boolean isAppInstalled = isAppAvailable(this, appName);
        if (isAppInstalled) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.setPackage(appName);
            myIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(myIntent, "Share with"));
        } else {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }

}
