package com.vexanium.vexgift.widget.tag;

import android.graphics.drawable.Drawable;

/**
 * Created by mac on 7/27/17.
 */

public class Tag {
    public int id;
    public String text;
    public int tagTextColor;
    public float tagTextSize;
    public int layoutColor;
    public int layoutColorPress;
    public float radius;
    public float layoutBorderSize;
    public int layoutBorderColor;
    public Drawable background;

    public boolean isSelected;

    public Tag(int id, String text) {
        init(id, text, TagConstant.DEFAULT_TAG_TEXT_COLOR, TagConstant.DEFAULT_TAG_TEXT_SIZE, TagConstant.DEFAULT_TAG_LAYOUT_COLOR, TagConstant.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                TagConstant.DEFAULT_TAG_RADIUS, TagConstant.DEFAULT_TAG_LAYOUT_BORDER_SIZE, TagConstant.DEFAULT_TAG_LAYOUT_BORDER_COLOR, false);
    }

    public Tag(String text) {
        init(0, text, TagConstant.DEFAULT_TAG_TEXT_COLOR, TagConstant.DEFAULT_TAG_TEXT_SIZE, TagConstant.DEFAULT_TAG_LAYOUT_COLOR, TagConstant.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                TagConstant.DEFAULT_TAG_RADIUS, TagConstant.DEFAULT_TAG_LAYOUT_BORDER_SIZE, TagConstant.DEFAULT_TAG_LAYOUT_BORDER_COLOR, false);
    }

    /*public Tag(String text, boolean isSelected) {
        init(
                0,
                text,
                TagConstant.DEFAULT_TAG_TEXT_COLOR,
                TagConstant.DEFAULT_TAG_TEXT_SIZE,
                TagConstant.DEFAULT_TAG_LAYOUT_COLOR,
                TagConstant.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                TagConstant.DEFAULT_TAG_RADIUS,
                isSelected ? 2f:TagConstant.DEFAULT_TAG_LAYOUT_BORDER_SIZE,
                isSelected ? ColorUtil.getColor(App.getContext(), R.color.point_color) : TagConstant.DEFAULT_TAG_LAYOUT_BORDER_COLOR,
                isSelected
        );
    }*/

    private void init(int id, String text, int tagTextColor, float tagTextSize,
                      int layoutColor, int layoutColorPress, float radius, float layoutBorderSize, int layoutBorderColor, boolean isSelected) {
        this.id = id;
        this.text = text;
        this.tagTextColor = tagTextColor;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layoutColor;
        this.layoutColorPress = layoutColorPress;
        this.radius = radius;
        this.layoutBorderSize = layoutBorderSize;
        this.layoutBorderColor = layoutBorderColor;
        this.isSelected = isSelected;
    }
}

