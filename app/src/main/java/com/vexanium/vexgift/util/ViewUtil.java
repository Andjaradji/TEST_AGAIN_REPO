package com.vexanium.vexgift.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.util.glide.CircleCropTransformation;

import java.lang.reflect.Field;

import jp.wasabeef.glide.transformations.GrayscaleTransformation;


/**
 * Created by mac on 11/16/17.
 */

public class ViewUtil {
    public static void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    public static void showStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    public static View createStatusView(Activity activity, int color) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    public static void rotateScreen(Activity activity) {
        if (activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static void setFullScreen(Activity activity, boolean full) {
        if (full) {
            setFullScreen(activity);
        } else {
            quitFullScreen(activity);
        }
    }

    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void quitFullScreen(Activity activity) {
        final WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void dynamicSetTabLayoutMode(TabLayout tabLayout) {
        int tabTotalWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            final View view = tabLayout.getChildAt(i);
            view.measure(0, 0);
            tabTotalWidth += view.getMeasuredWidth();
        }
        if (tabTotalWidth <= MeasureUtil.getScreenSize(tabLayout.getContext()).x) {
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (String param : arr) {
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get
                            .getContext() == destContext) {
                        f.set(imm, null);
                    } else {
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void setVisiblityToAllView(int viewVisibility, View root, int... viewIds) {
        for (int viewId : viewIds) {
            if (root != null) {
                if (root.findViewById(viewId).getVisibility() != viewVisibility)
                    root.findViewById(viewId).setVisibility(viewVisibility);
            }
        }
    }

    public static void setVisiblityToAllView( int viewVisibility, View... views){
        for (View view : views){
            if(view!= null){
                if(view.getVisibility() != viewVisibility)
                    view.setVisibility(viewVisibility);
            }
        }
    }

    public static <T extends View> T findViewById(View root, int viewId) {
        View view = root.findViewById(viewId);
        return (T) view;
    }

    public static <T extends View> T findViewById(Activity activity, int viewId) {
        View view = activity.findViewById(viewId);
        return (T) view;
    }

    public static void setRoundImageUrl(View root, int viewId, String imgUrl, int placeholder) {
        ImageView view = findViewById(root, viewId);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .error(ContextCompat.getDrawable(App.getContext(), placeholder))
                        .format(DecodeFormat.PREFER_RGB_565))
                .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(20)))
                .load(imgUrl)
                .into(view);
    }

    public static void setCircleImageUrl(View root, int viewId, String imgUrl, @DrawableRes int errorImage) {
        ImageView view = findViewById(root, viewId);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .placeholder(errorImage)
                        .error(ContextCompat.getDrawable(App.getContext(), errorImage))
                        .transform(new CircleCropTransformation(App.getContext()))
                )
                .load(imgUrl)
                .into(view);
    }

    public static void setImageUrl(View root, int viewId, String imgUrl, @DrawableRes int errorImage) {
        ImageView view = findViewById(root, viewId);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .placeholder(errorImage)
                        .error(ContextCompat.getDrawable(App.getContext(), errorImage))
                        .centerCrop()
                )
                .load(imgUrl)
                .into(view);
    }

    public static void setImageUrl(Activity root, int viewId, Uri imgUrl) {
        ImageView view = findViewById(root, viewId);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .centerCrop()
                )
                .load(imgUrl)
                .into(view);
    }

    public static void setImageUrl(Activity activity, int viewId, String imgUrl, @DrawableRes int errorImage) {
        ImageView view = activity.findViewById(viewId);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .placeholder(errorImage)
                        .error(ContextCompat.getDrawable(App.getContext(), errorImage))
                        .centerCrop()
                )
                .load(imgUrl)
                .into(view);
    }

    public static void setBnwImageUrl(Activity activity, int viewId, String imgUrl, @DrawableRes int errorImage) {
        ImageView view = activity.findViewById(viewId);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .error(ContextCompat.getDrawable(App.getContext(), errorImage))
                        .format(DecodeFormat.PREFER_RGB_565)
                        .bitmapTransform(new GrayscaleTransformation()))
                .load(imgUrl)
                .into(view);
    }

    public static void setCircleImageUrl(Activity activity, int viewId, String imgUrl, @DrawableRes int errorImage) {
        ImageView view = findViewById(activity, viewId);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .placeholder(errorImage)
                        .error(ContextCompat.getDrawable(App.getContext(), errorImage))
                        .transform(new CircleCropTransformation(App.getContext()))
                )
                .load(imgUrl)
                .into(view);
    }

    public static void setText(View root, int viewId, String text) {
        TextView tv = findViewById(root, viewId);
        tv.setText(text);
    }

    public static void setText(View root, int viewId, CharSequence text) {
        TextView tv = findViewById(root, viewId);
        tv.setText(text);
    }

    public static void setText(Activity activity, int viewId, String text) {
        TextView tv = findViewById(activity, viewId);
        tv.setText(text);
    }

    public static void setText(Activity activity, int viewId, CharSequence text) {
        TextView tv = findViewById(activity, viewId);
        tv.setText(text);
    }

    public static void setOnClickListener(Activity activity, View.OnClickListener onClickListener, @IdRes int... idRes) {
        for (int id : idRes) {
            findViewById(activity, id).setOnClickListener(onClickListener);
        }
    }

    public static boolean validateEmpty(Activity activity, String errorMsg, @IdRes int... ids) {
        boolean isValid = true;
        for (int id : ids) {
            String s = ((EditText) findViewById(activity, id)).getText().toString();
            if (TextUtils.isEmpty(s)) {
                isValid = false;
                ((EditText) findViewById(activity, id)).setError(errorMsg);
            }
        }
        return isValid;
    }

    private String getColorCodeWithPercent(int progress) {
        // Progress : 1~100
        double percentage = 1.0 - (long) progress / 100.0;
        long alphaFixed = Math.round(percentage * 255);
        String alphaHex = Long.toHexString(alphaFixed);

        if (alphaHex.length() == 1) {
            alphaHex = "0" + alphaHex;
        }

        return alphaHex;
    }

}
