package com.vexanium.vexgift.module.main.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.box.ui.BoxBaseFragment;
import com.vexanium.vexgift.module.box.ui.BoxHistoryFragment;
import com.vexanium.vexgift.module.buyback.ui.BuybackActivity;
import com.vexanium.vexgift.module.deposit.ui.DepositActivity;
import com.vexanium.vexgift.module.deposit.ui.TokenFreezeActivity;
import com.vexanium.vexgift.module.home.ui.HomeFragment;
import com.vexanium.vexgift.module.luckydraw.ui.LuckyDrawActivity;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.more.ui.WebViewActivity;
import com.vexanium.vexgift.module.notif.ui.NotifFragment;
import com.vexanium.vexgift.module.referral.ui.ReferralActivity;
import com.vexanium.vexgift.module.voucher.ui.VoucherActivity;
import com.vexanium.vexgift.module.wallet.ui.WalletFragmentOld;
import com.vexanium.vexgift.util.AnimUtil;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.CustomTabBarView;
import com.vexanium.vexgift.widget.CustomViewPager;
import com.vexanium.vexgift.widget.guideview.HoleStyle;
import com.vexanium.vexgift.widget.guideview.HoleView;
import com.vexanium.vexgift.widget.guideview.MotionType;
import com.vexanium.vexgift.widget.guideview.Overlay;
import com.vexanium.vexgift.widget.guideview.VexGuideView;
import com.vexanium.vexgift.widget.guideview.bubbletooltip.ArrowDirection;
import com.vexanium.vexgift.widget.guideview.bubbletooltip.BubbleToolTip;
import com.vexanium.vexgift.widget.guideview.handguide.HandGuide;
import com.vexanium.vexgift.widget.guideview.nextbutton.NextButton;

import rx.Observable;
import rx.functions.Action1;

import static android.view.View.VISIBLE;

@ActivityFragmentInject(contentViewId = R.layout.activity_main)
public class MainActivity extends BaseActivity {
    public static final int HOME_FRAGMENT = 0;
    public static final int BOX_FRAGMENT = 1;
    public static final int WALLET_FRAGMENT = 2;
    public static final int NOTIF_FRAGMENT = 3;
    public static final int MORE_FRAGMENT = 4;
    public static final int PAGE_COUNT = 5;
    private final int GUIDE_ANIMATION_INTERVAL = 400;
    int animStepCounter = 0;
    VexGuideView vexGuideView;
    private CustomTabBarView mCustomTabBarView;
    private CustomViewPager mCustomViewPager;
    private HomeFragment homeFragment;
    private BoxBaseFragment boxBaseFragment;
    private WalletFragmentOld walletFragment;
    private NotifFragment notifFragment;
    private MoreFragment moreFragment;
    private MainScreenPagerAdapter mainScreenPagerAdapter;
    private Observable<View> mVoucherGuidanceObservable;
    private Observable<View> mMyBoxGuidanceObservable;
    private Observable<Boolean> mClearGuidanceObservable;
    private View boxFragmentView;
    private CountDownTimer animationCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar();
        setCustomTabs();
        setPagerListener();

