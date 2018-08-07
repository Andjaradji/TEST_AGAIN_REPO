package com.vexanium.vexgift.module.term.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_webview, toolbarTitle = R.string.term_and_condition)
public class TermActivity extends BaseActivity {

    WebView mWvPrivacy;

    @Override
    protected void initView() {
        mWvPrivacy = (WebView) findViewById(R.id.wv_privacy);

        final ProgressDialog pd = ProgressDialog.show(this, "", "Loading...",true);

        mWvPrivacy.getSettings().setSupportZoom(true);
        mWvPrivacy.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if(pd!=null && pd.isShowing())
                {
                    pd.dismiss();
                }
            }
        });
        mWvPrivacy.loadUrl("http://www.vexgift.com/term.html");
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
