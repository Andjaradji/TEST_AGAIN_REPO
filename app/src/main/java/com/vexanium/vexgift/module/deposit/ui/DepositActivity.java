package com.vexanium.vexgift.module.deposit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.Deposit;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenter;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenterImpl;
import com.vexanium.vexgift.module.deposit.view.IDepositView;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_deposit, withLoadingAnim = true, toolbarTitle = R.string.deposit_title)
public class DepositActivity extends BaseActivity<IDepositPresenter> implements IDepositView {

    TextView mTvDepositTitle, mTvDepositDate, mTvDepositDesc, mTvDepositDuration, mTvDepositTotal;
    LinearLayout mBtnDeposit;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IDepositPresenterImpl(this);
        user = User.getCurrentUser(this);

        mTvDepositTitle = findViewById(R.id.tv_deposit_title);
        mTvDepositDate = findViewById(R.id.tv_deposit_date);
        mTvDepositDesc = findViewById(R.id.tv_desc);
        mTvDepositDuration = findViewById(R.id.tv_duration);
        mTvDepositTotal = findViewById(R.id.tv_total);

        mBtnDeposit = findViewById(R.id.btn_deposit);

        mPresenter.requstDepositList(user.getId());
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof DepositListResponse) {
                DepositListResponse depositListResponse = (DepositListResponse) data;
                Deposit item = depositListResponse.getDeposits().get(0);
                mTvDepositTitle.setText("[" + item.getCoinType() + "] " + item.getName());
                mTvDepositDate.setText(item.getStartTime() + " - " + item.getEndTime());
                mTvDepositDesc.setText(item.getDescription());
                mTvDepositDuration.setText(item.getDuration()+"");
                mTvDepositTotal.setText(item.getCoinDeposited() + " " + item.getCoinType());

                mBtnDeposit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DepositActivity.this, DepositListActivity.class);
                        startActivity(intent);
                    }
                });
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }
}