        if (getIntent().hasExtra("url")) {
            String url = getIntent().getStringExtra("url");
            KLog.v("MainActivity", "onCreate: getDeeplink " + url);
            getIntent().removeExtra("url");
            openDeepLink(url);
        }
        handlePushAction();
    }

    @Override
    protected void initView() {
        mVoucherGuidanceObservable = RxBus.get().register(RxBus.KEY_TOKEN_VOUCHER_GUIDANCE, View.class);
        mVoucherGuidanceObservable.subscribe(new Action1<View>() {
            @Override
            public void call(final View view) {
                KLog.v("MainActivity", "call: HPtes rxbus guidance called");
                if (view != null) {
                    boolean isAlreadyGuideVoucherToken = TpUtil.getInstance(App.getContext()).getBoolean(TpUtil.KEY_IS_ALREADY_GUIDE_HOME, false);
                    if (!isAlreadyGuideVoucherToken) {
                        openGuidanceHome(view);
                    }
                }
            }
        });

        mMyBoxGuidanceObservable = RxBus.get().register(RxBus.KEY_MY_BOX_GUIDANCE, View.class);
        mMyBoxGuidanceObservable.subscribe(new Action1<View>() {
            @Override
            public void call(View view) {
                KLog.v("MainActivity", "call: HPtes rxbus guidance called");
                if (view != null) {
                    boxFragmentView = view;
                }
            }
        });

        mClearGuidanceObservable = RxBus.get().register(RxBus.KEY_CLEAR_GUIDANCE, Boolean.class);
        mClearGuidanceObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean b) {
                KLog.v("MainActivity", "call: HPtes rxbus clear guidance called");
                resetGuidance(true);
                TpUtil tpUtil = new TpUtil(MainActivity.this);
                tpUtil.put(TpUtil.KEY_IS_ALREADY_GUIDE_HOME, true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("url")) {
            String url = getIntent().getStringExtra("url");
            getIntent().removeExtra("url");
            openDeepLink(url);
        }
        handlePushAction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClearGuidanceObservable != null) {
            RxBus.get().unregister(RxBus.KEY_CLEAR_GUIDANCE, mClearGuidanceObservable);
        }
        if (mVoucherGuidanceObservable != null) {
            RxBus.get().unregister(RxBus.KEY_TOKEN_VOUCHER_GUIDANCE, mVoucherGuidanceObservable);
        }
        if (mMyBoxGuidanceObservable != null) {
            RxBus.get().unregister(RxBus.KEY_MY_BOX_GUIDANCE, mMyBoxGuidanceObservable);
        }

    }

    public void setToolbar() {
        toolbar = findViewById(R.id.toolbar_main);

        mCustomTabBarView = findViewById(R.id.custom_tabbarview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        mCustomTabBarView.addTabView(0, R.drawable.home, R.drawable.home_active);
        mCustomTabBarView.addTabView(1, R.drawable.box, R.drawable.box_active);
        mCustomTabBarView.addTabView(2, R.drawable.wallet, R.drawable.wallet_active);
        mCustomTabBarView.addTabView(3, R.drawable.notif, R.drawable.notif_active);
        mCustomTabBarView.addTabView(4, R.drawable.more, R.drawable.more_active);

        setFragmentToolbar(0);

    }

    public void setFragmentToolbar(int fragment) {
        switch (fragment) {
            case HOME_FRAGMENT:
                break;
            case BOX_FRAGMENT:
                break;
            case WALLET_FRAGMENT:
                break;
            case NOTIF_FRAGMENT:
                break;
            case MORE_FRAGMENT:
                break;
            default:
                break;
        }
    }

    public void openGuidanceHome(View targetView) {
        KLog.v("MainActivity", "openGuidance: guidance open 1");

        final View voucherView = targetView;
//        View tokenView = targetView.findViewById(R.id.token_button);

        final View vexPointView = homeFragment.mVexPointButton;
        if (vexPointView == null) return;

        final Overlay overlay = new Overlay()
                .setBackgroundColor(ColorUtil.getColor(this, R.color.guide_background_color))
                .disableClick(true)
                .disableClickThroughHole(true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        KLog.v("HPtes Overlay clicked");
                    }
                })
                .setHolePadding(5)
                .setEnterAnimation(AnimUtil.getFadeIn(voucherView, GUIDE_ANIMATION_INTERVAL))
                .setExitAnimation(AnimUtil.getFadeOut(voucherView, GUIDE_ANIMATION_INTERVAL))
                .setStyle(HoleStyle.ROUNDED_RECTANGLE);

        final BubbleToolTip bubbleToolTip = new BubbleToolTip(this)
                .arrowDirection(ArrowDirection.TOP_RIGHT)
                .setDescription(getString(R.string.guidance_home_1))
                .setWidthPercent(60)
                .setMargin(0, 0, 8, 0)
                .setGravity(Gravity.BOTTOM)
                .target(vexPointView);

        final BubbleToolTip bubbleToolTip2 = new BubbleToolTip(this)
                .arrowDirection(ArrowDirection.TOP_LEFT)
                .setDescription(getString(R.string.guidance_home_2))
                .setWidthPercent(80)
                .setMargin(0, 0, 0, 0)
                .setGravity(Gravity.BOTTOM)
                .target(targetView);

        final HandGuide handGuide = new HandGuide(this)
                .setGravity(Gravity.BOTTOM | Gravity.END)
                .setMargin(0, 0, -16, -16)
                .target(voucherView);

