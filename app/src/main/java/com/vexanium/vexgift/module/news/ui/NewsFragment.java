package com.vexanium.vexgift.module.news.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.base.BaseWebChromeClient;
import com.vexanium.vexgift.base.BaseWebView;

@ActivityFragmentInject(contentViewId = R.layout.fragment_news)
public class NewsFragment extends BaseFragment {
    BaseWebView mWebView;
    RelativeLayout mLoadingContainer;

    private BaseWebChromeClient mWebChromeClient;

    private String url;
    private String javascript;
    private SwipeRefreshLayout mRefreshLayout;


    public static NewsFragment newInstance(String url, String javascript) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("js", javascript);
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Override
    protected void initView(View fragmentRootView) {
        mWebView = fragmentRootView.findViewById(R.id.webview);
        mLoadingContainer = fragmentRootView.findViewById(R.id.av_indicator_container);

        mRefreshLayout = fragmentRootView.findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(url);
            }
        });

        final Animation fadeIn = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_in_anim);
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

        url = getArguments().getString("url");
        javascript = getArguments().getString("js");

        mLoadingContainer.setVisibility(View.VISIBLE);

        mWebChromeClient = new BaseWebChromeClient(this.getContext());
        mWebView.setVisibility(View.GONE);
        mWebView.setWebChromeClient(mWebChromeClient);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }

        mWebView.getSettings().setSupportZoom(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mRefreshLayout.setRefreshing(false);
                mLoadingContainer.setVisibility(View.GONE);

                mWebView.setVisibility(View.VISIBLE);
                mWebView.startAnimation(fadeIn);

                if (mWebView != null && mWebView.getSettings() != null) {
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                }

                if (!TextUtils.isEmpty(javascript)) {
                    mWebView.loadUrl(javascript);
                }
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.v("NewsFragment", "onDestroy: ");
    }
}
