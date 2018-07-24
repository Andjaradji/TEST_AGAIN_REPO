package com.vexanium.vexgift.widget.tag;


import com.vexanium.vexgift.R;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.MeasureUtil;

/**
 * Created by mac on 7/27/17.
 */

public class TagConstant {
    //use dp and sp, not px

    //----------------- separator TagView-----------------//
    public static final float DEFAULT_LINE_MARGIN = 5;
    public static final float DEFAULT_TAG_MARGIN = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 10;
    public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 5;
    public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 10;
    public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5;

    public static final float LAYOUT_WIDTH_OFFSET = 2;

    //----------------- separator Tag Item-----------------//
    public static final float DEFAULT_TAG_TEXT_SIZE = 14f;
    public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 14f;
    public static final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 1f;
    public static final float DEFAULT_TAG_RADIUS = MeasureUtil.dip2px(App.getContext(), 4);
    public static final int DEFAULT_TAG_LAYOUT_COLOR = ColorUtil.getColor(App.getContext(), R.color.material_white);
    public static final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = ColorUtil.getColor(App.getContext(), R.color.material_white);
    public static final int DEFAULT_TAG_TEXT_COLOR = ColorUtil.getColor(App.getContext(), R.color.material_black_text_color);
    public static final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = ColorUtil.getColor(App.getContext(), R.color.material_tag_border_color);
    public static final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = ColorUtil.getColor(App.getContext(), R.color.material_tag_border_color);
    public static final String DEFAULT_TAG_DELETE_ICON = "×";
    public static final boolean DEFAULT_TAG_IS_DELETABLE = false;


    private TagConstant() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }
}
