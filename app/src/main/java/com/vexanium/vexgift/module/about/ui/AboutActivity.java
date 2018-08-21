package com.vexanium.vexgift.module.about.ui;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_webview, toolbarTitle = R.string.about_us)
public class AboutActivity extends BaseActivity {

    WebView mWv;
    RelativeLayout mLoadingContainer;

    @Override
    protected void initView() {
        mWv = findViewById(R.id.webview);
        mLoadingContainer = findViewById(R.id.av_indicator_container);

        final Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in_anim);
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
        mWv.setVisibility(View.GONE);

        mWv.getSettings().setSupportZoom(true);
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWv.setVisibility(View.VISIBLE);
                mWv.startAnimation(fadeIn);
            }
        });
        mWv.loadUrl("http://www.vexgift.com");
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
