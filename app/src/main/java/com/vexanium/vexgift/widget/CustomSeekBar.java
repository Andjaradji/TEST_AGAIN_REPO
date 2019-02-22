package com.vexanium.vexgift.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;

/**
 * Created by mac on 10/31/17.
 */

public class CustomSeekBar extends AppCompatSeekBar {
    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    Drawable mThumb;

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    public Drawable getSeekBarThumb() {
        return mThumb;
    }
}