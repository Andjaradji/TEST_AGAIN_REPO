package com.vexanium.vexgift.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.vexanium.vexgift.BuildConfig;
import com.vexanium.vexgift.app.StaticGroup;

public class BaseWebView extends WebView{
    public BaseWebView(Context context) {
        super(context);
        setInitResources();
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInitResources();
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInitResources();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void setInitResources() {
        WebSettings webSettings = getSettings();

        String userAgent = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgent + " VexGift/" + StaticGroup.VERSION_CODE + " " + BuildConfig.APPLICATION_ID);

        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webSettings.setDatabasePath("/data/data/" + getContext().getPackageName() + "/databases/");
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        setInitialScale(1);

        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setSupportMultipleWindows(true);

        webSettings.setGeolocationEnabled(true);

        if (Build.VERSION.SDK_INT < 18) {
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        setVerticalScrollbarOverlay(true);
    }
}
