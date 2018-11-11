package com.vexanium.vexgift.module.more.ui;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_webview, toolbarTitle = R.string.app_title)
public class WebViewActivity extends BaseActivity {

    WebView mWvTerm;
    RelativeLayout mLoadingContainer;

    String url;

    @Override
    protected void initView() {
        mWvTerm = findViewById(R.id.webview);
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
        mWvTerm.setVisibility(View.GONE);

        mWvTerm.getSettings().setSupportZoom(true);
        mWvTerm.getSettings().setJavaScriptEnabled(true);
        mWvTerm.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mWvTerm.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWvTerm.setVisibility(View.VISIBLE);
                mWvTerm.startAnimation(fadeIn);
            }
        });
        if(getIntent().hasExtra("url")){
            url = getIntent().getStringExtra("url");
            mWvTerm.loadUrl(url);

        }
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
