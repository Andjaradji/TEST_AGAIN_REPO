package com.vexanium.vexgift.widget.guideview;


import android.app.Activity;
import android.graphics.Point;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.widget.guideview.bubbletooltip.BubbleToolTip;
import com.vexanium.vexgift.widget.guideview.handguide.HandGuide;
import com.vexanium.vexgift.widget.guideview.nextbutton.NextButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mac on 5/30/17.
 */

public class VexGuideView {
    private ArrayList<HoleView> holeViews;
    private Activity activity;
    private MotionType motionType;
    private FrameLayoutWithHole frameLayoutWithHole;
    private ArrayList<BubbleToolTip> bubbleToolTips;
    private ArrayList<HandGuide> handGuides;
    private NextButton nextButton;
    private Overlay overlay;

    public VexGuideView init(Activity activity) {
        this.activity = activity;
        holeViews = new ArrayList<>();
        return this;
    }

    public VexGuideView holeView(HoleView... holes) {
        holeViews = new ArrayList<>();
        holeViews.addAll(Arrays.asList(holes));
        return this;
    }

    public VexGuideView bubbleTooltip(BubbleToolTip... tools) {
        bubbleToolTips = new ArrayList<>();
        bubbleToolTips.addAll(Arrays.asList(tools));
        return this;
    }

    public VexGuideView handGuide(HandGuide... hands) {
        handGuides = new ArrayList<>();
        handGuides.addAll(Arrays.asList(hands));
        return this;
    }

    public VexGuideView nextButton(NextButton nextButton) {
        this.nextButton = nextButton;
        return this;
    }

    public VexGuideView show() {
        setupView(true);
        return this;
    }

    public VexGuideView motionType(MotionType motionType) {
        this.motionType = motionType;
        return this;
    }

    public VexGuideView overlay(Overlay overlay) {
        this.overlay = overlay;
        return this;
    }

    public void cleanUpAll() {
        cleanUpAll(false);
    }

    public void cleanUpAll(boolean forceEndUp) {
        KLog.v("cleanUpAll GuideView");
        if (bubbleToolTips != null) {
            for (BubbleToolTip bubbleToolTip : bubbleToolTips) {
                if (bubbleToolTip != null && bubbleToolTip.view != null) {
                    bubbleToolTip.view.clearAnimation();
                    if (activity.getWindow().getDecorView() != null) {
                        ((ViewGroup) activity.getWindow().getDecorView()).removeView(bubbleToolTip.view);
                    }
                }

            }
            bubbleToolTips = null;
        }
        if (handGuides != null) {
            for (HandGuide handGuide : handGuides) {
                if (handGuide != null && handGuide.view != null) {
                    handGuide.view.clearAnimation();
                    if (activity.getWindow().getDecorView() != null) {
                        ((ViewGroup) activity.getWindow().getDecorView()).removeView(handGuide.view);
                    }
                }

            }
        }
        if (nextButton != null && nextButton.view != null && nextButton.imNext != null) {
            nextButton.view.clearAnimation();
            nextButton.imNext.clearAnimation();
            if (activity.getWindow().getDecorView() != null) {
                ((ViewGroup) activity.getWindow().getDecorView()).removeView(nextButton.view);
            }
        }
        if (frameLayoutWithHole != null) {
            KLog.v("cleanUpAll frameLayoutWithHole");
            if (forceEndUp) {
                frameLayoutWithHole.endup();
            } else {
                frameLayoutWithHole.cleanUp();
            }
        }
    }

    private boolean isAllHoleViewAttachedToWindows() {
        for (HoleView holeView : holeViews) {
            if (holeView != null && holeView.view != null && !ViewCompat.isAttachedToWindow(holeView.view)) {
                return false;
            }
        }
        return true;
    }

