package com.vexanium.vexgift.module.box.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseFragment;

/**
 * Created by Amang on 16/07/2018.
 */

@ActivityFragmentInject(contentViewId = R.layout.fragment_third)
public class TokenFragment extends BaseFragment {

    public static TokenFragment newInstance() {
        return new TokenFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("FourthFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View fragmentRootView) {

    }
}
