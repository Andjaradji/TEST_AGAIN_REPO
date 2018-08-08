package com.vexanium.vexgift.module.voucher.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.SortFilterCondition;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.VoucherResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.voucher.ui.adapter.FilterAdapter;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.widget.LockableScrollView;
import com.vexanium.vexgift.widget.select.MultiSelectActivity;
import com.vexanium.vexgift.widget.tag.Tag;
import com.vexanium.vexgift.widget.tag.TagView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ActivityFragmentInject(contentViewId = R.layout.activity_voucher)
public class VoucherActivity extends BaseActivity {
    private ArrayList<Voucher> data;
    GridLayoutManager layoutListManager;

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead,mTvErrorBody;

    RecyclerView mRecyclerview, mRvCategory, mRvLocation;
    FilterAdapter mAdapterCategory, mAdapterLocation;
    SlidingUpPanelLayout mSlidePanel;
    LinearLayout mDragView;
    LockableScrollView mPanelScrollview;

    SortFilterCondition sortFilterCondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mSlidePanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mPanelScrollview = (LockableScrollView) findViewById(R.id.voucher_scrollview);
        mDragView = findViewById(R.id.dragview);
        mRecyclerview = findViewById(R.id.recylerview);

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);

        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        sortFilterCondition = new SortFilterCondition();
        Random random = new Random();
//        data = FixtureData.giftVoucherVoucherResponse();

//        data.addAll(data);
        //setVoucherList(data);

        data = TableContentDaoUtil.getInstance().getVouchers() ;
        if(data==null) data = new ArrayList<>();
        setVoucherList(data);

        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.token_open_filter_button).setOnClickListener(this);

        ((ImageView)findViewById(R.id.iv_toolbar_logo)).setImageResource(R.drawable.ic_gift);
        ((TextView)findViewById(R.id.tv_toolbar_title)).setText(getString(R.string.voucher_title));

        mSlidePanel.setAnchorPoint(0.6f);
        mSlidePanel.setOverlayed(true);
        mSlidePanel.setShadowHeight(0);
        mSlidePanel.setDragView(mDragView);
        mSlidePanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                /*//  Toast.makeText(getApplicationContext(),newState.name().toString(),Toast.LENGTH_SHORT).show();
                if(newState.name().toString().equalsIgnoreCase("Collapsed")){

                    //action when collapsed

                }else if(newState.name().equalsIgnoreCase("Expanded")){


                }
*/
                if (newState.name().equalsIgnoreCase("Expanded")) {

                    //action when expanded
                    mPanelScrollview.setScrollingEnabled(true);
                } else {
                    mPanelScrollview.setScrollingEnabled(false);
                }

            }
        });

        mPanelScrollview.setScrollingEnabled(false);

        setFilterItem(R.id.tg_category, R.id.iv_filter_category_add_more, "category");
        setFilterItem(R.id.tg_type, R.id.iv_filter_type_add_more, "type");
        setFilterItem(R.id.tg_location, R.id.iv_filter_location_add_more, "location");

        mSlidePanel.getChildAt(1).setOnClickListener(null);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.token_open_filter_button:
                mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                break;
        }
    }

    private void setFilterItem(@IdRes int tagview, @IdRes int addButton, final String listType) {
        findViewById(addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                Intent intent = new Intent(VoucherActivity.this, MultiSelectActivity.class);
                intent.putExtra("type", listType);
                intent.putExtra("condition", JsonUtil.toString(sortFilterCondition));
                startActivityForResult(intent, ConstantGroup.EDIT_FILTER);
            }
        });

        TagView tagView = findViewById(tagview);
        tagView.removeAll();

        List<String> selectedItems = new ArrayList<>();

        if (listType.equalsIgnoreCase("category")) {
            selectedItems = sortFilterCondition.getCategory();
        } else if (listType.equalsIgnoreCase("location")) {
            selectedItems = sortFilterCondition.getLocation();
        } else if (listType.equalsIgnoreCase("type")) {
            selectedItems = sortFilterCondition.getType();
        }

        List<Tag> addedTagList = new ArrayList<>();
        for (String item : selectedItems) {
            String itemName = item;
            Tag tag = new Tag(itemName);
            addedTagList.add(tag);
        }

        tagView.addTags(addedTagList);
    }

    public void setVoucherList(final ArrayList<Voucher> data) {

        BaseRecyclerAdapter<Voucher> mAdapter = new BaseRecyclerAdapter<Voucher>(this, data, layoutListManager) {

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_coupon_list;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final Voucher item) {
                holder.setImageUrl(R.id.iv_coupon_image, item.getThumbnail(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_title, item.getTitle());
                if (item.getQtyAvailable() == 0)
                    holder.setText(R.id.tv_banner_quota, "Out of stock");
                else
                    holder.setText(R.id.tv_banner_quota, String.format("%s/%s", item.getQtyAvailable() + "", item.getQtyTotal() + ""));
//                        holder.setImageUrl(R.id.iv_brand_image, item.getVoucher().getBrand().getPhoto(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_exp, item.getExpiredDate());
                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        StaticGroup.goToVoucherDetailActivity(VoucherActivity.this, item, holder.getImageView(R.id.iv_coupon_image));
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

        if(data.size() <= 0){
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.error_voucher_gift_empty_header));
            mTvErrorBody.setText(getString(R.string.error_voucher_empty_body));

            mRecyclerview.setVisibility(View.GONE);
            findViewById(R.id.token_open_filter_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            super.onBackPressed();
        } else {
            mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantGroup.EDIT_FILTER) {
            if(data.hasExtra("condition")){
                String condition = data.getStringExtra("condition");
                sortFilterCondition = (SortFilterCondition) JsonUtil.toObject(condition, SortFilterCondition.class);
            }
            switch (resultCode) {
                case ConstantGroup.EDIT_FILTER_CATEGORY_RESULT_CODE:
                    break;
                case ConstantGroup.EDIT_FILTER_TYPE_RESULT_CODE:
                    break;
                case ConstantGroup.EDIT_FILTER_LOCATION_RESULT_CODE:
                    break;
            }
            setFilterItem(R.id.tg_category, R.id.iv_filter_category_add_more, "category");
            setFilterItem(R.id.tg_type, R.id.iv_filter_type_add_more, "type");
            setFilterItem(R.id.tg_location, R.id.iv_filter_location_add_more, "location");
        }
    }
}
