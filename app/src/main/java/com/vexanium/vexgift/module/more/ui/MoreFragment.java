package com.vexanium.vexgift.module.more.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.util.ViewUtil;

@ActivityFragmentInject(contentViewId = R.layout.fragment_more)
public class MoreFragment extends BaseFragment {

    @Override
    protected void initView(View fragmentRootView) {
        ViewUtil.setText(fragmentRootView,R.id.tv_toolbar_title,"More");

        App.setTextViewStyle((ViewGroup) fragmentRootView);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("NotifFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static MoreFragment newInstance(){
        return new MoreFragment();
    }
}
