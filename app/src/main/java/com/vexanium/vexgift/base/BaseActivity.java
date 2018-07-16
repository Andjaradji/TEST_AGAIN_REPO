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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.socks.klog.BuildConfig;
import com.socks.library.KLog;

import java.util.Hashtable;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;

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

//        if(!(this instanceof FilterActivity)){
//            setTheme(R.style.BaseAppTheme_AppTheme);
//        }
        setContentView(mContentViewId);

//        copySystemUiVisibility();
//
//        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);

        handleStatusView();
        handleAppBarLayoutOffset();

        initFinishRxBus();

//        initToolbar();

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

        ViewUtil.fixInputMethodManagerLeak(this);
    }

//    private void initToolbar() {
//        final View statusView = findViewById(R.id.status_view);
//        if (statusView != null) {
//            statusView.getLayoutParams().height = MeasureUtil.getStatusBarHeight(this);
//        }
//
//        if (toolbar != null) {
//            toolbar.setContentInsetStartWithNavigation(0);
//            setSupportActionBar(toolbar);
//            if (getSupportActionBar() != null) {
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            }
//            if (mToolbarTitle != -1) {
//                setToolbarTitle(mToolbarTitle);
//            } else {
//                setToolbarTitle("");
//            }
//            if (mToolbarIndicator != -1) {
//                setToolbarIndicator(mToolbarIndicator);
//            } else {
//                setToolbarIndicator(R.drawable.ic_menu_back);
//            }
//
////
////            RelativeLayout customToolbarLayout = (RelativeLayout) findViewById(R.id.custom_toolbar_layout);
////            if (false) {
////                customToolbarLayout.setVisibility(View.VISIBLE);
////                findViewById(R.id.custom_toolbar_back_button).setOnClickListener(new View.OnClickListener() {
////                                                                                     @Override
////                                                                                     public void onClick(View v) {
////                                                                                         if(ClickUtil.isFastDoubleClick())return;
////                                                                                         finish();
////                                                                                     }
////                                                                                 });
////
////                        setToolbarIndicator(0);
////                toolbar.setContentInsetsAbsolute(0, 0);
////                toolbar.setPadding(0, 0, 0, 0);
////
////            } else if (customToolbarLayout != null) {
////                customToolbarLayout.setVisibility(View.GONE);
////            }
//        }
//
//    }

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(str);
        }
    }

    protected void setToolbarTitle(int strId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(strId);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        KLog.v("onOptionsItemSelected : " + mToolbarIndicator);
//        if (item.getItemId() == android.R.id.home
//                && (mToolbarIndicator == -1 || mToolbarIndicator == R.drawable.ic_menu_back || mToolbarIndicator == R.drawable.ic_menu_back_white)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                finishAfterTransition();
//            } else {
//                finish();
//            }
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (this instanceof MainActivity) {
//                if (!mBack2Flag && !isFinishing()) {
//                    Toast.makeText(this, getResources().getString(R.string.common_exit_msg), Toast.LENGTH_SHORT).show();
//                    mBack2Flag = true;
//                    Observable.timer(2000, TimeUnit.MILLISECONDS)
//                            .subscribe(new Action1<Object>() {
//                                @Override
//                                public void call(Object o) {
//                                    mBack2Flag = false;
//                                }
//                            }, new Action1<Throwable>() {
//                                @Override
//                                public void call(Throwable throwable) {
//                                }
//                            }, new Action0() {
//                                @Override
//                                public void call() {
//                                }
//                            });
//                    return false;
//                } else {
//                    RxBus.get().post(RxBus.KEY_APP_FINISH, true);
//                }
//                return true;
//            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void toast(String msg) {
        if (!isFinishing()) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    public void snackbar(String msg){
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

    }

    @Override
    public void hideProgress() {

    }

}
