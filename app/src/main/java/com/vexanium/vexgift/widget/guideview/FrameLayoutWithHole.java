package com.vexanium.vexgift.widget.guideview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.socks.library.KLog;
import com.vexanium.vexgift.util.MeasureUtil;

import java.util.ArrayList;


public class FrameLayoutWithHole extends FrameLayout {
    public final int DEFAULT_HOLE_ALPHA_VALUE = 127;
    public final int DEFAULT_SCALE_ALPHA_VALUE = 255;
    public int scaleAnimStep = 0;
    public int fadeVal = DEFAULT_SCALE_ALPHA_VALUE;
    public int holeAlphaVal = DEFAULT_HOLE_ALPHA_VALUE;
    private Activity mActivity;
    private MotionType mMotionType;
    private Paint mEraser;
    private Bitmap mEraserBitmap;
    private Bitmap mAnimationBitmap;
    private Canvas mEraserCanvas;
    private Canvas mAnimationCanvas;
    private Paint whiteOverlay;
    private Paint holeOverlay;
    private ArrayList<HoleView> holesViews;
    private int mRadius;
    private Overlay mOverlay;
    private Activity activity;
    private HoleView targetAnimHole;
    private HoleView newHole;
    private boolean mCleanUpLock = false;

    public FrameLayoutWithHole(Activity context, ArrayList<HoleView> views, MotionType motionType, Overlay overlay) {
        super(context);
        mActivity = context;
        holesViews = views;
        mOverlay = overlay;
        mRadius = 10;
        mMotionType = motionType;
        this.activity = context;

        init();
        initRecf();
    }


