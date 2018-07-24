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

    public static Typeface bold;
    public static Typeface regular;
    public static Typeface medium;
    public static Typeface latoBold;
    public static Typeface latoRegular;
    public static Typeface light;
    public static Typeface sspBold;
    public static Typeface hnBoldCond;
    public static Typeface hnMed;

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
        hnBoldCond = Typeface.createFromAsset(this.getAssets(), "fonts/HelveticaNeue-BlackCond.ttf");
        hnMed = Typeface.createFromAsset(this.getAssets(), "fonts/HelveticaNeueMed.ttf");
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
                    else if (tagObj.equals("hnBoldCond"))
                        ((TextView) child).setTypeface(hnBoldCond);
                    else if (tagObj.equals("hnMed"))
                        ((TextView) child).setTypeface(hnMed);
                    else
                        ((TextView) child).setTypeface(latoRegular);
                }
            }
        }
    }

    public static void setTextViewStyle(Typeface typeface, TextView... textView) {
        for (TextView tv : textView) {
            tv.setTypeface(typeface);
        }
    }
}
