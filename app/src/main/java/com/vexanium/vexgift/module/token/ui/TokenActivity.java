package com.vexanium.vexgift.module.token.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.SortFilterCondition;
import com.vexanium.vexgift.bean.model.Voucher;
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

import static com.vexanium.vexgift.app.StaticGroup.goToVoucherDetailActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_voucher)
public class TokenActivity extends BaseActivity {

    private ArrayList<Voucher> data;
    GridLayoutManager layoutListManager;

    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead,mTvErrorBody;

    SwipeRefreshLayout mRefreshLayout;
    RecyclerView mRecyclerview;
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
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl_refresh);
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
//        data = FixtureData.tokenAirdropTokenResponse();
//        data.addAll(data);
        //setVoucherList(data);

        data = new ArrayList<>();
        setVoucherList(data);

        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.token_open_filter_button).setOnClickListener(this);

        ((ImageView)findViewById(R.id.iv_toolbar_logo)).setImageResource(R.drawable.ic_coin);
        ((TextView)findViewById(R.id.tv_toolbar_title)).setText(getString(R.string.token_title));

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
                if(newState.name().equalsIgnoreCase("Expanded")){
                    //action when expanded
                    mPanelScrollview.setScrollingEnabled(true);
                }else{
                    mPanelScrollview.setScrollingEnabled(false);
                }

            }
        });

        mPanelScrollview.setScrollingEnabled(false);

        setFilterItem(R.id.tg_member, R.id.iv_filter_member_add_more, R.id.filter_member, "member");
        setFilterItem(R.id.tg_payment, R.id.iv_filter_payment_add_more, R.id.filter_payment, "payment");
        setFilterItem(R.id.tg_location, R.id.iv_filter_location_add_more, R.id.filter_location, "location");

        mSlidePanel.getChildAt(1).setOnClickListener(null);

        mSlidePanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        // Setup refresh listener which triggers new data loading
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //fetchTimelineAsync(0);
                updateData();//update data here

            }
        });

        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mRefreshLayout.setEnabled(layoutListManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;
            case R.id.token_open_filter_button:
                mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                break;
        }
    }

    private void updateData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        },3000);
    }

    private void setFilterItem(@IdRes int tagview, @IdRes int addButton, @IdRes int rootView, final String listType) {
        findViewById(rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                Intent intent = new Intent(TokenActivity.this, MultiSelectActivity.class);
                intent.putExtra("type", listType);
                intent.putExtra("condition", JsonUtil.toString(sortFilterCondition));
                startActivityForResult(intent, ConstantGroup.EDIT_FILTER);
            }
        });

        TagView tagView = findViewById(tagview);
        tagView.removeAll();

        List<String> selectedItems = new ArrayList<>();

        if (listType.equalsIgnoreCase("member")) {
            selectedItems = sortFilterCondition.getMemberType();
        } else if (listType.equalsIgnoreCase("location")) {
            selectedItems = sortFilterCondition.getLocation();
        } else if (listType.equalsIgnoreCase("payment")) {
            selectedItems = sortFilterCondition.getPaymentType();
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
                holder.setText(R.id.tv_coupon_exp, item.getExpiredDate());
                holder.setBackground(R.id.ll_qty, item.getPrice() == 0 ? R.drawable.shape_price_free_bg : R.drawable.shape_price_bg);

                if (item.getQtyAvailable() == 0) {
                    holder.setText(R.id.tv_price, "Out of stock");
                    holder.setBackgroundColor(R.id.ll_qty, R.color.material_black);
                } else
                    holder.setText(R.id.tv_price, item.getPrice() == 0 ? "Free" : item.getPrice() + " VP");
//                    holder.setText(R.id.tv_price, String.format("%s/%s", item.getQtyAvailable() + "", item.getQtyTotal() + ""));
//                holder.setImageUrl(R.id.iv_brand_image, item..getBrand().getPhoto(), R.drawable.placeholder);
                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        goToVoucherDetailActivity(TokenActivity.this, item, holder.getImageView(R.id.iv_coupon_image));
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
            mIvError.setImageResource(R.drawable.token_empty);
            mTvErrorHead.setText(getString(R.string.error_token_airdrop_empty_header));
            mTvErrorBody.setText(getString(R.string.error_token_empty_body));

            mRecyclerview.setVisibility(View.GONE);
            findViewById(R.id.token_open_filter_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(mSlidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            super.onBackPressed();
        }else{
            mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }
}
