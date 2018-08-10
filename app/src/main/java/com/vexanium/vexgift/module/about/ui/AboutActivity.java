package com.vexanium.vexgift.module.about.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_webview, toolbarTitle = R.string.about_us)
public class AboutActivity extends BaseActivity {

    WebView mWv;

    @Override
    protected void initView() {
        mWv = (WebView) findViewById(R.id.webview);

        final ProgressDialog pd = ProgressDialog.show(this, "", "Loading...",true);

        mWv.getSettings().setJavaScriptEnabled(true);
        mWv.getSettings().setSupportZoom(true);
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if(pd!=null && pd.isShowing())
                {
                    pd.dismiss();
                }
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