//        final HandGuide handGuide2 = new HandGuide(this)
//                .setGravity(Gravity.BOTTOM | Gravity.END)
//                .target(targetView);

        final NextButton nextButton = new NextButton(this)
                .setGravity(Gravity.BOTTOM | Gravity.END);

        final HoleView holeViewFirst = new HoleView(targetView).setPadding(this, 0, 0, 0, 0).isAlwaysAllowClick(true);
        final HoleView holeView = new HoleView(vexPointView).setPadding(this, 0, 0, 0, 0);

        animStepCounter = 0;
        final int INITIAL_COUNTER = 1;
        animationCountDownTimer = new CountDownTimer(GUIDE_ANIMATION_INTERVAL * (INITIAL_COUNTER + 13), GUIDE_ANIMATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (vexGuideView == null) return;

                animStepCounter++;
                KLog.v("HPtes ===== " + animStepCounter);
                if (animStepCounter == INITIAL_COUNTER) {
                    vexGuideView.overlay(overlay.setStyle(HoleStyle.ROUNDED_RECTANGLE)).addNewHole(holeView);

                } else if (animStepCounter == INITIAL_COUNTER + 1) {
                    vexGuideView.highlightAnimOn(holeView);

                } else if (animStepCounter == INITIAL_COUNTER + 3) {
                    bubbleToolTip.view.setVisibility(View.VISIBLE);
                    AnimUtil.transBottomIn(bubbleToolTip.view, true);

                } else if (animStepCounter == INITIAL_COUNTER + 5) {
                    vexGuideView.addNewHole(holeViewFirst);

                } else if (animStepCounter == INITIAL_COUNTER + 6) {
                    vexGuideView.highlightAnimOn(holeViewFirst);

                } else if (animStepCounter == INITIAL_COUNTER + 8) {
                    bubbleToolTip2.view.setVisibility(View.VISIBLE);
                    AnimUtil.transBottomIn(bubbleToolTip2.view, true);

                } else if (animStepCounter == INITIAL_COUNTER + 10) {
                    handGuide.view.setVisibility(View.VISIBLE);
                    handGuide.view.startAnimation(handGuide.mEnterAnimation);

//                    handGuide2.view.setVisibility(View.VISIBLE);
//                    handGuide2.view.startAnimation(handGuide2.mEnterAnimation);
                }
                KLog.v("guidance time " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                KLog.v("guidance finish");

                AnimUtil.stopAnimOnAllViews(bubbleToolTip.view, bubbleToolTip2.view, nextButton.view, nextButton.imNext);
                ViewUtil.setVisiblityToAllView(VISIBLE, bubbleToolTip.view, bubbleToolTip2.view, nextButton.view, nextButton.imNext);

                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("Finish Open Guidance Home")
                        .putContentType("Guidance")
                        .putContentId("guide"));
            }
        };
        if (vexGuideView == null) {
            vexGuideView = new VexGuideView().init(MainActivity.this)
                    .motionType(MotionType.CLICK_ONLY)
                    .overlay(overlay.setStyle(HoleStyle.NO_HOLE))
                    .bubbleTooltip(bubbleToolTip2, bubbleToolTip)
                    .handGuide(handGuide)
                    .show();
            animationCountDownTimer.start();
        }
    }

    public void openGuidanceMyBox1() {
        KLog.v("MainActivity", "openGuidance: guidance open 2");

        if (boxFragmentView == null || homeFragment == null) return;

        final View historyView = boxFragmentView.findViewById(R.id.ib_history);

        final View vexPointView = homeFragment.mVexPointButton;
        if (vexPointView == null) return;

        final Overlay overlay = new Overlay()
                .setBackgroundColor(ColorUtil.getColor(this, R.color.guide_background_color))
                .disableClick(true)
                .disableClickThroughHole(true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        KLog.v("HPtes Overlay clicked");
                    }
                })
                .setHolePadding(20)
                .setEnterAnimation(AnimUtil.getFadeIn(historyView, GUIDE_ANIMATION_INTERVAL))
                .setExitAnimation(AnimUtil.getFadeOut(historyView, GUIDE_ANIMATION_INTERVAL))
                .setStyle(HoleStyle.CIRCLE);

        final BubbleToolTip bubbleToolTip = new BubbleToolTip(this)
                .arrowDirection(ArrowDirection.TOP_RIGHT)
                .setDescription(getString(R.string.guidance_box_1))
                .setWidthPercent(80)
                .setMargin(0, 0, 14, 0)
                .setGravity(Gravity.BOTTOM)
                .target(historyView);


        final NextButton nextButton = new NextButton(this)
                .setGravity(Gravity.BOTTOM | Gravity.END);

        final HoleView holeView = new HoleView(historyView).setPadding(this, 0, 0, 0, 0);

        animStepCounter = 0;
        final int INITIAL_COUNTER = 1;
        animationCountDownTimer = new CountDownTimer(GUIDE_ANIMATION_INTERVAL * (INITIAL_COUNTER + 8), GUIDE_ANIMATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (vexGuideView == null) return;

                animStepCounter++;
                KLog.v("HPtes ===== " + animStepCounter);
                if (animStepCounter == INITIAL_COUNTER) {
                    vexGuideView.overlay(overlay.setStyle(HoleStyle.CIRCLE).setHoleRadius(25)).addNewHole(holeView);

                } else if (animStepCounter == INITIAL_COUNTER + 1) {
                    vexGuideView.highlightAnimOn(holeView);

                } else if (animStepCounter == INITIAL_COUNTER + 3) {
                    bubbleToolTip.view.setVisibility(View.VISIBLE);
                    AnimUtil.transBottomIn(bubbleToolTip.view, true);

                } else if (animStepCounter == INITIAL_COUNTER + 5) {
                    AnimUtil.fadeIn(nextButton.view, 1000);
                    nextButton.view.setVisibility(View.VISIBLE);
                    nextButton.imNext.setAnimation(nextButton.mEnterAnimation);
                    nextButton.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resetGuidance(true);

                            openGuidanceMyBox2();
                        }
                    });
                }
                KLog.v("guidance time " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                KLog.v("guidance finish");
                resetGuidance(false);

                AnimUtil.stopAnimOnAllViews(bubbleToolTip.view, nextButton.view, nextButton.imNext);
                ViewUtil.setVisiblityToAllView(VISIBLE, bubbleToolTip.view, nextButton.view, nextButton.imNext);

                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("Finish Open Guidance Box 1")
                        .putContentType("Guidance")
                        .putContentId("guide"));
            }
        };
        if (vexGuideView == null) {
            vexGuideView = new VexGuideView().init(MainActivity.this)
                    .motionType(MotionType.CLICK_ONLY)
                    .overlay(overlay.setStyle(HoleStyle.NO_HOLE))
                    .bubbleTooltip(bubbleToolTip)
                    .nextButton(nextButton)
                    .show();
            animationCountDownTimer.start();
        }
    }

    public void openGuidanceMyBox2() {
        KLog.v("MainActivity", "openGuidance: guidance open 2");

        final View receiveView = boxFragmentView.findViewById(R.id.ib_receive);

        final Overlay overlay = new Overlay()
                .setBackgroundColor(ColorUtil.getColor(this, R.color.guide_background_color))
                .disableClick(true)
                .disableClickThroughHole(true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        KLog.v("HPtes Overlay clicked");
                    }
                })
                .setHolePadding(20)
                .setEnterAnimation(AnimUtil.getFadeIn(receiveView, GUIDE_ANIMATION_INTERVAL))
                .setExitAnimation(AnimUtil.getFadeOut(receiveView, GUIDE_ANIMATION_INTERVAL))
                .setStyle(HoleStyle.CIRCLE);

        final BubbleToolTip bubbleToolTip = new BubbleToolTip(this)
                .arrowDirection(ArrowDirection.TOP_RIGHT)
                .setDescription(getString(R.string.guidance_box_2))
                .setWidthPercent(80)
                .setMargin(0, 0, 62, 0)
                .setGravity(Gravity.BOTTOM)
                .target(receiveView);


        final NextButton nextButton = new NextButton(this)
                .setGravity(Gravity.BOTTOM | Gravity.END);

        final HoleView holeView = new HoleView(receiveView).setPadding(this, 0, 0, 0, 0);

        animStepCounter = 0;
        final int INITIAL_COUNTER = 1;
        animationCountDownTimer = new CountDownTimer(GUIDE_ANIMATION_INTERVAL * (INITIAL_COUNTER + 8), GUIDE_ANIMATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (vexGuideView == null) return;

                animStepCounter++;
                KLog.v("HPtes ===== " + animStepCounter);
                if (animStepCounter == INITIAL_COUNTER) {
                    vexGuideView.overlay(overlay.setStyle(HoleStyle.CIRCLE).setHoleRadius(25)).addNewHole(holeView);

                } else if (animStepCounter == INITIAL_COUNTER + 1) {
                    vexGuideView.highlightAnimOn(holeView);

                } else if (animStepCounter == INITIAL_COUNTER + 3) {
                    bubbleToolTip.view.setVisibility(View.VISIBLE);
                    AnimUtil.transBottomIn(bubbleToolTip.view, true);

                } else if (animStepCounter == INITIAL_COUNTER + 5) {
                    AnimUtil.fadeIn(nextButton.view, 1000);
                    nextButton.view.setVisibility(View.VISIBLE);
                    nextButton.imNext.setAnimation(nextButton.mEnterAnimation);
                    nextButton.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resetGuidance(true);
                            TpUtil tpUtil = new TpUtil(App.getContext());
                            tpUtil.put(TpUtil.KEY_IS_ALREADY_GUIDE_MYBOX, true);
                        }
                    });
                }
                KLog.v("guidance time " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                KLog.v("guidance finish");
                resetGuidance(false);

                AnimUtil.stopAnimOnAllViews(bubbleToolTip.view, nextButton.view, nextButton.imNext);
                ViewUtil.setVisiblityToAllView(VISIBLE, bubbleToolTip.view, nextButton.view, nextButton.imNext);

                TpUtil tpUtil = new TpUtil(App.getContext());
                tpUtil.put(TpUtil.KEY_IS_ALREADY_GUIDE_MYBOX, true);

                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("Finish Open Guidance Box 2")
                        .putContentType("Guidance")
                        .putContentId("guide"));
            }
        };
        if (vexGuideView == null) {
            vexGuideView = new VexGuideView().init(MainActivity.this)
                    .motionType(MotionType.CLICK_ONLY)
                    .overlay(overlay.setStyle(HoleStyle.NO_HOLE))
                    .bubbleTooltip(bubbleToolTip)
                    .nextButton(nextButton)
                    .show();
            animationCountDownTimer.start();
        }
    }

    public void releaseAnimTimer() {
        KLog.v("releaseAnimTimer");
        if (animationCountDownTimer != null) {
            animationCountDownTimer.cancel();
            animationCountDownTimer = null;
        }
    }

    private void resetGuidance(boolean withLayout) {
        animStepCounter = 0;
        releaseAnimTimer();

        if (withLayout && vexGuideView != null) {
            KLog.v("ctGuideView cleanUpAll");
            vexGuideView.cleanUpAll(true);
            vexGuideView = null;
        }
    }

    private void setPagerListener() {
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int lastPagePosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCustomTabBarView.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                KLog.i("Page: " + position);
                mCustomTabBarView.onPageSelected(position);
                setFragmentToolbar(position);
                Fragment fragment = mainScreenPagerAdapter.getRegisteredFragment(position);
                if (fragment != null) {
                    fragment.onResume();
                }

                Fragment lastFragment = mainScreenPagerAdapter.getRegisteredFragment(lastPagePosition);
                if (lastFragment != null) {
                    lastFragment.onPause();

                    if (lastFragment instanceof BoxBaseFragment) {
                        ((BoxBaseFragment) lastFragment).onCustomPause();
                    }
                }

                lastPagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mCustomTabBarView.onPageScrollStateChanged(state);
            }
        });
    }

    private void setCustomTabs() {
        mainScreenPagerAdapter = new MainScreenPagerAdapter(getSupportFragmentManager());
        mCustomViewPager = findViewById(R.id.custom_viewpager);
        mCustomViewPager.setAdapter(mainScreenPagerAdapter);
        mCustomTabBarView.setViewPager(mCustomViewPager);
        mCustomViewPager.setOffscreenPageLimit(PAGE_COUNT);
        mCustomViewPager.setPagingEnabled(true);
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == BOX_FRAGMENT) {
                    boolean isAlreadyGuideMyBox = TpUtil.getInstance(App.getContext()).getBoolean(TpUtil.KEY_IS_ALREADY_GUIDE_MYBOX, false);
                    if (!isAlreadyGuideMyBox && boxFragmentView != null) {
                        openGuidanceMyBox1();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mCustomViewPager.setCurrentItem(0, false);
    }

    private void handlePushAction() {
        String url = getIntent().getStringExtra("t_url");
        if (!TextUtils.isEmpty(url)) {
            openDeepLink(url);
            getIntent().removeExtra("t_url");
        }
    }

    public void openDeepLink(String url) {
        KLog.v("MainActivity", "openDeepLink: " + url);

        boolean isAlreadyHandled = StaticGroup.handleUrl(this, url);
        Intent intent;
        if (!isAlreadyHandled) {
            Uri uri = Uri.parse(url);
            String path = url.replace("http://www.vexgift.com/", "")
                    .replace("https://www.vexgift.com/", "")
                    .replace("www.vexgift.com/", "")
                    .replace("vexgift://", "");

            if (path.startsWith("main")) {

            } else if (path.startsWith("voucher")) {
                String sId = uri.getQueryParameter("id");
                int id = 0;
                try {
                    id = Integer.parseInt(sId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                intent = new Intent(this, VoucherActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

            } else if (path.startsWith("in.app.web")) {
                String sUrl = uri.getQueryParameter("l");
                String sTitle = uri.getQueryParameter("t");

                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", sUrl);
                intent.putExtra("title", sTitle);
                startActivity(intent);

            } else if (path.startsWith("tokenfreeze")) {
                if (StaticGroup.isTokenFreezeBannerActive()) {
                    intent = new Intent(this, TokenFreezeActivity.class);
                    startActivity(intent);
                }

            } else if (path.startsWith("token")) {
                String sId = uri.getQueryParameter("id");
                int id = 0;
                try {
                    id = Integer.parseInt(sId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                intent = new Intent(this, VoucherActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("isToken", true);
                startActivity(intent);

            } else if (path.startsWith("notif")) {
                gotoPage(NOTIF_FRAGMENT);

            } else if (path.startsWith("deposit")) {
                if (StaticGroup.isDepositAvailable()) {
                    String sId = uri.getQueryParameter("id");
                    int id = 0;
                    try {
                        id = Integer.parseInt(sId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(this, DepositActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }

            } else if (path.startsWith("receive")) {
                String code = uri.getQueryParameter("c");
                intent = new Intent(this, VoucherActivity.class);
                intent.putExtra("code", code);
                startActivity(intent);

            } else if (path.startsWith("box")) {
                gotoPage(BOX_FRAGMENT);

            }  else if (path.startsWith("luckydraw")) {
                if (StaticGroup.isDepositAvailable()) {
                    String sId = uri.getQueryParameter("id");
                    int id = 0;
                    try {
                        id = Integer.parseInt(sId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(this, LuckyDrawActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }

            } else if (path.startsWith("buyback")) {
                if (StaticGroup.isBuybackBannerActive()) {
                    String sId = uri.getQueryParameter("id");
                    int id = 0;
                    try {
                        id = Integer.parseInt(sId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(this, BuybackActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }

            } else if (path.startsWith("referral")) {
                if (StaticGroup.isReferralBannerActive()) {
                    intent = new Intent(this, ReferralActivity.class);
                    startActivity(intent);
                }
            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                StaticGroup.openAndroidBrowser(this, url);
            }
        }

    }

    public void gotoPage(int page, int secondaryPage) {
        mCustomViewPager.setCurrentItem(page, false);

        if (page == 1) {
            Fragment fragment = mainScreenPagerAdapter.getRegisteredFragment(1);
            if (fragment instanceof BoxBaseFragment) {
                ((BoxBaseFragment) fragment).changeBoxTab(secondaryPage);
            }
        }
    }

    public void gotoPage(int page) {
        mCustomViewPager.setCurrentItem(page, false);

        if (page >= 0 && page < PAGE_COUNT) {
            mainScreenPagerAdapter.getRegisteredFragment(page);
        }
    }

    public boolean isEligibleToExit() {
        Fragment fragment = mainScreenPagerAdapter.getRegisteredFragment(mCustomViewPager.getCurrentItem());
        if (fragment instanceof BoxBaseFragment) {
            BoxBaseFragment boxBaseFragment = (BoxBaseFragment) fragment;
            if (boxBaseFragment.getCurrentFragment() instanceof BoxHistoryFragment) {
                boxBaseFragment.changeFragment(false);
                return false;
            }
        }
        return true;
    }

    public class MainScreenPagerAdapter extends FragmentStatePagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        MainScreenPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case HOME_FRAGMENT:
                    if (homeFragment == null) {
                        homeFragment = HomeFragment.newInstance();
                    }
                    return homeFragment;
                case BOX_FRAGMENT:
                    if (boxBaseFragment == null) {
                        boxBaseFragment = BoxBaseFragment.newInstance();
                    }
                    return boxBaseFragment;
                case WALLET_FRAGMENT:
                    if (walletFragment == null) {
                        walletFragment = WalletFragmentOld.newInstance();
                    }
                    return walletFragment;
                case NOTIF_FRAGMENT:
                    if (notifFragment == null) {
                        notifFragment = NotifFragment.newInstance();
                    }
                    return notifFragment;
                case MORE_FRAGMENT:
                    if (moreFragment == null) {
                        moreFragment = MoreFragment.newInstance();
                    }
                    return moreFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}
