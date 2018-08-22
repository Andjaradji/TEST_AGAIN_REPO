package com.vexanium.vexgift.module.main.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.module.box.ui.BoxBaseFragment;
import com.vexanium.vexgift.module.box.ui.BoxHistoryFragment;
import com.vexanium.vexgift.module.home.ui.HomeFragment;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.notif.ui.NotifFragment;
import com.vexanium.vexgift.module.voucher.ui.VoucherActivity;
import com.vexanium.vexgift.module.wallet.ui.WalletFragment;
import com.vexanium.vexgift.widget.CustomTabBarView;
import com.vexanium.vexgift.widget.CustomViewPager;

@ActivityFragmentInject(contentViewId = R.layout.activity_main)
public class MainActivity extends BaseActivity {

    public static final int HOME_FRAGMENT = 0;
    public static final int BOX_FRAGMENT = 1;
    //public static final int WALLET_FRAGMENT = 2;
    public static final int NOTIF_FRAGMENT = 2;
    public static final int MORE_FRAGMENT = 3;
    public static final int PAGE_COUNT = 4;
    private CustomTabBarView mCustomTabBarView;
    private CustomViewPager mCustomViewPager;
    private HomeFragment homeFragment;
    private BoxBaseFragment boxBaseFragment;
    private WalletFragment walletFragment;
    private NotifFragment notifFragment;
    private MoreFragment moreFragment;
    private MainScreenPagerAdapter mainScreenPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(User.getCurrentUser(this) == null){
//            StaticGroup.logOutClear(this,0);
//        }else {
        super.onCreate(savedInstanceState);

        setToolbar();
        setCustomTabs();
        setPagerListener();

        if (getIntent().hasExtra("url")) {
            String url = getIntent().getStringExtra("url");
            getIntent().removeExtra("url");
            openDeepLink(url);
        }
        handlePushAction();
//        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra("url")) {
            String url = getIntent().getStringExtra("url");
            getIntent().removeExtra("url");
            openDeepLink(url);
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
        //mCustomTabBarView.addTabView(2, R.drawable.wallet, R.drawable.wallet_active);
        mCustomTabBarView.addTabView(2, R.drawable.notif, R.drawable.notif_active);
        mCustomTabBarView.addTabView(3, R.drawable.more, R.drawable.more_active);

        setFragmentToolbar(0);

    }

    public void setFragmentToolbar(int fragment) {
        switch (fragment) {
            case HOME_FRAGMENT:
                break;
            case BOX_FRAGMENT:
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
        mCustomViewPager.setCurrentItem(0, false);
    }

    private void handlePushAction() {
        String url = getIntent().getStringExtra("t_url");
        if (!TextUtils.isEmpty(url)) {
            openDeepLink(url);
        }
    }

    public void openDeepLink(String url) {
        KLog.v("MainActivity", "openDeepLink: " + url);

        // TODO: 09/08/18 handle Deeplink
        boolean isAlreadyHandled = StaticGroup.handleUrl(this, url);
        Intent intent;
        if (!isAlreadyHandled) {
            Uri uri = Uri.parse(url);
            String path = url.replace("http://www.vexgift.com/", "")
                    .replace("https://www.vexgift.com/", "")
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

            } else if (path.startsWith("receive")) {
                String code = uri.getQueryParameter("c");
                intent = new Intent(this, VoucherActivity.class);
                intent.putExtra("code", code);
                startActivity(intent);

            } else if (path.startsWith("box")) {
                gotoPage(BOX_FRAGMENT);

            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                StaticGroup.openAndroidBrowser(this, url);
            }
        }
    }

    public void gotoPage(int page, int secondaryPage) {
        mCustomViewPager.setCurrentItem(page, false);

        //for boxframent
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
                /*case WALLET_FRAGMENT:
                    if (walletFragment == null) {
                        walletFragment = WalletFragment.newInstance();
                    }
                    return walletFragment;*/
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
