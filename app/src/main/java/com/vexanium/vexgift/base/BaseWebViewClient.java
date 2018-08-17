package com.vexanium.vexgift.base;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.socks.library.KLog;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BaseWebViewClient extends WebViewClient{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        KLog.v("BaseWebViewClient", "BaseWebViewClient shouldOverrideUrlLoading : " + view.getUrl());
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        KLog.v("BaseWebViewClient", "BaseWebViewClient onPageStarted : " + url);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        KLog.v("BaseWebViewClient", "BaseWebViewClient onReceivedSslError : [" + error.toString() + "]");
        handler.cancel();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        KLog.v("BaseWebViewClient", "BaseWebViewClient onReceivedError : [" + errorCode + "] " + description);
//        view.loadUrl("file:///android_asset/www/500.html?url="+ StaticGroup.urlEncodeCompat(failingUrl));
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        KLog.v("BaseWebViewClient", "BaseWebViewClient onPageFinished : " + url);
    }


    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    protected Map<String, String> corsHeader(String host) {
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Access-Control-Allow-Origin", host);
        headers.put("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD");
        headers.put("Access-Control-Max-Age", "2592000");
        headers.put("Access-Control-Allow-Credentials", "true");
        headers.put("Vary", "Origin, Access-Control-Request-Headers, Access-Control-Request-Method");
        headers.put("Expires", "Mon, 1 Dec 2025 00:00:00 GMT");

        return headers;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().getPath();
//        if (url.contains("jquery.min.js")) {
//            try {
//                return new WebResourceResponse("application/javascript", "UTF-8", view.getContext().getAssets().open("www/js/jquery.min.js"));
//            } catch (IOException e) {
//                KLog.v("WebViewClient", url+" error: "+e.toString());
//            }
//        }else if (url.contains("OpenSans.woff2")) {
//            try {
//                KLog.w("fonts/OpenSans.woff2");
//                return new WebResourceResponse("font/woff2", "UTF-8", 200, "OK", corsHeader(request.getRequestHeaders().get("Origin")), view.getContext().getAssets().open("fonts/OpenSans.woff2"));
//            } catch (IOException e) {
//                KLog.v("WebViewClient", url+" error: "+e.toString());
//            }
//        }else if (url.contains("favicon.")) {
//            try {
//                return new WebResourceResponse("image/png", "UTF-8", view.getContext().getAssets().open("www/favicon.png"));
//            } catch (IOException e) {
//                KLog.v("WebViewClient", url+" error: "+e.toString());
//            }
//        }
        KLog.v("WebViewClient", "shouldInterceptRequest : " + url + " " + request.getUrl().toString());
        return null;
    }
}
