package com.vexanium.vexgift.module.splash.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.login.ui.LoginActivity;
import com.vexanium.vexgift.module.walkthrough.ui.WalkthroughActivity;
import com.vexanium.vexgift.util.TpUtil;

/**
 * Created by Amang on 16/07/2018.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    protected static int SPLASH_TIME = 2000;

    public static Class<? extends Activity> getDestinationActivity(Context context) {
        Class<? extends Activity> destination;
        TpUtil tpUtil = new TpUtil(context);
        boolean isFirstTime = tpUtil.getBoolean(TpUtil.KEY_WALKTHROUGH, true);

        if (isFirstTime) {
            destination = WalkthroughActivity.class;
        } else {
            destination = LoginActivity.class;
        }

        return destination;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Class<? extends Activity> destinationActivity = getDestinationActivity(this);

        if (destinationActivity != null) {
            startThread(destinationActivity);
        }

    }

    private void startThread(final Class<? extends Activity> DestinationActivity) {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(SPLASH_TIME);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();

                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, DestinationActivity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        splashTread.start();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void initView() {

    }
}
