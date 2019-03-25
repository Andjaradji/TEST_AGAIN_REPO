package com.vexanium.vexgift.module.more.ui;

import android.app.DownloadManager;
import android.content.Context;
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

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.util.LocaleUtil;

@ActivityFragmentInject(contentViewId = R.layout.activity_webview, toolbarTitle = R.string.about_us)
public class AboutActivity extends BaseActivity {

    WebView mWv;
    RelativeLayout mLoadingContainer;

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

        mWv.getSettings().setSupportZoom(true);
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
        String url = ConstantGroup.WEB_LINK;
        if (LocaleUtil.getLanguage(this).equalsIgnoreCase("zh")) {
            url = ConstantGroup.CHINA_WEB_LINK;
            KLog.v("AboutActivity", "initView: HMtes china");
        }
        mWv.loadUrl(url);
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
