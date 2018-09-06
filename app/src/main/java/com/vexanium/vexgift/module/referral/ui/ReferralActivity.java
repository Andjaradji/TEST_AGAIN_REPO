package com.vexanium.vexgift.module.referral.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserReferralResponse;
import com.vexanium.vexgift.module.referral.presenter.IReferralPresenter;
import com.vexanium.vexgift.module.referral.presenter.IReferralPresenterImpl;
import com.vexanium.vexgift.module.referral.view.IReferralView;

import java.io.Serializable;

import static com.vexanium.vexgift.app.StaticGroup.isAppAvailable;

@ActivityFragmentInject(contentViewId = R.layout.activity_referral, toolbarTitle = R.string.referral_invite_others)
public class ReferralActivity extends BaseActivity<IReferralPresenter> implements IReferralView {

    TextView mTvInvitedCount, mTvInviteLink;
    ImageView mIvCopy, mIvWhatsapp, mIvTelegram, mIvLine, mIvTwitter, mIvFb, mIvShare;

    String mShareText;
    User user;

    @Override
    protected void initView() {
        mPresenter = new IReferralPresenterImpl(this);
        user = User.getCurrentUser(this);
        mTvInvitedCount = findViewById(R.id.tv_referral_invited_user_count);
        mTvInviteLink = findViewById(R.id.tv_referral_link);
        mIvCopy = findViewById(R.id.iv_referral_copy);
        mIvWhatsapp = findViewById(R.id.referral_share_whatsapp_button);
        mIvTelegram = findViewById(R.id.referral_share_telegram_button);
        mIvLine = findViewById(R.id.referral_share_line_button);
        mIvTwitter = findViewById(R.id.referral_share_twitter_button);
        mIvFb = findViewById(R.id.referral_share_fb_button);
        mIvShare = findViewById(R.id.referral_share_sm_button);

        mIvCopy.setOnClickListener(this);
        mIvWhatsapp.setOnClickListener(this);
        mIvTelegram.setOnClickListener(this);
        mIvLine.setOnClickListener(this);
        mIvTwitter.setOnClickListener(this);
        mIvFb.setOnClickListener(this);
        mIvShare.setOnClickListener(this);


        mTvInviteLink.setText(user.getReferralCode());
        mTvInvitedCount.setText("" + 0);
        mShareText = "VexGift is a great wat to get free vouchers. Check it out here " + user.getReferralCode();
        mPresenter.requestUserReferral(user.getId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_referral_copy:
                copyToClipboard(mShareText);
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

        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof UserReferralResponse) {
                int referralsCount = ((UserReferralResponse) data).getReferrals().size();
                mTvInvitedCount.setText(referralsCount + "");
            }
        } else if (errorResponse != null) {

        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
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
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                shareIntent.setPackage("com.facebook.lite");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
            } catch (Exception er) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra(android.content.Intent.EXTRA_TEXT, text);
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
