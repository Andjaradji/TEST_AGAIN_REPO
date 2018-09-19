package com.vexanium.vexgift.module.voucher.ui;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseWebChromeClient;
import com.vexanium.vexgift.base.BaseWebView;
import com.vexanium.vexgift.base.BaseWebViewClient;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.util.JsonUtil;

import static com.vexanium.vexgift.app.App.getContext;

@ActivityFragmentInject(contentViewId = R.layout.activity_voucher_web_view, toolbarTitle = R.string.app_name, withLoadingAnim = true)
public class VoucherWebViewActivity extends BaseActivity {

    private BaseWebView mWebView;
    private BaseWebChromeClient mWebChromeClient;

    private String url;
    private Voucher voucher;
    private boolean isKFC3rd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        if (getIntent().hasExtra("url")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("url"))) {
                url = getIntent().getStringExtra("url");
            }
        }

        if (getIntent().hasExtra("voucher")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("voucher"))) {
                voucher = (Voucher) JsonUtil.toObject(getIntent().getStringExtra("voucher"), Voucher.class);
                if (voucher.getId() == 5 || voucher.getId() == 6) {
                    isKFC3rd = true;
                }
            }
        }

        try {
            mWebView = findViewById(R.id.webview);
            ((TextView) findViewById(R.id.tv_toolbar_title)).setText(voucher.getVendor().getName().toUpperCase());
        } catch (Exception e) {
        }

        final VoucherWebViewClient mWebClient = new VoucherWebViewClient();
        mWebView.setWebViewClient(mWebClient);

        mWebChromeClient = new BaseWebChromeClient(this);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setJavaScriptEnabled(true);

        showProgress();
        KLog.v("VoucherWebViewActivity", "initView: HPtes url " + url);
        mWebView.loadUrl(url);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }

        StaticGroup.syncCookies();

    }

    private void handleWebPageUrl(String url) {
        boolean isAlreadyHandled = StaticGroup.handleUrl(getContext(), url);
        if (!isAlreadyHandled) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                mWebView.requestFocus();
                mWebView.loadUrl(url);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();

        mWebChromeClient.dismissDialog();
        mWebView.onPause();
        hideProgress();
    }

    @Override
    public void onResume() {
        KLog.v("onResume : " + mWebView.getUrl());
        super.onResume();
        CookieSyncManager.getInstance().startSync();

        mWebView.onResume();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }

        super.onDestroy();
    }

    private class VoucherWebViewClient extends BaseWebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            KLog.v("IntraWeb", "IntraWeb shouldOverrideUrlLoading : " + url);

            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                if (uri.getScheme().contains("http")) {
                    showProgress();
                }
                KLog.v("VoucherWebViewClient", "shouldOverrideUrlLoading: " + url);
                handleWebPageUrl(url);
            }

            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            KLog.v("IntraWeb", "IntraWeb shouldOverrideUrlLoading : " + uri);

            if (uri != null) {
                String url = uri.toString();
                if (uri.getScheme().contains("http")) {
                    showProgress();
                }
                handleWebPageUrl(url);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            KLog.v("VoucherWebView", "VoucherWebView onPageFinished : " + url);
            hideProgress();
            if (mWebView != null && mWebView.getSettings() != null) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            }

            // KFC giftN patch
            if (isKFC3rd) {
                mWebView.loadUrl("javascript:(function() { " +
                        "var rows = document.getElementsByTagName(\"table\")[0].rows;\n" +
                        "rows[8].parentNode.removeChild(rows[8]);\n" +
                        "rows[7].parentNode.removeChild(rows[7]);\n" +
                        "rows[6].parentNode.removeChild(rows[6]);\n" +
                        "var logo = document.getElementById(\"g_gmktLogo\");\n" +
                        "logo.parentNode.removeChild(logo);" +
                        "})()");
                mWebView.getSettings().setJavaScriptEnabled(true);
            }

            StaticGroup.syncCookies();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            KLog.v("BaseWebViewClient", "BaseWebViewClient onReceivedError : [" + errorCode + "] " + description);

//            scrollView.setFillViewport(true);
//            view.loadUrl("file:///android_asset/www/500_s.html?url="+StaticGroup.urlEncodeCompat(failingUrl));
        }
    }
}
