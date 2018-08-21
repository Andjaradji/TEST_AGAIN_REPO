package com.vexanium.vexgift.module.box.ui;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.module.box.ui.helper.BoxFragmentChangeListener;
import com.vexanium.vexgift.module.voucher.ui.ReceiveVoucherActivity;
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

    private ImageButton mReceiveButton;
    private ImageButton mHistoryButton;
    private RelativeLayout mNotifBar;
    private TextView mNotifText;
    private TextView mNotifSeeMore;

    private BoxFragmentChangeListener listener;


    @Override
    protected void initView(View fragmentRootView) {

        mHistoryButton =  fragmentRootView.findViewById(R.id.ib_history);
        mReceiveButton =  fragmentRootView.findViewById(R.id.ib_receive);

        mViewPager = fragmentRootView.findViewById(R.id.vp_box);
        mTabBarView = fragmentRootView.findViewById(R.id.ittbv_tabview);

        mNotifBar = fragmentRootView.findViewById(R.id.rl_notif_info);
        mNotifText = fragmentRootView.findViewById(R.id.tv_notif_info);
        mNotifSeeMore = fragmentRootView.findViewById(R.id.tv_notif_see_all);

        mTabBarView.addTabView(0, R.drawable.box_voucher, "My Voucher");
        mTabBarView.addTabView(1, R.drawable.box_token, "My Token");

        BoxPagerAdapter boxPagerAdapter = new BoxPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setPagingEnabled(true);
        mViewPager.setAdapter(boxPagerAdapter);
        mViewPager.setOffscreenPageLimit(PAGE_COUNT);
        mViewPager.setCurrentItem(0, false);

        mTabBarView.setViewPager(mViewPager);

        setPagerListener();

        App.setTextViewStyle((ViewGroup)fragmentRootView);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("BoxFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getParentFragment() instanceof BoxFragmentChangeListener){
            listener = (BoxFragmentChangeListener)getParentFragment();
        }

        mHistoryButton.setOnClickListener(this);
        mReceiveButton.setOnClickListener(this);
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
//                if(position == 0){
//                    changeNotifBarVisibility(true,"You Have 10 Expired and 8 Used Voucher");
//                }else{
//                    changeNotifBarVisibility(false,"You Have 10 Expired and 8 Used Voucher");
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mTabBarView.onPageScrollStateChanged(state);
            }
        });
    }

    private void changeNotifBarVisibility(boolean isVisible, String text){
        mNotifText.setText(text);
        /*if(isVisible){
            mNotifBar.setVisibility(View.VISIBLE);
        }else{
            mNotifBar.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.ib_history:
                if(listener!=null) {
                    listener.onClick(true);
                }
                break;
            case R.id.ib_receive:
                Intent intent = new Intent(getActivity(), ReceiveVoucherActivity.class);
                startActivity(intent);

        }
    }

    public void changeTab(int page){
        mViewPager.setCurrentItem(page,false);
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