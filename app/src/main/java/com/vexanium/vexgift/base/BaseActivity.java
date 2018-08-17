package com.vexanium.vexgift.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.klog.BuildConfig;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.module.login.ui.GoogleAuthActivity;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.splash.ui.SplashActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Hashtable;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by mac on 11/16/17.
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView, View.OnClickListener {

    public static Hashtable<String, Stack<BaseActivity>> activityMap;

    protected T mPresenter;
    protected int mContentViewId;

    private int mToolbarTitle;
    private int mToolbarIndicator;

    public AppBarLayout appBarLayout;
    public Toolbar toolbar;

    private Observable<Boolean> mFinishObservable;

    private boolean mBack2Flag = false;

    private View decorView;
    private int uiOption;

    private boolean withLoadingAnim = false;
    public AVLoadingIndicatorView mLoadingView;
    public RelativeLayout mLoadingContainerView;

    public BaseActivity() {
        super();
        if (activityMap == null) {
            activityMap = new Hashtable<>();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
            mContentViewId = annotation.contentViewId();
            mToolbarTitle = annotation.toolbarTitle();
            mToolbarIndicator = annotation.toolbarIndicator();
            withLoadingAnim = annotation.withLoadingAnim();
        } else {
            throw new RuntimeException(
                    "Class must add annotations of ActivityFragmentInitParams.class");
        }

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        setContentView(mContentViewId);

        appBarLayout = findViewById(R.id.app_bar);
        toolbar = findViewById(R.id.toolbar);

        mLoadingView = findViewById(R.id.av_indicator_view);
        if (mLoadingView != null) {
            mLoadingView.setOnClickListener(this);
        }
        mLoadingContainerView = findViewById(R.id.av_indicator_container);
        if (mLoadingContainerView != null) {
            mLoadingContainerView.setOnClickListener(this);
        }

        handleStatusView();
        handleAppBarLayoutOffset();

        initFinishRxBus();

        initToolbar();

        initView();

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        App.setTextViewStyle(viewGroup);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
//            getDecorView().setSystemUiVisibility(uiOption);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) mPresenter.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!(this instanceof SplashActivity || this instanceof GoogleAuthActivity)) {
            User.setLastActiveTime();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        KLog.v("onUserLeaveHint");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        if (mFinishObservable != null) {
            RxBus.get().unregister(RxBus.KEY_APP_FINISH, mFinishObservable);
        }
        if (!(this instanceof SplashActivity || this instanceof GoogleAuthActivity)) {
            User.setLastActiveTime();
        }
        ViewUtil.fixInputMethodManagerLeak(this);
    }

    private void initToolbar() {

        if (toolbar != null && mToolbarTitle != -1) {
            if (mToolbarTitle != -1) {
                setToolbarTitle(mToolbarTitle);
            } else {
                setToolbarTitle("");
            }

            toolbar.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    finish();
                }
            });
        }

    }

    protected void setToolbarIndicator(int resId) {
        if (getSupportActionBar() != null) {
            if (resId > 0) {
                getSupportActionBar().setHomeAsUpIndicator(resId);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    protected void setToolbarTitle(String str) {
        if (toolbar.findViewById(R.id.tv_toolbar_title) != null) {
            ((TextView) toolbar.findViewById(R.id.tv_toolbar_title)).setText(str);
        }
    }

    protected void setToolbarTitle(int strId) {
        if (toolbar.findViewById(R.id.tv_toolbar_title) != null) {
            ((TextView) toolbar.findViewById(R.id.tv_toolbar_title)).setText(getString(strId));
        }
    }

    protected ActionBar getToolbar() {
        return getSupportActionBar();
    }

    protected View getDecorView() {
        return getWindow().getDecorView();
    }

    protected abstract void initView();

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void handleStatusView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            View statusView = findViewById(R.id.status_view);
//            if (statusView != null) {
//                statusView.setVisibility(View.GONE);
//            }
        }
    }

    private void handleAppBarLayoutOffset() {
        if (appBarLayout != null && toolbar != null) {
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    RxBus.get().post(RxBus.KEY_ENABLE_REFRESH_LAYOUT_OR_SCROLL_RECYCLER_VIEW, verticalOffset == 0);
                }
            });
        }
    }

    private void initFinishRxBus() {
        mFinishObservable = RxBus.get().register(RxBus.KEY_APP_FINISH, Boolean.class);
        mFinishObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isBackButton) {
                try {
                    if (isBackButton) {
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (this instanceof MainActivity) {
                if (!mBack2Flag && !isFinishing()) {
                    if (((MainActivity) this).isEligibleToExit()) {
                        Toast.makeText(this, getResources().getString(R.string.common_exit_msg), Toast.LENGTH_SHORT).show();
                        mBack2Flag = true;
                        Observable.timer(2000, TimeUnit.MILLISECONDS)
                                .subscribe(new Action1<Object>() {
                                    @Override
                                    public void call(Object o) {
                                        mBack2Flag = false;
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
                    return false;
                } else {
                    RxBus.get().post(RxBus.KEY_APP_FINISH, true);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) return;
    }

    @Override
    public void toast(String msg) {
        if (!isFinishing()) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    public void snackbar(String msg) {
        if (!isFinishing()) {
            Snackbar.make(this.getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void showProgress() {
        if (withLoadingAnim) {
            try {
                mLoadingContainerView.setVisibility(View.VISIBLE);
                if (mLoadingView != null && !isFinishing()) {
                    mLoadingView.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void hideProgress() {
        if (withLoadingAnim) {
            try {
                mLoadingContainerView.setVisibility(View.GONE);
                if (mLoadingView != null && !isFinishing()) {
                    mLoadingView.hide();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
