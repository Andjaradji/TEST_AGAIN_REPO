package com.vexanium.vexgift.module.more.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseWebChromeClient;
import com.vexanium.vexgift.base.BaseWebView;

@ActivityFragmentInject(contentViewId = R.layout.activity_webview, toolbarTitle = R.string.app_title)
public class WebViewActivity extends BaseActivity {

    BaseWebView mWv;
    RelativeLayout mLoadingContainer;
    String url;
    private BaseWebChromeClient mWebChromeClient;

    @Override
    protected void initView() {
        mWv = findViewById(R.id.webview);
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
        mWv.setVisibility(View.GONE);

        mWebChromeClient = new BaseWebChromeClient(this);

        mWv.getSettings().setSupportZoom(true);
        mWv.setWebChromeClient(mWebChromeClient);
        mWv.getSettings().setJavaScriptEnabled(true);
        mWv.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWv.setVisibility(View.VISIBLE);
                mWv.startAnimation(fadeIn);
            }
        });
        mWv.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                KLog.v("AboutActivity","onDownloadStart: HPMtes "+url);
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                downloadManager.enqueue(request);
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWv.getSettings()
                    .setMixedContentMode(
                            WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    );
        }
        if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");
            mWv.loadUrl(url);

        }

        if (getIntent().hasExtra("title")) {
            String title = getIntent().getStringExtra("title");
            if (!TextUtils.isEmpty(title)) {
                TextView tvTitle = findViewById(R.id.tv_toolbar_title);
                tvTitle.setText(title);
            }
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
