package com.vexanium.vexgift.module.wallet.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.fixture.WalletToken;
import com.vexanium.vexgift.bean.fixture.WalletTokenRecord;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_wallet_detail, toolbarTitle = R.string.my_wallet_title, withLoadingAnim = false)
public class WalletDetailActivity extends BaseActivity {

    RecyclerView mRecyclerview;
    GridLayoutManager layoutListManager;
    BaseRecyclerAdapter<WalletTokenRecord> mAdapter;
    ArrayList<WalletTokenRecord> walletTokenRecords;
    WalletToken walletToken;

    ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {



        ivIcon = findViewById(R.id.iv_icon);;
        mRecyclerview = findViewById(R.id.rv_wallet_record);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);
        mRecyclerview.setLayoutManager(layoutListManager);

        if (getIntent().hasExtra("wallet_token")) {
            walletToken = (WalletToken)getIntent().getSerializableExtra("wallet_token");

            setWalletRecordList();
        }



    }

    public void setWalletRecordList() {

        DecimalFormat df = new DecimalFormat("#,###.##");
        ViewUtil.setText(this, R.id.tv_vex_amount, df.format(walletToken.getAmount())+ " "+walletToken.getName());
        ViewUtil.setText(this, R.id.tv_vex_idr, df.format(walletToken.getAmount()*walletToken.getEstPriceInIDR())+" IDR");
        ivIcon.setImageResource(walletToken.getResIcon());

        walletTokenRecords = new ArrayList<>();
        for(WalletTokenRecord walletTokenRecord : FixtureData.tokenRecordList){
            if(walletTokenRecord.getTokenId() == walletToken.getId()){
                walletTokenRecords.add(walletTokenRecord);
            }
        }

        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<WalletTokenRecord>(this, walletTokenRecords, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_wallet_record_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, final int position, final WalletTokenRecord item) {
                    holder.setText(R.id.tv_wallet_record_title,item.getTitle());
                    holder.setText(R.id.tv_wallet_record_subtitle,item.getDate());

                    if (item.getType() == WalletTokenRecord.RECEIVE) {
                        holder.setImageResource(R.id.iv_wallet_record_type,R.drawable.record_receive);
                        holder.getImageView(R.id.iv_wallet_record_type).setRotation(90);
                        holder.setText(R.id.tv_wallet_record_vex,"+ " + item.getAmount() + " "+walletToken.getName());
                    } else {
                        holder.setImageResource(R.id.iv_wallet_record_type,R.drawable.record_send);
                        holder.getImageView(R.id.iv_wallet_record_type).setRotation(-90);
                        holder.setText(R.id.tv_wallet_record_vex,"- " + item.getAmount() + " "+walletToken.getName());
                    }


                }
            };
            mAdapter.setHasStableIds(true);
            mRecyclerview.setLayoutManager(layoutListManager);
            mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this, 16)));
            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
            if (mRecyclerview.getItemAnimator() != null)
                mRecyclerview.getItemAnimator().setAddDuration(250);
            mRecyclerview.getItemAnimator().setMoveDuration(250);
            mRecyclerview.getItemAnimator().setChangeDuration(250);
            mRecyclerview.getItemAnimator().setRemoveDuration(250);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setItemViewCacheSize(30);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    App.setTextViewStyle(mRecyclerview);
                }
            });
        } else {
            mAdapter.setData(walletTokenRecords);
        }

//        if (data.size() <= 0) {
//            mErrorView.setVisibility(View.VISIBLE);
//            mIvError.setImageResource(R.drawable.voucher_empty);
//            mTvErrorHead.setText(getString(R.string.error_voucher_empty_header));
//            mTvErrorBody.setText(getString(R.string.error_my_voucher_empty_body));
//
//            mRecyclerview.setVisibility(View.GONE);
//        } else {
//            mErrorView.setVisibility(View.GONE);
//            mRecyclerview.setVisibility(View.VISIBLE);
//
//        }
    }


}
