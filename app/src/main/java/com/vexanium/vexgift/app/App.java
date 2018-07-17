package com.vexanium.vexgift.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.socks.library.KLog;

/**
 * Created by hizkia on 12/03/18.
 */

public class App extends Application {
    private static Context mApplicationContext;

    public static App getApplication() {
        return (App) mApplicationContext;
    }

    public static Context getContext() {
        return mApplicationContext;
    }

    private static Typeface bold;
    private static Typeface regular;
    private static Typeface medium;
    private static Typeface latoBold;
    private static Typeface latoRegular;
    private static Typeface light;
    private static Typeface sspBold;

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.v("=========== BaseApplication onCreate ===========");
        mApplicationContext = this;

        setupCustomFont();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    private void setupCustomFont() {
        bold = Typeface.createFromAsset(this.getAssets(), "fonts/SourceSansPro-Bold.ttf");
        regular = Typeface.createFromAsset(this.getAssets(), "fonts/SourceSansPro-Regular.ttf");
        medium = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Medium.ttf");
        latoBold = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Bold.ttf");
        latoRegular = Typeface.createFromAsset(this.getAssets(), "fonts/Lato-Regular.ttf");
        light = Typeface.createFromAsset(this.getAssets(), "fonts/SourceSansPro-Light.ttf");
        sspBold = Typeface.createFromAsset(this.getAssets(), "fonts/SourceSansPro-Bold.ttf");
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
                    else if (tagObj.equals("latoBold"))
                        ((TextView) child).setTypeface(latoBold);
                    else if (tagObj.equals("latoRegular"))
                        ((TextView) child).setTypeface(latoRegular);
                    else if (tagObj.equals("light"))
                        ((TextView) child).setTypeface(light);
                    else if (tagObj.equals("sspBold"))
                        ((TextView) child).setTypeface(sspBold);
                    else
                        ((TextView) child).setTypeface(latoRegular);
                }
            }
        }
    }
}
