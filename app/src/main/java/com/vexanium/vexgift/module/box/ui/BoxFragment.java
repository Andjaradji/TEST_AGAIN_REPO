package com.vexanium.vexgift.module.box.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.module.wallet.ui.WalletFragment;
import com.vexanium.vexgift.widget.CustomViewPager;
import com.vexanium.vexgift.widget.IconTextTabBarView;

@ActivityFragmentInject(contentViewId = R.layout.fragment_box)
public class BoxFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    private static final int VOUCHER_FRAGMENT = 0;
    private static final int TOKEN_FRAGMENT = 1;
    private static final int PAGE_COUNT = 2;

    private VoucherFragment voucherFragment;
    private TokenFragment tokenFragment;

    private IconTextTabBarView mTabBarView;
    private CustomViewPager mViewPager;


    @Override
    protected void initView(View fragmentRootView) {

        mViewPager = (CustomViewPager) fragmentRootView.findViewById(R.id.vp_box);
        mTabBarView = (IconTextTabBarView) fragmentRootView.findViewById(R.id.ittbv_tabview);

        mTabBarView.addTabView(0, R.drawable.home, R.drawable.home_active,"My Voucher");
        mTabBarView.addTabView(1, R.drawable.box, R.drawable.box_active, "My Token");

        BoxPagerAdapter boxPagerAdapter = new BoxPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(boxPagerAdapter);
        mViewPager.setOffscreenPageLimit(PAGE_COUNT);
        mViewPager.setCurrentItem(0, false);

        mTabBarView.setViewPager(mViewPager);


        setPagerListener();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("NotifFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setPagerListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTabBarView.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                KLog.i("Page: " + position);
                mTabBarView.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mTabBarView.onPageScrollStateChanged(state);
            }
        });
    }

    public class BoxPagerAdapter extends FragmentStatePagerAdapter {

        BoxPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case VOUCHER_FRAGMENT:
                    if (voucherFragment == null) {
                        voucherFragment = VoucherFragment.newInstance();
                    }
                    return voucherFragment;
                case TOKEN_FRAGMENT:
                    if (tokenFragment == null) {
                        tokenFragment = TokenFragment.newInstance();
                    }
                    return tokenFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        }

    public static BoxFragment newInstance() {
        return new BoxFragment();
    }


    }