    private void addGlobalLayoutListenerToComp(final View view, final boolean isShowAll) {
        if (view == null) return;

        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                startView(isShowAll);
            }
        });
    }


    private void setupView(boolean isShowAll) {
        if (isAllHoleViewAttachedToWindows()) {
            startView(isShowAll);
        } else {
            for (final HoleView holeView : holeViews) {
                if (holeView != null && holeView.view != null) {
                    addGlobalLayoutListenerToComp(holeView.view, isShowAll);
                }
            }
        }
    }

    @UiThread
    public VexGuideView refreshView() {
        if (frameLayoutWithHole != null) {
            frameLayoutWithHole.setHolesViews(holeViews);
            frameLayoutWithHole.invalidate();
        }
        return this;
    }

    public VexGuideView addNewHole(HoleView holeView) {
        if (holeView == null) {
            holeViews = new ArrayList<>();
        }
        holeViews.add(holeView);

        if (frameLayoutWithHole != null) {
            frameLayoutWithHole.addNewHoleWithAnim(holeView);
        }
        return this;
    }

    public void highlightAnimOn(HoleView holeView) {
        if (frameLayoutWithHole != null && holeView != null) {
            frameLayoutWithHole.animateOn(holeView);
        }
    }

    private void startView(boolean isShowAll) {
        frameLayoutWithHole = new FrameLayoutWithHole(activity, holeViews, motionType, overlay);
        handleDisableClicking(frameLayoutWithHole);
        setupFrameLayout();
        if (isShowAll) {
            setupBubbleToolTip();
            setupNextButton();
            setupHandGuide();
        }
    }

    private void handleDisableClicking(FrameLayoutWithHole frameLayoutWithHole) {
        if (frameLayoutWithHole == null) return;

        if (overlay != null && overlay.mOnClickListener != null) {
            frameLayoutWithHole.setClickable(true);
            frameLayoutWithHole.setOnClickListener(overlay.mOnClickListener);
        } else if (overlay != null && overlay.mDisableClick) {
            frameLayoutWithHole.setHolesViews(holeViews);
            frameLayoutWithHole.setSoundEffectsEnabled(false);
            frameLayoutWithHole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    private void setupFrameLayout() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ViewGroup contentArea = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        int[] pos = new int[2];
        if (contentArea != null) {
            contentArea.getLocationOnScreen(pos);
            layoutParams.setMargins(0, -pos[1], 0, 0);
            contentArea.addView(frameLayoutWithHole, layoutParams);
        }
    }

    private int getPxFromDp(int dp) {
        return MeasureUtil.dip2px(activity, dp);
    }

    private void setupBubbleToolTip() {
        if (bubbleToolTips == null) return;

        for (final BubbleToolTip bubbleToolTip : bubbleToolTips) {
            final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            final ViewGroup parent = (activity.getWindow().getDecorView().findViewById(android.R.id.content));

            if (bubbleToolTip != null && bubbleToolTip.view != null && parent != null) {
                final View bubbleTooltipView = bubbleToolTip.view;
                bubbleTooltipView.setVisibility(View.INVISIBLE);

                TextView toolTipDes = bubbleTooltipView.findViewById(R.id.bub_text);
                if (toolTipDes != null) {
                    if (TextUtils.isEmpty(bubbleToolTip.mDescription)) {
                        toolTipDes.setVisibility(View.GONE);
                    } else {
                        toolTipDes.setVisibility(View.VISIBLE);
                        toolTipDes.setText(bubbleToolTip.mDescription);
//                        App.getApplication().setSansRegularFontToView(toolTipDes);
                    }
                }

                if (bubbleToolTip.mWidth != -1) {
                    layoutParams.width = bubbleToolTip.mWidth;
                }
                if (bubbleToolTip.mWidthPercent != -1) {
                    layoutParams.width = (parent.getWidth() * bubbleToolTip.mWidthPercent) / 100;
                }

                int[] pos = new int[2];
                if (bubbleToolTip.target != null) {
                    bubbleToolTip.target.getLocationOnScreen(pos);
                } else {
                    bubbleToolTip.target = parent;
                    bubbleToolTip.target.getLocationOnScreen(pos);
                }
                int targetViewX = pos[0];
                final int targetViewY = pos[1];

                bubbleTooltipView.setLayoutParams(layoutParams);
                bubbleTooltipView.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                int toolTipMeasuredWidth = (bubbleToolTip.mWidth != -1 ? bubbleToolTip.mWidth : bubbleTooltipView.getLayoutParams().width);
                int toolTipMeasuredHeight = bubbleTooltipView.getMeasuredHeight() + bubbleToolTip.marginTop + bubbleToolTip.marginBottom;

                Point resultPoint = new Point();
                final float adjustment = getPxFromDp(10);

                if (toolTipMeasuredWidth > parent.getWidth()) {
                    resultPoint.x = getXForItem(bubbleToolTip.target, bubbleToolTip.mGravity, parent.getWidth(), targetViewX, -adjustment);
                } else {
                    resultPoint.x = getXForItem(bubbleToolTip.target, bubbleToolTip.mGravity, toolTipMeasuredWidth, targetViewX, -adjustment);
                }

                resultPoint.y = getYForItem(bubbleToolTip.target, bubbleToolTip.mGravity, toolTipMeasuredHeight, targetViewY, adjustment) - bubbleToolTip.marginBottom + bubbleToolTip.marginTop;

                addView(bubbleTooltipView, layoutParams);

                if (toolTipMeasuredWidth > parent.getWidth()) {
                    bubbleTooltipView.getLayoutParams().width = parent.getWidth() - bubbleToolTip.marginLeft - bubbleToolTip.marginRight;
                    toolTipMeasuredWidth = parent.getWidth() - bubbleToolTip.marginLeft - bubbleToolTip.marginRight;
                }

                if (resultPoint.x < 0) {
                    bubbleTooltipView.getLayoutParams().width = toolTipMeasuredWidth + resultPoint.x;
                    resultPoint.x = bubbleToolTip.marginLeft;
                    KLog.v("bubbletooltip left boundary check");
                }

                int tempRightX = resultPoint.x + toolTipMeasuredWidth + bubbleToolTip.marginLeft + bubbleToolTip.marginRight;
                if (tempRightX > parent.getWidth()) {
                    resultPoint.x -= tempRightX - parent.getWidth();
                    bubbleTooltipView.getLayoutParams().width = parent.getWidth() - resultPoint.x - bubbleToolTip.marginLeft - bubbleToolTip.marginRight;
                    KLog.v("bubbletooltip right boundary check");
                }

                final int fixedX = resultPoint.x;

                bubbleTooltipView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        bubbleTooltipView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int fixedY;
                        int toolTipHeightAfterLayouted = bubbleTooltipView.getHeight();
                        fixedY = getYForItem(bubbleToolTip.target, bubbleToolTip.mGravity, toolTipHeightAfterLayouted, targetViewY, adjustment) - bubbleToolTip.marginBottom + bubbleToolTip.marginTop;
                        layoutParams.setMargins( fixedX, fixedY, 0, 0);
                        KLog.v("VexGuideView", ": HPtes1 x : " + fixedX + "   y : " + fixedY);

                    }
                });
                KLog.v("VexGuideView", ": HPtes2 x : " + resultPoint.x + "   y : " + resultPoint.y);
                layoutParams.setMargins(resultPoint.x, resultPoint.y, 0, 0);
            }
        }
    }


    private void setupHandGuide() {
        if (handGuides == null) return;

        for (final HandGuide handGuide : handGuides) {
            final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            final ViewGroup parent = ((ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content));

            if (handGuide != null && handGuide.view != null && parent != null) {

                final View tooltipView = handGuide.view;
                tooltipView.setVisibility(View.INVISIBLE);

                int[] pos = new int[2];
                if (handGuide.target != null)
                    handGuide.target.getLocationOnScreen(pos);
                else {
                    handGuide.target = parent;
                    handGuide.target.getLocationOnScreen(pos);
                }
                int targetViewX = pos[0];
                final int targetViewY = pos[1];

                tooltipView.setLayoutParams(layoutParams);
                tooltipView.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                int toolTipMeasuredWidth = handGuide.mWidth != -1 ? handGuide.mWidth : tooltipView.getMeasuredWidth();
                int toolTipMeasuredHeight = tooltipView.getMeasuredHeight();

                Point resultPoint = new Point();
                final float adjustment = getPxFromDp(64);

                if (toolTipMeasuredWidth > parent.getWidth()) {
                    resultPoint.x = getXForItem(handGuide.target, handGuide.mGravity, parent.getWidth(), targetViewX, adjustment) - handGuide.marginLeft - handGuide.marginRight;
                } else {
                    resultPoint.x = getXForItem(handGuide.target, handGuide.mGravity, toolTipMeasuredWidth, targetViewX, adjustment) - handGuide.marginLeft - handGuide.marginRight;
                }

                resultPoint.y = getYForItem(handGuide.target, handGuide.mGravity, toolTipMeasuredHeight, targetViewY, adjustment) - handGuide.marginBottom + handGuide.marginTop;

                addView(tooltipView, layoutParams);

                if (toolTipMeasuredWidth > parent.getWidth()) {
                    tooltipView.getLayoutParams().width = parent.getWidth();
                    toolTipMeasuredWidth = parent.getWidth();
                }

                if (resultPoint.x < 0) {
                    tooltipView.getLayoutParams().width = toolTipMeasuredWidth + resultPoint.x;
                }

                int tempRightX = resultPoint.x + toolTipMeasuredWidth;
                if (tempRightX > parent.getWidth()) {
                    tooltipView.getLayoutParams().width = parent.getWidth() - resultPoint.x;
                }

                tooltipView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        tooltipView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int fixedY;
                        int toolTipHeightAfterLayout = tooltipView.getHeight();
                        fixedY = getYForItem(handGuide.target, handGuide.mGravity, toolTipHeightAfterLayout, targetViewY, adjustment) - handGuide.marginBottom + handGuide.marginTop;
                        layoutParams.setMargins((int) tooltipView.getX(), fixedY, 0, 0);
                    }
                });

                layoutParams.setMargins(resultPoint.x, resultPoint.y, 0, 0);
            }
        }
    }

    private void setupNextButton() {
        if (nextButton == null || nextButton.view == null) return;

        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        final ViewGroup parent = (activity.getWindow().getDecorView().findViewById(android.R.id.content));
        if (parent == null) return;

        final View nextButtonView = nextButton.view;
        nextButtonView.setVisibility(View.INVISIBLE);
//        App.getApplication().setSansBoldFontToView((TextView) nextButtonView.findViewById(R.id.txt_next));

        int[] pos = new int[2];
        parent.getLocationOnScreen(pos);
        int targetViewX = pos[0];
        final int targetViewY = pos[1];

        nextButtonView.setLayoutParams(layoutParams);
        nextButtonView.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        int nextButtonWidth = nextButton.mWidth != -1 ? nextButton.mWidth : nextButtonView.getMeasuredWidth();
        final int nextButtonMeasuredHeight = nextButtonView.getMeasuredHeight();

        Point resultPoint = new Point();
        final float adjustmentX = getPxFromDp(140);
        final float adjustmentY = getPxFromDp(55);

        if (nextButtonWidth > parent.getWidth()) {
            resultPoint.x = getXForItem(parent, nextButton.mGravity, parent.getWidth(), targetViewX, adjustmentX);
        } else {
            resultPoint.x = getXForItem(parent, nextButton.mGravity, nextButtonWidth, targetViewX, adjustmentX);
        }

        resultPoint.y = getYForItem(parent, nextButton.mGravity, nextButtonMeasuredHeight, targetViewY, adjustmentY);

        addView(nextButtonView, layoutParams);

        if (nextButtonWidth > parent.getWidth()) {
            nextButtonView.getLayoutParams().width = parent.getWidth();
            nextButtonWidth = parent.getWidth();
        }

        if (resultPoint.x < 0) {
            nextButtonView.getLayoutParams().width = nextButtonWidth + resultPoint.x;
            resultPoint.x = 0;
        }

        int tempRightX = resultPoint.x + nextButtonWidth;
        if (tempRightX > parent.getWidth()) {
            nextButtonView.getLayoutParams().width = parent.getWidth() - resultPoint.x;
        }

        final int fixedX = resultPoint.x;
        nextButtonView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                nextButtonView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int fixedY;
                int toolTipHeightAfterLayout = nextButtonView.getHeight();
                fixedY = getYForItem(parent, nextButton.mGravity, toolTipHeightAfterLayout, targetViewY, adjustmentY);
                layoutParams.setMargins(fixedX, fixedY, 0, 0);
            }
        });

        layoutParams.setMargins(resultPoint.x, resultPoint.y, 0, 0);
    }

    private void addView(View view, FrameLayout.LayoutParams layoutParams) {
        try {
            if (view.getParent() != null && ((ViewGroup) view.getParent()).getChildCount() > 0) {
                ((ViewGroup) view.getParent()).removeAllViews();
            }
            ((ViewGroup) activity.getWindow().getDecorView()).addView(view, layoutParams);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getXForItem(View target, int gravity, int toolTipMeasuredWidth, int targetViewX, float adjustment) {
        if (target == null) return 0;

        if ((gravity & Gravity.START) == Gravity.START) {
            return targetViewX - toolTipMeasuredWidth + (int) adjustment;
        } else if ((gravity & Gravity.END) == Gravity.END) {
            return targetViewX + target.getWidth() - (int) adjustment;
        } else {
            return targetViewX + target.getWidth() / 2 - toolTipMeasuredWidth / 2;
        }
    }

    private int getYForItem(View target, int gravity, int toolTipMeasuredHeight, int targetViewY, float adjustment) {
        if (target == null) return 0;

        if ((gravity & Gravity.TOP) == Gravity.TOP) {
            if (((gravity & Gravity.START) == Gravity.START) || ((gravity & Gravity.END) == Gravity.END)) {
                return targetViewY - toolTipMeasuredHeight + (int) adjustment;
            } else {
                return targetViewY - toolTipMeasuredHeight - (int) adjustment;
            }
        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            if (((gravity & Gravity.START) == Gravity.START) || ((gravity & Gravity.END) == Gravity.END)) {
                return targetViewY + target.getHeight() - (int) adjustment;
            } else {
                return targetViewY + target.getHeight() + (int) adjustment;
            }

        } else if ((gravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
            if (((gravity & Gravity.START) == Gravity.START) || ((gravity & Gravity.END) == Gravity.END)) {
                return targetViewY;
            } else {
                return targetViewY + target.getHeight() / 2 - toolTipMeasuredHeight / 2;
            }
        } else {
            if (((gravity & Gravity.START) == Gravity.START) || ((gravity & Gravity.END) == Gravity.END)) {
                return targetViewY + target.getHeight() - (int) adjustment;
            } else {
                return targetViewY + target.getHeight() + (int) adjustment;
            }
        }
    }
}
