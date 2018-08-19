package com.vexanium.vexgift.module.premium.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.PremiumPurchase;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.PremiumHistoryResponse;
import com.vexanium.vexgift.module.premium.presenter.IPremiumPresenter;
import com.vexanium.vexgift.module.premium.presenter.IPremiumPresenterImpl;
import com.vexanium.vexgift.module.premium.ui.adapter.PremiumHistoryAdapter;
import com.vexanium.vexgift.module.premium.ui.helper.AdapterHistoryOnClick;
import com.vexanium.vexgift.module.profile.view.IProfileView;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_premium_history, toolbarTitle = R.string.premium_history)
public class PremiumHistoryActivity extends BaseActivity<IPremiumPresenter> implements IProfileView, AdapterHistoryOnClick {


    User user;
    RecyclerView mRvPurchaseHistory;
    PremiumHistoryAdapter mAdapterHistory;
    NestedScrollView mScrollView;

    ArrayList<PremiumPurchase> mPremiumHistoryList = new ArrayList<>();

    @Override
    protected void initView() {
        mPresenter = new IPremiumPresenterImpl(this);
        user = User.getCurrentUser(this);

        mScrollView = findViewById(R.id.sv_purchase_history);
        mRvPurchaseHistory = findViewById(R.id.rv_purchase_history);
        mRvPurchaseHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvPurchaseHistory.setNestedScrollingEnabled(false);

        mRvPurchaseHistory.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mAdapterHistory = new PremiumHistoryAdapter(this, this);
        mRvPurchaseHistory.setAdapter(mAdapterHistory);

        mPremiumHistoryList = (ArrayList<PremiumPurchase>)getIntent().getSerializableExtra("premium_history_list");
        updateView(mPremiumHistoryList);
        //mPresenter.requestUserPremiumHistory(user.getId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
        if (data != null) {
            if (data instanceof PremiumHistoryResponse) {
                PremiumHistoryResponse premiumListResponse = (PremiumHistoryResponse) data;
                updateView(premiumListResponse.getPremiumPurchase());
            }

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
        }
    }


    @Override
    public void onItemClick(PremiumPurchase premiumPurchase) {
        Intent intent = new Intent(this,PremiumHistoryDetailActivity.class);
        intent.putExtra("premium_history_detail",premiumPurchase);
        startActivity(intent);
    }

    private void updateView(ArrayList<PremiumPurchase> dataList){
        mAdapterHistory.addItemList(dataList);
        mAdapterHistory.notifyDataSetChanged();
        if(mAdapterHistory.getItemCount() > 0 ){
            if(mRvPurchaseHistory.getVisibility()!=View.VISIBLE) {
                mRvPurchaseHistory.setVisibility(View.VISIBLE);
            }
        }else{
            if(mRvPurchaseHistory.getVisibility()!=View.GONE) {
                mRvPurchaseHistory.setVisibility(View.GONE);
            }
        }
    }
}
