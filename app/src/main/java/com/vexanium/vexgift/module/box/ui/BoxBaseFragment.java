package com.vexanium.vexgift.module.box.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.module.box.ui.helper.BoxFragmentChangeListener;

@ActivityFragmentInject(contentViewId = R.layout.fragment_box_base)
public class BoxBaseFragment extends BaseFragment implements BoxFragmentChangeListener {


    private FrameLayout mContainer;

    public static BoxBaseFragment newInstance() {
        return new BoxBaseFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        mContainer = fragmentRootView.findViewById(R.id.fl_container);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(mContainer.getId(), BoxFragment.newInstance()).commit();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("BoxBaseFragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void changeFragment(boolean toHistory) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (toHistory) {
            ft.replace(mContainer.getId(), BoxHistoryFragment.newInstance()).commit();
        } else {
            ft.replace(mContainer.getId(), BoxFragment.newInstance()).commit();
        }
    }

    public void onCustomPause() {
        changeFragment(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //changeFragment(false);
    }

    @Override
    public void onClick(boolean toHistory) {
        changeFragment(toHistory);
    }

    public void changeBoxTab(int page) {
        Fragment currFragment = getCurrentFragment();
        if (currFragment instanceof BoxFragment) {
            ((BoxFragment) currFragment).changeTab(page);
        }
    }

    public Fragment getCurrentFragment() {
        return getChildFragmentManager().findFragmentById(mContainer.getId());
    }


}
