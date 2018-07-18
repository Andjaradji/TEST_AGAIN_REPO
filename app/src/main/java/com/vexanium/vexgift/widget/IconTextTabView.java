package com.vexanium.vexgift.widget;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.app.App;

/**
 * Created by mac on 11/17/17.
 */

public class IconTextTabView extends LinearLayout {
    private int idx;
    @DrawableRes
    private int selectedIcon;
    @DrawableRes
    private int icon;

    private String text;

    private View view;
    private ImageView ivIcon;
    private TextView tvTitle;
    private Context context;


    public IconTextTabView(Context context) {
        super(context);
        init(context);
    }

    public IconTextTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconTextTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

//    public TabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context);
//    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
        ivIcon.setImageResource(icon);
    }

    public void setText(String text){
        this.text = text;
        tvTitle.setText(text);
    }

    private void init(Context context) {
        this.context = context;
        view = View.inflate(context, R.layout.item_icon_text_tab_view, this);
        ivIcon = (ImageView) view.findViewById(R.id.iv_tab_icon);
        tvTitle = (TextView) view.findViewById(R.id.tv_tab_title);

        App.setTextViewStyle((ViewGroup)view);

    }

    public void updateView(boolean isSelected) {
        ivIcon.setAlpha(isSelected ? 1f : 0.6f);
        tvTitle.setTextColor(isSelected? context.getResources().getColor(R.color.material_black) : context.getResources().getColor(R.color.material_grey_a6));
    }

}
