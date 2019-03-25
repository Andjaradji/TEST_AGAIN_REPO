package com.vexanium.vexgift.module.more.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_webview, toolbarTitle = R.string.privacy_policy)
public class PrivacyActivity extends BaseActivity {

    WebView mWvPrivacy;
    RelativeLayout mLoadingContainer;

    @Override
    protected void initView() {
        mWvPrivacy = findViewById(R.id.webview);
        mLoadingContainer = findViewById(R.id.av_indicator_container);

        final Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_anim);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadingContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mLoadingContainer.setVisibility(View.VISIBLE);
        mWvPrivacy.setVisibility(View.GONE);

        mWvPrivacy.getSettings().setSupportZoom(true);
        mWvPrivacy.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWvPrivacy.setVisibility(View.VISIBLE);
                mWvPrivacy.startAnimation(fadeIn);
            }
        });
        mWvPrivacy.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        mWvPrivacy.loadUrl("http://www.vexgift.com/privacy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

}
