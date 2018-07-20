package com.vexanium.vexgift.module.more.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.module.profile.ui.MyProfileActivity;
import com.vexanium.vexgift.util.ClickUtil;
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

    @Override
    public void onClick(View v) {
        if(ClickUtil.isFastDoubleClick()){
            return;
        }
        super.onClick(v);
        switch (v.getId()){
            case R.id.more_myprofile_button:
                Intent intent = new Intent(MoreFragment.this.getActivity(), MyProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.more_setting_button:
                break;
            case R.id.more_change_password_button:
                break;
            case R.id.more_merchant_button:
                break;
            case R.id.more_feedback_buttton:
                break;
            case R.id.more_problem_button:
                break;
            case R.id.more_gp_button:
                break;
            case R.id.more_about_button:
                break;
            case R.id.more_privacy_policy:
                break;
            case R.id.more_logout_button:
                break;
        }
    }

    public static MoreFragment newInstance(){
        return new MoreFragment();
    }


}
