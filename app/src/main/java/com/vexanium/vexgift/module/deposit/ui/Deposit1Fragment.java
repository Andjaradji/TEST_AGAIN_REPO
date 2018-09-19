package com.vexanium.vexgift.module.deposit.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.bean.model.DepositOption;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.box.ui.BoxFragment;
import com.vexanium.vexgift.module.box.ui.BoxHistoryFragment;
import com.vexanium.vexgift.module.box.ui.helper.BoxFragmentChangeListener;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenter;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenterImpl;
import com.vexanium.vexgift.module.deposit.ui.adapter.DepositOptionAdapter;
import com.vexanium.vexgift.module.deposit.ui.helper.AdapterOptionOnClick;
import com.vexanium.vexgift.module.deposit.view.IDepositView;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.fragment_deposit_step1)
public class Deposit1Fragment extends BaseFragment<IDepositPresenter> implements IDepositView, AdapterOptionOnClick{

    ImageView mIvHeaderIcon;
    TextView mTvHeaderStep, mTvHeaderTitle;
    RecyclerView mRvDeposit;

    DepositOptionAdapter mDepositOptionAdapter;

    User user;

    public static Deposit1Fragment newInstance() {
        return new Deposit1Fragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        mPresenter = new IDepositPresenterImpl(this);
        user = User.getCurrentUser(getActivity());
        mIvHeaderIcon = fragmentRootView.findViewById(R.id.iv_header_icon);
        mTvHeaderStep = fragmentRootView.findViewById(R.id.tv_step);
        mTvHeaderTitle = fragmentRootView.findViewById(R.id.tv_title);
        mRvDeposit = fragmentRootView.findViewById(R.id.rv_deposit_option);

        mIvHeaderIcon.setImageResource(R.drawable.ic_premium_luckydraw);
        mTvHeaderStep.setText("Step 1/ 3");
        mTvHeaderTitle.setText("Deposit Amount");

        mRvDeposit.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mDepositOptionAdapter = new DepositOptionAdapter(getActivity(),this);
        mRvDeposit.setAdapter(mDepositOptionAdapter);

        mPresenter.requstDepositList(user.getId());

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("Deposit1Fragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof DepositListResponse) {
                DepositListResponse depositListResponse = (DepositListResponse) data;
                setDepositOptionList(depositListResponse);
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(getActivity(), errorResponse);
        }
    }

    @Override
    public void onItemClick(DepositOption depositOption) {

    }

    public void setDepositOptionList(DepositListResponse depositListResponse) {
        mDepositOptionAdapter.removeAll();
        mDepositOptionAdapter.addItemList(depositListResponse.getDeposits().get(0).getDepositOptions());
        mDepositOptionAdapter.notifyDataSetChanged();
    }
}
