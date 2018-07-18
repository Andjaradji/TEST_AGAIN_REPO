package com.vexanium.vexgift.module.main.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.box.ui.BoxBaseFragment;
import com.vexanium.vexgift.module.box.ui.BoxFragment;
import com.vexanium.vexgift.module.home.ui.HomeFragment;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.notif.ui.NotifFragment;
import com.vexanium.vexgift.module.wallet.ui.WalletFragment;
import com.vexanium.vexgift.widget.CustomTabBarView;
import com.vexanium.vexgift.widget.CustomViewPager;

@ActivityFragmentInject(contentViewId = R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private CustomTabBarView mCustomTabBarView;
    private CustomViewPager mCustomViewPager;

    private HomeFragment homeFragment;
    private BoxBaseFragment boxBaseFragment;
    private WalletFragment walletFragment;
    private NotifFragment notifFragment;
    private MoreFragment moreFragment;

    public static final int HOME_FRAGMENT = 0;
    public static final int BOX_FRAGMENT = 1;
    public static final int WALLET_FRAGMENT = 2;
    public static final int NOTIF_FRAGMENT = 3;
    public static final int MORE_FRAGMENT = 4;
    public static final int PAGE_COUNT = 5;

    private MainScreenPagerAdapter mainScreenPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar();
        setCustomTabs();
        setPagerListener();

    }

    @Override
    protected void initView() {
        setToolbar();
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);

        mCustomTabBarView = (CustomTabBarView) findViewById(R.id.custom_tabbarview);

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
        TextView textView = findViewById(R.id.tv_toolbar_title);
        ImageView logo = findViewById(R.id.iv_vex_logo);

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

    private void setPagerListener() {
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCustomTabBarView.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                KLog.i("Page: " + position);
                mCustomTabBarView.onPageSelected(position);
                setFragmentToolbar(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mCustomTabBarView.onPageScrollStateChanged(state);
            }
        });
    }

    private void setCustomTabs() {
        mainScreenPagerAdapter = new MainScreenPagerAdapter(getSupportFragmentManager());
        mCustomViewPager = (CustomViewPager) findViewById(R.id.custom_viewpager);
        mCustomViewPager.setAdapter(mainScreenPagerAdapter);
        mCustomTabBarView.setViewPager(mCustomViewPager);
        mCustomViewPager.setOffscreenPageLimit(PAGE_COUNT);
        mCustomViewPager.setPagingEnabled(true);
        mCustomViewPager.setCurrentItem(0, false);
    }

    public void changeBoxFragment(Fragment targetFragment){

    }

    public class MainScreenPagerAdapter extends FragmentStatePagerAdapter {

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
                        walletFragment = WalletFragment.newInstance();
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
    }

}