    public FrameLayoutWithHole(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHolesViews(ArrayList<HoleView> holesViews) {
        this.holesViews = holesViews;
        initRecf();
    }

    public void addNewHoleWithAnim(HoleView holeView) {
        if (this.holesViews == null) this.holesViews = new ArrayList<>();
        this.holesViews.add(holeView);
        this.newHole = holeView;
        initRecf();
        invalidate();
    }

    private void enforceMotionTypes() {
        if (holesViews != null) {
            for (final HoleView holeView : holesViews) {
                if (mMotionType != null && mMotionType == MotionType.CLICK_ONLY) {
                    holeView.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (holeView.getParent() != null)
                                holeView.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                } else if (mMotionType != null && mMotionType == MotionType.SWIPE_ONLY) {
                    holeView.setClickable(false);
                } else if (mMotionType != null && mMotionType == MotionType.DISALLOW_ALL) {
                    holeView.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (holeView.getParent() != null)
                                holeView.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                    holeView.setClickable(false);
                }
            }
        }
    }

    private void deforceMotionTypes() {
        if (holesViews != null) {
            for (final HoleView holeView : holesViews) {
                if (holeView == null) continue;
                if (mMotionType != null && mMotionType == MotionType.CLICK_ONLY) {
                    holeView.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (holeView.getParent() != null)
                                holeView.getParent().requestDisallowInterceptTouchEvent(false);
                            return false;
                        }
                    });
                } else if (mMotionType != null && mMotionType == MotionType.SWIPE_ONLY) {
                    holeView.setClickable(true);
                } else if (mMotionType != null && mMotionType == MotionType.DISALLOW_ALL) {
                    holeView.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (holeView.getParent() != null)
                                holeView.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                        }
                    });
                    holeView.setClickable(true);
                }
            }
        }
    }

    public int getPxFromDp(int px) {
        return MeasureUtil.dip2px(activity, px);
    }

    public void initRecf() {

        mRadius = 10;
        enforceMotionTypes();

        if (holesViews == null) return;
        for (HoleView holeView : holesViews) {
            int[] pos = new int[2];
            if (holeView.view != null) {
                holeView.view.getLocationOnScreen(pos);
                holeView.loc = pos;
            }

            if (mOverlay != null && mOverlay.mHoleStyle != HoleStyle.UN_SET) {
                if (holeView.holeStyle == HoleStyle.UN_SET) {
                    holeView.holeStyle = mOverlay.mHoleStyle;
                }
            }


            int padding = getPxFromDp(20);

            if (holeView.getHeight() > holeView.getWidth()) {
                holeView.radius = holeView.getHeight() / 2 + padding;
            } else {
                holeView.radius = holeView.getWidth() / 2 + padding;
            }

            // Init a RectF to be used in OnDraw for a ROUNDED_RECTANGLE HoleStyle Overlay
            if ((mOverlay != null && mOverlay.mHoleStyle == HoleStyle.ROUNDED_RECTANGLE) || holeView.holeStyle == HoleStyle.ROUNDED_RECTANGLE) {

                int recfFPaddingPx = getPxFromDp(mOverlay.mPaddingDp);
                holeView.mRectF = new RectF(pos[0] - recfFPaddingPx - holeView.paddingLeft + mOverlay.mHoleOffsetLeft,
                        pos[1] - recfFPaddingPx - holeView.paddingTop + mOverlay.mHoleOffsetTop,
                        pos[0] + holeView.getWidth() + holeView.paddingRight + recfFPaddingPx + mOverlay.mHoleOffsetLeft,
                        pos[1] + holeView.getHeight() + recfFPaddingPx + holeView.paddingBottom + mOverlay.mHoleOffsetTop);
            }

        }
    }

    private void init() {
        setWillNotDraw(false);

        Point size = new Point();
        size.x = mActivity.getResources().getDisplayMetrics().widthPixels;
        size.y = mActivity.getResources().getDisplayMetrics().heightPixels;

        mEraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        mAnimationBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);
        mAnimationCanvas = new Canvas(mAnimationBitmap);

        whiteOverlay = new Paint();
        final int WHITE_TRANS = 0xaaFFFFFF;
        whiteOverlay.setColor(WHITE_TRANS);

        mEraser = new Paint();
        mEraser.setColor(Color.WHITE);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        holeOverlay = new Paint();
        if (mOverlay != null && mOverlay.mBackgroundColor != 0) {
            holeOverlay.setColor(mOverlay.mBackgroundColor);
        } else {
            holeOverlay.setColor(Color.TRANSPARENT);
        }

    }

    protected void cleanUp() {
        if (getParent() != null) {
            if (mOverlay != null && mOverlay.mExitAnimation != null) {
                performOverlayExitAnimation();
            } else {
                ((ViewGroup) this.getParent()).removeView(this);
            }
        }
    }

    protected void endup() {
        if (getParent() != null) {
            clearAnimation();
            ((ViewGroup) this.getParent()).removeView(this);
        }
    }

    private void performOverlayExitAnimation() {
        if (!mCleanUpLock && mOverlay != null && mOverlay.mExitAnimation != null) {
            final FrameLayout _pointerToFrameLayout = this;
            mCleanUpLock = true;
            mOverlay.mExitAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (_pointerToFrameLayout != null && _pointerToFrameLayout.getParent() != null)
                        ((ViewGroup) _pointerToFrameLayout.getParent()).removeView(_pointerToFrameLayout);
                }
            });
            this.startAnimation(mOverlay.mExitAnimation);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /* cleanup reference to prevent memory leak */

        mAnimationCanvas.setBitmap(null);
        mAnimationBitmap = null;
        mEraserCanvas.setBitmap(null);
        mEraserBitmap = null;
        deforceMotionTypes();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //first check if the location button should handle the touch event
        if (holesViews != null) {
            //check if the viewholes is only one and it is viewpager,
            if (holesViews.get(0) != null) {
                if (holesViews.get(0).isViewPager) {
                    if (isWithinMainFrame(ev) && mOverlay != null && mOverlay.mDisableClickThroughHole) {
                        return false;
                    } else if (isWithinMainFrame(ev)) {
                        return true;
                    }
                }
            }
            if (isWithinClickAllowedLayout(ev) && mOverlay != null && mOverlay.mDisableClickThroughHole) {
                ev.setAction(MotionEvent.ACTION_DOWN);
                return false;
            } else if (isWithinButton(ev) && mOverlay != null && mOverlay.mDisableClickThroughHole) {
                return true;
            } else if (isWithinButton(ev)) {
                return false;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean isWithinButton(MotionEvent ev) {
        int[] pos = new int[2];
        boolean isAllow = false;

        for (HoleView holeView : holesViews) {
            int padding = getPxFromDp(mOverlay.mPaddingDp);
            int tolerance = getPxFromDp(2);
            holeView.getLocationOnScreen(pos);
            switch (holeView.holeStyle) {
                case RECTANGLE:
                    isAllow = isAllow ||
                            (ev.getRawY() >= (pos[1] - padding + mOverlay.mHoleOffsetTop) &&
                                    ev.getRawY() <= (pos[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop)
                                    &&
                                    ev.getRawX() >= (pos[0] - padding + mOverlay.mHoleOffsetLeft) &&
                                    ev.getRawX() <= (pos[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft)
                            );
                    break;
                case ROUNDED_RECTANGLE:
                    int recfFPaddingPx = getPxFromDp(mOverlay.mPaddingDp);
                    isAllow = isAllow ||
                            (ev.getRawY() >= (pos[1] - recfFPaddingPx - holeView.paddingTop + mOverlay.mHoleOffsetTop - tolerance) &&
                                    ev.getRawY() <= (pos[1] + holeView.getHeight() + recfFPaddingPx + holeView.paddingBottom + mOverlay.mHoleOffsetTop + tolerance)
                                    &&
                                    ev.getRawX() >= (pos[0] - recfFPaddingPx - holeView.paddingLeft + mOverlay.mHoleOffsetLeft - tolerance) &&
                                    ev.getRawX() <= (pos[0] + holeView.getWidth() + holeView.paddingRight + recfFPaddingPx + mOverlay.mHoleOffsetLeft + tolerance)
                            );
                    break;
                case CIRCLE:
                    isAllow = isAllow ||
                            (ev.getRawY() >= (pos[1] - padding + mOverlay.mHoleOffsetTop) &&
                                    ev.getRawY() <= (pos[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop)
                                    &&
                                    ev.getRawX() >= (pos[0] - padding + mOverlay.mHoleOffsetLeft) &&
                                    ev.getRawX() <= (pos[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft)
                            );
                    break;
                default:
                    isAllow = isAllow || (ev.getRawY() >= pos[1] && ev.getRawY() <= (pos[1] + holeView.getHeight())
                            && ev.getRawX() >= pos[0] && ev.getRawX() <= (pos[0] + holeView.getWidth())
                    );
            }

        }

        return isAllow;
    }

    private boolean isWithinClickAllowedLayout(MotionEvent ev) {
        int[] pos = new int[2];
        boolean isAllow = false;
        for (HoleView holeView : holesViews) {
            if (!holeView.isAlwaysAllowClick) continue;

            int padding = getPxFromDp(mOverlay.mPaddingDp);
            int tolerance = MeasureUtil.dip2px(activity, 3);
            holeView.getLocationOnScreen(pos);
            switch (holeView.holeStyle) {
                case RECTANGLE:
                    isAllow = isAllow ||
                            (ev.getRawY() >= (pos[1] - padding + mOverlay.mHoleOffsetTop) &&
                                    ev.getRawY() <= (pos[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop)
                                    &&
                                    ev.getRawX() >= (pos[0] - padding + mOverlay.mHoleOffsetLeft) &&
                                    ev.getRawX() <= (pos[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft)
                            );
                    break;
                case ROUNDED_RECTANGLE:
                    int recfFPaddingPx = getPxFromDp(mOverlay.mPaddingDp);
                    isAllow = isAllow ||
                            (ev.getRawY() >= (pos[1] - recfFPaddingPx - holeView.paddingTop + mOverlay.mHoleOffsetTop - tolerance) &&
                                    ev.getRawY() <= (pos[1] + holeView.getHeight() + recfFPaddingPx + holeView.paddingBottom + mOverlay.mHoleOffsetTop + tolerance)
                                    &&
                                    ev.getRawX() >= (pos[0] - recfFPaddingPx - holeView.paddingLeft + mOverlay.mHoleOffsetLeft - tolerance) &&
                                    ev.getRawX() <= (pos[0] + holeView.getWidth() + holeView.paddingRight + recfFPaddingPx + mOverlay.mHoleOffsetLeft + tolerance)
                            );
                    break;
                case CIRCLE:
                    isAllow = isAllow ||
                            (ev.getRawY() >= (pos[1] - padding + mOverlay.mHoleOffsetTop) &&
                                    ev.getRawY() <= (pos[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop)
                                    &&
                                    ev.getRawX() >= (pos[0] - padding + mOverlay.mHoleOffsetLeft) &&
                                    ev.getRawX() <= (pos[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft)
                            );
                    break;
                default:
                    isAllow = isAllow || (ev.getRawY() >= pos[1] && ev.getRawY() <= (pos[1] + holeView.getHeight())
                            && ev.getRawX() >= pos[0] && ev.getRawX() <= (pos[0] + holeView.getWidth())
                    );
            }
        }
        return isAllow;
    }

    private boolean isWithinMainFrame(MotionEvent ev) {
        int[] pos = new int[2];
        boolean isAllow = false;
        if (holesViews == null) return false;
        for (HoleView holeView : holesViews) {
            holeView.getLocationOnScreen(pos);
            isAllow = isAllow || (ev.getRawY() >= pos[1] && ev.getRawY() <= (pos[1] + holeView.getHeight() - 240)
                    && ev.getRawX() >= pos[0] && ev.getRawX() <= (pos[0] + holeView.getWidth())
            );
        }

        return isAllow;
    }

    public void animateOn(HoleView holeView) {
        this.targetAnimHole = holeView;
        scaleAnimStep = 0;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mOverlay != null && mOverlay.mHoleStyle != null && mEraserBitmap != null && mAnimationBitmap != null && mEraserCanvas != null && mAnimationCanvas != null) {
            mEraserBitmap.eraseColor(Color.TRANSPARENT);
            mAnimationBitmap.eraseColor(Color.TRANSPARENT);

            mEraserCanvas.drawColor(mOverlay.mBackgroundColor);
            int padding = getPxFromDp(mOverlay.mPaddingDp);

            //Animation works here
            final int GAP_SIZE = 5;
            final int SCALE_SIZE = 12;
            final int FADE_STEP = 20;

            KLog.v("Testing onDraw is call");

            if (targetAnimHole != null && holesViews != null) {
                int animPadding = GAP_SIZE * scaleAnimStep;
                if (animPadding < MeasureUtil.dip2px(activity, SCALE_SIZE)) {
                    for (HoleView holeView : holesViews) {
                        if (holeView != null && holeView.equals(targetAnimHole) && whiteOverlay != null) {
                            whiteOverlay.setAlpha(fadeVal);
                            if (mOverlay.mHoleStyle == HoleStyle.RECTANGLE) {

                                mEraserCanvas.drawRect(
                                        holeView.loc[0] - padding + mOverlay.mHoleOffsetLeft - animPadding,
                                        holeView.loc[1] - padding + mOverlay.mHoleOffsetTop - animPadding,
                                        holeView.loc[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft + animPadding,
                                        holeView.loc[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop + animPadding, whiteOverlay);

                            } else if (mOverlay.mHoleStyle == HoleStyle.ROUNDED_RECTANGLE) {

                                int roundedCornerRadiusPx;
                                if (mOverlay.mRoundedCornerRadiusDp != 0) {
                                    roundedCornerRadiusPx = getPxFromDp(mOverlay.mRoundedCornerRadiusDp);
                                } else {
                                    roundedCornerRadiusPx = getPxFromDp(10);
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mEraserCanvas.drawRoundRect(holeView.mRectF.left - animPadding,
                                            holeView.mRectF.top - animPadding,
                                            holeView.mRectF.right + animPadding,
                                            holeView.mRectF.bottom + animPadding,
                                            roundedCornerRadiusPx, roundedCornerRadiusPx, whiteOverlay);
                                } else {
                                    RectF rectF = holeView.mRectF;
                                    RectF rectFAnim = new RectF(rectF.left - animPadding, rectF.top - animPadding, rectF.right + animPadding, rectF.bottom + animPadding);
                                    mEraserCanvas.drawRoundRect(rectFAnim, roundedCornerRadiusPx, roundedCornerRadiusPx, whiteOverlay);
                                }
                            } else if (mOverlay.mHoleStyle == HoleStyle.CIRCLE) {
                                mEraserCanvas.drawCircle(
                                        holeView.loc[0] + holeView.getWidth() / 2 + mOverlay.mHoleOffsetLeft,
                                        holeView.loc[1] + holeView.getHeight() / 2 + mOverlay.mHoleOffsetTop,
                                        mOverlay.mHoleRadius + animPadding * 2, whiteOverlay);
                            }
                            break;
                        }
                    }
                    scaleAnimStep++;
                    fadeVal -= FADE_STEP;
                } else if (fadeVal > 0 && holesViews != null) {
                    for (HoleView holeView : holesViews) {
                        if (holeView != null && holeView.equals(targetAnimHole)) {
                            whiteOverlay.setAlpha(fadeVal);
                            if (mOverlay.mHoleStyle == HoleStyle.RECTANGLE) {

                                mEraserCanvas.drawRect(
                                        holeView.loc[0] - padding + mOverlay.mHoleOffsetLeft - animPadding,
                                        holeView.loc[1] - padding + mOverlay.mHoleOffsetTop - animPadding,
                                        holeView.loc[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft + animPadding,
                                        holeView.loc[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop + animPadding, whiteOverlay);

                            } else if (mOverlay.mHoleStyle == HoleStyle.ROUNDED_RECTANGLE) {

                                int roundedCornerRadiusPx;
                                if (mOverlay.mRoundedCornerRadiusDp != 0) {
                                    roundedCornerRadiusPx = getPxFromDp(mOverlay.mRoundedCornerRadiusDp);
                                } else {
                                    roundedCornerRadiusPx = getPxFromDp(10);
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mEraserCanvas.drawRoundRect(holeView.mRectF.left - animPadding,
                                            holeView.mRectF.top - animPadding,
                                            holeView.mRectF.right + animPadding,
                                            holeView.mRectF.bottom + animPadding,
                                            roundedCornerRadiusPx, roundedCornerRadiusPx, whiteOverlay);
                                } else {
                                    RectF rectF = holeView.mRectF;
                                    RectF rectFAnim = new RectF(rectF.left - animPadding, rectF.top - animPadding, rectF.right + animPadding, rectF.bottom + animPadding);
                                    mEraserCanvas.drawRoundRect(rectFAnim, roundedCornerRadiusPx, roundedCornerRadiusPx, whiteOverlay);
                                }

                            } else if (mOverlay.mHoleStyle == HoleStyle.CIRCLE) {
                                mEraserCanvas.drawCircle(
                                        holeView.loc[0] + holeView.getWidth() / 2 + mOverlay.mHoleOffsetLeft,
                                        holeView.loc[1] + holeView.getHeight() / 2 + mOverlay.mHoleOffsetTop,
                                        mOverlay.mHoleRadius + animPadding * 2, whiteOverlay);
                            }
                            break;
                        }
                    }
                    fadeVal -= FADE_STEP;
                } else {
                    fadeVal = DEFAULT_SCALE_ALPHA_VALUE;
                    whiteOverlay.setAlpha(0);
                    targetAnimHole = null;
                }
                invalidate();
//            KLog.v("Testing invalidate");
            }


            if (holesViews != null) {
                for (HoleView holeView : holesViews) {
                    if (holeView != null && mEraser != null) {
                        if (mOverlay.mHoleStyle == HoleStyle.RECTANGLE) {

                            mEraserCanvas.drawRect(
                                    holeView.loc[0] - padding + mOverlay.mHoleOffsetLeft,
                                    holeView.loc[1] - padding + mOverlay.mHoleOffsetTop,
                                    holeView.loc[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft,
                                    holeView.loc[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop, mEraser);

                        } else if (mOverlay.mHoleStyle == HoleStyle.NO_HOLE) {

                            mEraserCanvas.drawCircle(
                                    holeView.loc[0] + holeView.getWidth() / 2 + mOverlay.mHoleOffsetLeft,
                                    holeView.loc[1] + holeView.getHeight() / 2 + mOverlay.mHoleOffsetTop,
                                    0, mEraser);

                        } else if (mOverlay.mHoleStyle == HoleStyle.ROUNDED_RECTANGLE) {

                            int roundedCornerRadiusPx;
                            if (mOverlay.mRoundedCornerRadiusDp != 0) {
                                roundedCornerRadiusPx = getPxFromDp(mOverlay.mRoundedCornerRadiusDp);
                            } else {
                                roundedCornerRadiusPx = getPxFromDp(10);
                            }
                            mEraserCanvas.drawRoundRect(holeView.mRectF, roundedCornerRadiusPx, roundedCornerRadiusPx, mEraser);

                        } else {
                            int holeRadius = mOverlay.mHoleRadius != Overlay.NOT_SET ? mOverlay.mHoleRadius : mRadius;
                            holeRadius = MeasureUtil.dip2px(activity, holeRadius);

                            if (holeView.holeStyle != HoleStyle.UN_SET && holeView.holeStyle != mOverlay.mHoleStyle) {
                                if (holeView.holeStyle == HoleStyle.RECTANGLE) {
                                    mEraserCanvas.drawRect(
                                            holeView.loc[0] - padding + mOverlay.mHoleOffsetLeft,
                                            holeView.loc[1] - padding + mOverlay.mHoleOffsetTop,
                                            holeView.loc[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft,
                                            holeView.loc[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop, mEraser);

                                } else if (holeView.holeStyle == HoleStyle.ROUNDED_RECTANGLE) {
                                    int roundedCornerRadiusPx;
                                    if (mOverlay.mRoundedCornerRadiusDp != 0) {
                                        roundedCornerRadiusPx = getPxFromDp(mOverlay.mRoundedCornerRadiusDp);
                                    } else {
                                        roundedCornerRadiusPx = getPxFromDp(10);
                                    }
                                    mEraserCanvas.drawRoundRect(holeView.mRectF, roundedCornerRadiusPx, roundedCornerRadiusPx, mEraser);
                                }
                            } else {
                                mEraserCanvas.drawCircle(
                                        holeView.loc[0] + holeView.getWidth() / 2 + mOverlay.mHoleOffsetLeft - holeView.paddingLeft,
                                        holeView.loc[1] + holeView.getHeight() / 2 + mOverlay.mHoleOffsetTop - holeView.paddingTop,
                                        holeRadius, mEraser);
                            }

                        }
                    }
                }
            }


            final int HOLE_FADE_STEP = 20;
            if (holeOverlay != null) {
                if (newHole != null && holesViews != null) {
                    if (holeAlphaVal > 0) {
                        for (HoleView holeView : holesViews) {
                            if (holeView != null && holeView.equals(newHole)) {
                                holeOverlay.setAlpha(holeAlphaVal);
                                if (mOverlay.mHoleStyle == HoleStyle.RECTANGLE) {

                                    mAnimationCanvas.drawRect(
                                            holeView.loc[0] - padding + mOverlay.mHoleOffsetLeft,
                                            holeView.loc[1] - padding + mOverlay.mHoleOffsetTop,
                                            holeView.loc[0] + holeView.getWidth() + padding + mOverlay.mHoleOffsetLeft,
                                            holeView.loc[1] + holeView.getHeight() + padding + mOverlay.mHoleOffsetTop, holeOverlay);

                                } else if (mOverlay.mHoleStyle == HoleStyle.ROUNDED_RECTANGLE) {

                                    int roundedCornerRadiusPx;
                                    if (mOverlay.mRoundedCornerRadiusDp != 0) {
                                        roundedCornerRadiusPx = getPxFromDp(mOverlay.mRoundedCornerRadiusDp);
                                    } else {
                                        roundedCornerRadiusPx = getPxFromDp(10);
                                    }
                                    mAnimationCanvas.drawRoundRect(holeView.mRectF, roundedCornerRadiusPx, roundedCornerRadiusPx, holeOverlay);
                                }
                            }
                        }
                        holeAlphaVal -= HOLE_FADE_STEP;
                    } else {
                        holeAlphaVal = DEFAULT_HOLE_ALPHA_VALUE;
                        holeOverlay.setAlpha(0);
                        newHole = null;
                    }
                    invalidate();
                }

            }

            canvas.drawBitmap(mEraserBitmap, 0, 0, null);
            canvas.drawBitmap(mAnimationBitmap, 0, 0, null);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mOverlay != null && mOverlay.mEnterAnimation != null) {
            this.startAnimation(mOverlay.mEnterAnimation);
        }
    }

}
