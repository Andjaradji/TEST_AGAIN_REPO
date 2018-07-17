package com.vexanium.vexgift.module.walkthrough.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseFragment;

/**
 * Created by Amang on 16/07/2018.
 */

@ActivityFragmentInject(contentViewId = R.layout.fragment_first)
public class FirstFragment extends BaseFragment {

    TextView mTvWalkthrough;

    public static FirstFragment newInstance() {
        return new FirstFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("FirstFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    protected void initView(View fragmentRootView) {
        mTvWalkthrough = (TextView) fragmentRootView.findViewById(R.id.tv_walkthrough);
        mTvWalkthrough.setText(getActivity().getString(R.string.walkthrough_first));
    }
}
