package com.vexanium.vexgift.module.token.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.response.HomeFeedResponse;
import com.vexanium.vexgift.bean.response.VoucherResponse;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.MeasureUtil;

import java.util.ArrayList;
import java.util.Random;

@ActivityFragmentInject(contentViewId = R.layout.activity_token)
public class TokenActivity extends BaseActivity {

    private ArrayList<VoucherResponse> data;
    GridLayoutManager layoutListManager;
    RecyclerView mRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mRecyclerview = findViewById(R.id.recylerview);
        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        Random random = new Random();
        data = FixtureData.getRandomVoucherResponse(random.nextInt(3) + 2, true);
        setVoucherList(data);

        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;
        }
    }

    public void setVoucherList(final ArrayList<VoucherResponse> data) {

        BaseRecyclerAdapter<VoucherResponse> mAdapter = new BaseRecyclerAdapter<VoucherResponse>(this, data, layoutListManager) {
            @Override
            public int getItemViewType(int position) {
                return data.get(position).type;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_coupon_list;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final VoucherResponse item) {
                holder.setImageUrl(R.id.iv_coupon_image, item.getVoucher().getPhoto(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_title, item.getVoucher().getTitle());
                if (item.getAvail() == 0)
                    holder.setText(R.id.tv_banner_quota, "Out of stock");
                else
                    holder.setText(R.id.tv_banner_quota, String.format("%s/%s", item.getAvail() + "", item.getStock() + ""));
//                        holder.setImageUrl(R.id.iv_brand_image, item.getVoucher().getBrand().getPhoto(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_exp, item.getVoucher().getExpiredDate());
                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtil.isFastDoubleClick()) return;
//                                goToVoucherDetailActivity(item);
                    }
                });

            }
        };
        mAdapter.setHasStableIds(true);
        mRecyclerview.setLayoutManager(layoutListManager);
        mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this, 16)));
        mRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mRecyclerview.getItemAnimator().setAddDuration(250);
        mRecyclerview.getItemAnimator().setMoveDuration(250);
        mRecyclerview.getItemAnimator().setChangeDuration(250);
        mRecyclerview.getItemAnimator().setRemoveDuration(250);
        mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerview.setItemViewCacheSize(30);
        mRecyclerview.setDrawingCacheEnabled(true);
        mRecyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                App.setTextViewStyle(mRecyclerview);
            }
        });
    }
}
