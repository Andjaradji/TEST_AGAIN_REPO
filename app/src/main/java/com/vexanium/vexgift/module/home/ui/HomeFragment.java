package com.vexanium.vexgift.module.home.ui;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.VoucherResponse;
import com.vexanium.vexgift.module.home.presenter.IHomePresenter;
import com.vexanium.vexgift.module.home.view.IHomeView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static com.vexanium.vexgift.app.StaticGroup.HOT_COUPON;
import static com.vexanium.vexgift.app.StaticGroup.NORMAL_COUPON;

@ActivityFragmentInject(contentViewId = R.layout.fragment_home)
public class HomeFragment extends BaseFragment<IHomePresenter> implements IHomeView{

    private Toolbar toolbar;
    private GridLayoutManager layoutListManager;
    private RecyclerView mRecyclerview;
    private BaseRecyclerAdapter<VoucherResponse> mAdapter;
    private ArrayList<VoucherResponse> data;
    private Random random;

    private ArrayList<VoucherResponse> hotVoucherList;
    private BaseRecyclerAdapter<VoucherResponse> mHotAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("NotifFragment onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

    }

    public void loadData() {
        data = FixtureData.getRandomVoucherResponse(random.nextInt(3) + 2, true);
//        hotVoucherList = FixtureData.getRandomVoucherResponse(5, true);
//        brandList = FixtureData.getRandomBrand(random.nextInt(4) + 4);
        hotVoucherList = FixtureData.showCaseVoucherResponse;
        data.add(0, new VoucherResponse(HOT_COUPON));
    }

    public void initHomeList() {
        mAdapter = new BaseRecyclerAdapter<VoucherResponse>(getActivity(), data, layoutListManager) {
            @Override
            public int getItemViewType(int position) {
                return data.get(position).type;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                switch (viewType) {
                    case HOT_COUPON:
                        return R.layout.item_hot_coupon_pager;
                    case NORMAL_COUPON:
                    default:
                        return R.layout.item_coupon_list;
                }
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final VoucherResponse item) {
                switch (getItemViewType(position)) {
                    case HOT_COUPON:
                        setHotVoucherList(holder, hotVoucherList);
                        break;
                    case NORMAL_COUPON:
                    default:
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
                        break;
                }
            }
        };
        mAdapter.setHasStableIds(true);
        mRecyclerview.setLayoutManager(layoutListManager);
        mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this.getActivity(), 16)));
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

    public void setHotVoucherList(BaseRecyclerViewHolder holder, ArrayList<VoucherResponse> couponList) {
        DiscreteScrollView discreteScrollView = (DiscreteScrollView) holder.getView(R.id.scrollView);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        mHotAdapter = new BaseRecyclerAdapter<VoucherResponse>(getActivity(), couponList, layoutManager) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_hot_list;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final VoucherResponse item) {
                holder.setImageUrl(R.id.iv_coupon_image, item.getVoucher().getPhoto(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_title, item.getVoucher().getTitle());
                holder.setText(R.id.tv_coupon_exp, item.getVoucher().getExpiredDate());
                if (item.getAvail() == 0)
                    holder.setText(R.id.tv_banner_quota, "Out of stock");
                else
                    holder.setText(R.id.tv_banner_quota, String.format("%s/%s", item.getAvail() + "", item.getStock() + ""));
//                holder.setImageUrl(R.id.iv_brand_image, item.getVoucher().getBrand().getPhoto(), R.drawable.placeholder);
                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtil.isFastDoubleClick()) return;
//                        goToVoucherDetailActivity(item);
                    }
                });
            }
        };
        discreteScrollView.setAdapter(mHotAdapter);
        discreteScrollView.setItemViewCacheSize(30);
        discreteScrollView.setDrawingCacheEnabled(true);
        discreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
//        discreteScrollView.setSlideOnFling(true);
        discreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.CENTER)
                .build()
        );
        discreteScrollView.scrollToPosition(1);
    }

}
