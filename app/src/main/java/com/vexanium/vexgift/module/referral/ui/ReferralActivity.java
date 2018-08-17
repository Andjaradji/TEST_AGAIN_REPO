package com.vexanium.vexgift.module.referral.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_referral,toolbarTitle = R.string.referral_invite_others)
public class ReferralActivity extends BaseActivity {

    TextView mTvInvitedCount, mTvInviteLink;
    ImageView mIvCopy, mIvWhatsapp, mIvTelegram, mIvLine, mIvTwitter, mIvFb, mIvShare;

    @Override
    protected void initView() {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.iv_referral_copy:

                break;
            case R.id.referral_share_whatsapp_button:

                break;
            case R.id.referral_share_telegram_button:

                break;
            case R.id.referral_share_line_button:

                break;
            case R.id.referral_share_twitter_button:

                break;
            case R.id.referral_share_fb_button:

                break;
            case R.id.referral_share_sm_button:

                break;

        }
    }
}
