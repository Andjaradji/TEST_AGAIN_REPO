package com.vexanium.vexgift.module.login.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.AccessToken;
import com.vexanium.vexgift.BuildConfig;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.SettingResponse;
import com.vexanium.vexgift.database.TablePrefDaoUtil;
import com.vexanium.vexgift.module.login.presenter.ILoginPresenter;
import com.vexanium.vexgift.module.login.presenter.ILoginPresenterImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.walkthrough.ui.WalkthroughActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.ConstantGroup.SUPPORT_EMAIL;

/**
 * Created by Amang on 16/07/2018.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_splash, withLoadingAnim = true)
public class SplashActivity extends BaseActivity<ILoginPresenter> implements ILoginView {

    protected static int SPLASH_TIME = 1000;
    protected static int currentCountdown = 8;
    private Class<? extends Activity> destinationActivity;
    Uri uri;


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
        mPresenter = new ILoginPresenterImpl(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        destinationActivity = getDestinationActivity(this);

        if (destinationActivity != null) {
            if (checkLoginInfo()) {
                User user = User.getCurrentUser(this);
                mPresenter.requestSetting(user.getId());
            } else {
                mPresenter.requestAppStatus();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null && Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            uri = getIntent().getData();
            getIntent().setData(null);

        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof SettingResponse) {
                SettingResponse settingResponse = (SettingResponse) data;
                if (checkLoginInfo()) {
                    TablePrefDaoUtil.getInstance().saveSettingToDb(JsonUtil.toString(settingResponse));
                }
//                findViewById(R.id.splash_container).setVisibility(View.GONE);

                checkApp(settingResponse.getMinimumVersion(), settingResponse.getSettingValByKey("is_maintenance"));
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    private void checkApp(long minimumVersion, long isMaintenance) {
        long currentVersion = BuildConfig.VERSION_CODE;
        boolean isNeedUpdate = currentVersion < minimumVersion;

        // TODO: 23/08/18 remove condition 
        if (isMaintenance == 1 && false) {
            findViewById(R.id.ll_maintenance).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_need_update).setVisibility(View.GONE);

            if(User.getCurrentUser(this)!= null){
                TpUtil tpUtil = new TpUtil(App.getContext());
                tpUtil.put(TpUtil.KEY_CURRENT_LOGGED_IN_USER,"");
                tpUtil.remove(TpUtil.KEY_CURRENT_LOGGED_IN_USER);
            }

            findViewById(R.id.tv_contact).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    String subject = "[URGENT]";
                    String message = "Hi Vexgift Support!\nI've urgent problem with...";
                    StaticGroup.shareWithEmail(SplashActivity.this, SUPPORT_EMAIL, subject, message);
                }
            });
        } else if (isNeedUpdate) {
            findViewById(R.id.ll_need_update).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_maintenance).setVisibility(View.GONE);

            String newVersion = String.format(getString(R.string.appversion_need_update_version_new), String.valueOf(minimumVersion));
            String oldVersion = String.format(getString(R.string.appversion_need_update_version_current), String.valueOf(currentVersion));

            findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    StaticGroup.openVexgiftGooglePlay(SplashActivity.this);
                }
            });

            ViewUtil.setText(this, R.id.tv_version, oldVersion);
            ViewUtil.setText(this, R.id.tv_version_new, newVersion);

            startCoundownTimer();
        } else {

            finish();

            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, destinationActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if(uri!=null){
                intent.putExtra("url",uri.toString());
            }
            startActivity(intent);
        }
    }

    private void startCoundownTimer() {
        currentCountdown = 8;
        Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        String msg = String.format(getString(R.string.appversion_need_update_goto_gp), currentCountdown);
                        ViewUtil.setText(SplashActivity.this, R.id.tv_goto, msg);

                        if (currentCountdown > 0) {
                            currentCountdown--;
                        } else {
                            StaticGroup.openVexgiftGooglePlay(SplashActivity.this);
                            finish();
                            throw new NullPointerException();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                    }
                });
    }

    private boolean checkLoginInfo() {
        User user = User.getCurrentUser(this.getApplicationContext());

        if (user != null) {
            if (!TextUtils.isEmpty(user.getFacebookAccessToken())) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    Date now = new Date();
                    if (accessToken.getExpires() != null && accessToken.getExpires().getTime() < now.getTime()) {
                        AccessToken.refreshCurrentAccessTokenAsync();
                    }
                }
            }
            return true;
        } else {
            return false;
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
                    if(uri!=null){
                        intent.putExtra("url",uri.toString());
                    }
                    startActivity(intent);
                }
            }
        };

        splashTread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void initView() {
        if (getIntent() != null && Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            uri = getIntent().getData();
            getIntent().setData(null);

        }

    }
}
