package com.vexanium.vexgift.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.socks.library.KLog;
import com.vexanium.vexgift.BuildConfig;
import com.vexanium.vexgift.database.DaoMaster;
import com.vexanium.vexgift.database.DaoSession;
import com.vexanium.vexgift.database.DatabaseUpgradeHelper;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import io.fabric.sdk.android.Fabric;

/**
 * Created by hizkia on 12/03/18.
 */

public class App extends Application {
    public static Typeface bold;
    public static Typeface regular;
    public static Typeface medium;
    public static Typeface light;
    public static Typeface hnBoldCond;
    public static Typeface hnMed;
    private static Context mApplicationContext;
    private DaoSession mDaoSession;
    private Thread.UncaughtExceptionHandler mExceptionHandler;

    public static App getApplication() {
        return (App) mApplicationContext;
    }

    public static Context getContext() {
        return mApplicationContext;
    }

    public static void setTextViewStyle(ViewGroup root) {
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                setTextViewStyle((ViewGroup) child);

            } else if (child instanceof TextView) {
                final Object tagObj = child.getTag();
                if (tagObj != null) {
//                    KLog.v("HPtes Masuk "+tagObj);
                    if (tagObj.equals("bold"))
                        ((TextView) child).setTypeface(bold);
                    else if (tagObj.equals("medium"))
                        ((TextView) child).setTypeface(medium);
                    else if (tagObj.equals("light"))
                        ((TextView) child).setTypeface(light);
                    else if (tagObj.equals("hnBoldCond"))
                        ((TextView) child).setTypeface(hnBoldCond);
                    else if (tagObj.equals("hnMed"))
                        ((TextView) child).setTypeface(hnMed);
                    else
                        ((TextView) child).setTypeface(regular);
                }
            }
        }
    }

    public static void setTextViewStyle(Typeface typeface, TextView... textView) {
        for (TextView tv : textView) {
            tv.setTypeface(typeface);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        KLog.v("=========== BaseApplication onCreate ===========");
        mApplicationContext = this;

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }

        if (!BuildConfig.DEBUG) {
            mExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(final Thread thread, final Throwable ex) {
                    mExceptionHandler.uncaughtException(thread, ex);
                }
            });
        }

        KLog.init(BuildConfig.DEBUG);

        StaticGroup.initialize(this);
        setupCustomFont();
        setupDatabase();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    private void setupDatabase() {
        DatabaseUpgradeHelper helper = new DatabaseUpgradeHelper(this, ConstantGroup.DB_NAME);
        Database db = helper.getWritableDb();

        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();

        QueryBuilder.LOG_SQL = BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;
    }

    private void setupCustomFont() {
        bold = Typeface.createFromAsset(this.getAssets(), "fonts/SourceSansPro-Bold.ttf");
        regular = Typeface.createFromAsset(this.getAssets(), "fonts/SourceSansPro-Regular.ttf");
        medium = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Medium.ttf");
        light = Typeface.createFromAsset(this.getAssets(), "fonts/SourceSansPro-Light.ttf");
        hnBoldCond = Typeface.createFromAsset(this.getAssets(), "fonts/HelveticaNeue-BlackCond.ttf");
        hnMed = Typeface.createFromAsset(this.getAssets(), "fonts/HelveticaNeueMed.ttf");
    }
}
