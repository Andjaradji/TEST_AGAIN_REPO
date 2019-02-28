package com.vexanium.vexgift.module.box.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.module.box.ui.helper.BoxFragmentChangeListener;
import com.vexanium.vexgift.widget.CustomViewPager;
import com.vexanium.vexgift.widget.IconTextTabBarView;

@ActivityFragmentInject(contentViewId = R.layout.fragment_box_history)
public class BoxHistoryFragment extends BaseFragment {

    private static final int VOUCHER_HISTORY_FRAGMENT = 0;
    private static final int TOKEN_HISTORY_FRAGMENT = 1;
    private static final int PAGE_COUNT = 2;

    private VoucherHistoryFragment voucherHistoryFragment;
    private TokenHistoryFragment tokenHistoryFragment;

    private IconTextTabBarView mTabBarView;
    private CustomViewPager mViewPager;
    private ImageButton mBackButton;

    private BoxFragmentChangeListener listener;

    public static BoxHistoryFragment newInstance() {
        return new BoxHistoryFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {

        mViewPager = fragmentRootView.findViewById(R.id.vp_box);
        mTabBarView = fragmentRootView.findViewById(R.id.ittbv_tabview);
        mBackButton = fragmentRootView.findViewById(R.id.ib_back);

        mTabBarView.addTabView(0, R.drawable.box_voucher, getString(R.string.shortcut_my_voucher));
        mTabBarView.addTabView(1, R.drawable.box_token, getString(R.string.shortcut_my_token));

        BoxPagerAdapter boxPagerAdapter = new BoxPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(boxPagerAdapter);
        mViewPager.setOffscreenPageLimit(PAGE_COUNT);
        mViewPager.setCurrentItem(0, false);

        mTabBarView.setViewPager(mViewPager);

        setPagerListener();

        App.setTextViewStyle((ViewGroup) fragmentRootView);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("BoxHistoryFragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() instanceof BoxFragmentChangeListener) {
            listener = (BoxFragmentChangeListener) getParentFragment();
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(false);
            }
        });

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
                case VOUCHER_HISTORY_FRAGMENT:
                    if (voucherHistoryFragment == null) {
                        voucherHistoryFragment = VoucherHistoryFragment.newInstance();
                    }
                    return voucherHistoryFragment;
                case TOKEN_HISTORY_FRAGMENT:
                    if (tokenHistoryFragment == null) {
                        tokenHistoryFragment = TokenHistoryFragment.newInstance();
                    }
                    return tokenHistoryFragment;

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
