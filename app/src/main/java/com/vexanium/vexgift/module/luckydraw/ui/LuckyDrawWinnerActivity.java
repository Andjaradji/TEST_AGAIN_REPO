package com.vexanium.vexgift.module.luckydraw.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.vexanium.vexgift.bean.model.LuckyDraw;
import com.vexanium.vexgift.bean.model.SortFilterCondition;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.LuckyDrawListResponse;
import com.vexanium.vexgift.bean.response.UserLuckyDrawListResponse;
import com.vexanium.vexgift.module.luckydraw.helper.WinnerCoverAsync;
import com.vexanium.vexgift.module.luckydraw.presenter.ILuckyDrawPresenter;
import com.vexanium.vexgift.module.luckydraw.presenter.ILuckyDrawPresenterImpl;
import com.vexanium.vexgift.module.luckydraw.view.ILuckyDrawView;
import com.vexanium.vexgift.module.voucher.ui.select.MultiSelectActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.widget.LockableScrollView;
import com.vexanium.vexgift.widget.tag.Tag;
import com.vexanium.vexgift.widget.tag.TagView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ActivityFragmentInject(contentViewId = R.layout.activity_luckydraw_winner)
public class LuckyDrawWinnerActivity extends BaseActivity<ILuckyDrawPresenter> implements ILuckyDrawView {
    GridLayoutManager layoutListManager;
    LinearLayout mErrorView;
    ImageView mIvError;
    TextView mTvErrorHead, mTvErrorBody;
    //AVLoadingIndicatorView mAvi;
    RelativeLayout mAviContainer;
    SwipeRefreshLayout mRefreshLayout;
    RecyclerView mRecyclerview;
    SlidingUpPanelLayout mSlidePanel;
    LinearLayout mDragView;
    LockableScrollView mPanelScrollview;
    Spinner spDocType;
    SortFilterCondition sortFilterCondition;
    BaseRecyclerAdapter<LuckyDraw> mAdapter;
    boolean isSortFilterUpdate = false;
    User user;

    private ArrayList<LuckyDraw> completedLuckyDraws;
    private LoadLuckyDrawAsync loadLuckyDrawAsync;
    private Animation mFadeIn, mFadeOut;

    private boolean isScrolledTop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void initView() {
//        ViewUtil.findViewById(this,R.id.rl_luckydraw_toolbar).setVisibility(View.GONE);
//        ViewUtil.findViewById(this,R.id.rl_luckydraw_winner_toolbar).setVisibility(View.VISIBLE);

        mPresenter = new ILuckyDrawPresenterImpl(this);
        user = User.getCurrentUser(this);

//        if (getIntent().hasExtra("code")) {
//            Intent intent = new Intent(this, ReceiveVoucherActivity.class);
//            intent.putExtra("code", getIntent().getStringExtra("code"));
//            startActivity(intent);
//        } else if (getIntent().hasExtra("id")) {
//            int id = getIntent().getIntExtra("id", 0);
//            ArrayList<Voucher> luckyDraws = TableContentDaoUtil.getInstance().getVouchers();
//            Voucher voucher = StaticGroup.getVoucherById(luckyDraws, id);
//            if (voucher != null)
//                StaticGroup.goToVoucherDetailActivity(this, voucher);
//        }

        mAviContainer = findViewById(R.id.av_indicator_container);
        mRefreshLayout = findViewById(R.id.srl_refresh);
        mSlidePanel = findViewById(R.id.sliding_layout);
        mPanelScrollview = findViewById(R.id.voucher_scrollview);
        mDragView = findViewById(R.id.dragview);
        mRecyclerview = findViewById(R.id.recylerview);

        mErrorView = findViewById(R.id.ll_error_view);
        mIvError = findViewById(R.id.iv_error_view);
        mTvErrorHead = findViewById(R.id.tv_error_head);
        mTvErrorBody = findViewById(R.id.tv_error_body);

        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        sortFilterCondition = new SortFilterCondition();

        mFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_anim);
        mFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_anim);
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAviContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        loadLuckyDrawAsync = new LoadLuckyDrawAsync();

        if (getIntent().hasExtra("luckyDraw_winner")) {
            String luckyDrawString = getIntent().getStringExtra("luckyDraw_winner");
            LuckyDrawListResponse luckyDrawResponse = (LuckyDrawListResponse) JsonUtil.toObject(luckyDrawString, LuckyDrawListResponse.class);
            if (luckyDrawResponse != null) {
                completedLuckyDraws = luckyDrawResponse.getLuckyDraws();
            }
        }
        //luckyDraws = TableContentDaoUtil.getInstance().getVouchers();
        if (completedLuckyDraws == null) completedLuckyDraws = new ArrayList<>();
        setLuckyDrawList(completedLuckyDraws);

        updateData();

        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.token_open_filter_button).setVisibility(View.GONE);
        findViewById(R.id.iv_winner).setOnClickListener(this);

//        ((ImageView) findViewById(R.id.iv_toolbar_logo)).setImageResource(isToken ? R.drawable.ic_coin : R.drawable.ic_gift);
//        ((TextView) findViewById(R.id.tv_toolbar_title)).setText(getString(isToken ? R.string.token_title : R.string.voucher_title));

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

                if (newState.name().equalsIgnoreCase("EXPANDED")) {

                    //action when expanded
                    mPanelScrollview.setScrollingEnabled(true);
                } else if (newState.name().equalsIgnoreCase("COLLAPSED")) {
                    if (isSortFilterUpdate) {
                        mRefreshLayout.setEnabled(false);
                        mRefreshLayout.setRefreshing(false);
                        if (loadLuckyDrawAsync != null && loadLuckyDrawAsync.getStatus() != AsyncTask.Status.RUNNING) {
                            loadLuckyDrawAsync = new LoadLuckyDrawAsync();
                            loadLuckyDrawAsync.execute();
                        }
                    }

                    mPanelScrollview.setScrollingEnabled(false);
                } else {

                    mPanelScrollview.setScrollingEnabled(false);
                }

            }
        });

        mPanelScrollview.setScrollingEnabled(false);

        mSlidePanel.getChildAt(1).setOnClickListener(null);

        mSlidePanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //updateData();
                //resetFilter();
                //resetSort();
            }
        });

        mRefreshLayout.setEnabled(false);

        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mRefreshLayout.setEnabled(layoutListManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });

        findViewById(R.id.tv_sort_reset).setOnClickListener(this);
        findViewById(R.id.tv_filter_reset).setOnClickListener(this);

        final List<String> sorts = new ArrayList<>();
        sorts.add(getString(SortFilterCondition.SORT_BY_PRICE_ASC));
        sorts.add(getString(SortFilterCondition.SORT_BY_PRICE_DESC));
        sorts.add(getString(SortFilterCondition.SORT_BY_RELEASE_DATE_ASC));
        sorts.add(getString(SortFilterCondition.SORT_BY_RELEASE_DATE_DESC));
//        sorts.add(getString(SortFilterCondition.SORT_BY_EXPIRED_DATE_ASC));
//        sorts.add(getString(SortFilterCondition.SORT_BY_EXPIRED_DATE_DESC));

        final List<Integer> sortVals = new ArrayList<>();
        sortVals.add(SortFilterCondition.SORT_BY_PRICE_ASC);
        sortVals.add(SortFilterCondition.SORT_BY_PRICE_DESC);
        sortVals.add(SortFilterCondition.SORT_BY_RELEASE_DATE_ASC);
        sortVals.add(SortFilterCondition.SORT_BY_RELEASE_DATE_DESC);
//        sortVals.add(SortFilterCondition.SORT_BY_EXPIRED_DATE_ASC);
//        sortVals.add(SortFilterCondition.SORT_BY_EXPIRED_DATE_DESC);

        spDocType = findViewById(R.id.spin_sort);
        ArrayAdapter<String> spDocTypeAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, sorts) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setPadding(0, view.getPaddingTop(), 0, view.getPaddingBottom());
                return view;
            }
        };
        spDocTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDocType.setAdapter(spDocTypeAdapter);
        spDocType.setSelection(-1);
        spDocType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sortFilterCondition.getSort() != sortVals.get(spDocType.getSelectedItemPosition())) {
                    sortFilterCondition.setSort(sortVals.get(spDocType.getSelectedItemPosition()));
                    isSortFilterUpdate = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sortFilterCondition.setSort(SortFilterCondition.SORT_DEFAULT);
            }
        });

        setFilterItem(R.id.tg_category, R.id.filter_category, "category");
        setFilterItem(R.id.tg_member, R.id.filter_member, "member");
        setFilterItem(R.id.tg_payment, R.id.filter_payment, "payment");
        setFilterItem(R.id.tg_location, R.id.filter_location, "location");


        mPanelScrollview.setOnScrollListener(new LockableScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(LockableScrollView scrollView, int x, int y, int oldX, int oldY) {
                if (y == 0) {
                    isScrolledTop = true;
                } else {
                    isScrolledTop = false;

                }
            }

            @Override
            public void onEndScroll(LockableScrollView scrollView) {

            }
        });
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof LuckyDrawListResponse) {

            } else if (data instanceof UserLuckyDrawListResponse) {

            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_sort_reset:
                resetSort();
                break;
            case R.id.tv_filter_reset:
                resetFilter();
                break;
            case R.id.back_button:
                finish();
                break;
            case R.id.token_open_filter_button:
                mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                break;
        }
    }

    private void resetSort() {
        sortFilterCondition.resetSort();
        spDocType.setSelection(3);
    }

    private void resetFilter() {
        sortFilterCondition.resetFilter();

        setFilterItem(R.id.tg_category, R.id.filter_category, "category");
        setFilterItem(R.id.tg_member, R.id.filter_member, "member");
        setFilterItem(R.id.tg_payment, R.id.filter_payment, "payment");
        setFilterItem(R.id.tg_location, R.id.filter_location, "location");
    }

    private void updateData() {
        //mPresenter.requestAllLuckyDrawList(user.getId());
    }

    private void setFilterItem(@IdRes int tagview, @IdRes int rootView, final String listType) {
        findViewById(rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                Intent intent = new Intent(LuckyDrawWinnerActivity.this, MultiSelectActivity.class);
                intent.putExtra("type", listType);
                intent.putExtra("condition", JsonUtil.toString(sortFilterCondition));
                startActivityForResult(intent, ConstantGroup.EDIT_FILTER);
            }
        });

        TagView tagView = findViewById(tagview);
        tagView.removeAll();

        List<String> selectedItems = new ArrayList<>();

        if (listType.equalsIgnoreCase("member")) {
            selectedItems = sortFilterCondition.getMemberTypes();
        } else if (listType.equalsIgnoreCase("location")) {
            selectedItems = sortFilterCondition.getLocation();
        } else if (listType.equalsIgnoreCase("payment")) {
            selectedItems = sortFilterCondition.getPaymentTypes();
        } else if (listType.equalsIgnoreCase("voucher")) {
            selectedItems = sortFilterCondition.getVoucherTypes();
        } else if (listType.equalsIgnoreCase("category")) {
            selectedItems = sortFilterCondition.getCategories();
        }
        if (selectedItems == null) selectedItems = new ArrayList<>();

        List<Tag> addedTagList = new ArrayList<>();
        for (String item : selectedItems) {
            Tag tag = new Tag(item);
            addedTagList.add(tag);
        }

        tagView.addTags(addedTagList);
    }

    public void setLuckyDrawList(final ArrayList<LuckyDraw> data) {

        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<LuckyDraw>(this, data, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_luckydraw_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final LuckyDraw item) {
                    holder.setImageUrl(R.id.iv_luckydraw_image, item.getThumbnail(), R.drawable.placeholder);
                    holder.setText(R.id.tv_luckydraw_title, item.getTitle());
//                    holder.setBackground(R.id.ll_qty, item.getPrice() == 0 ? R.drawable.shape_price_free_bg : R.drawable.shape_price_bg);
                    if (item.isForPremium())
                        holder.setViewGone(R.id.iv_premium, false);
                    else
                        holder.setViewGone(R.id.iv_premium, true);

                    if (item.getUserPurchasedTotal() < 0) {
                        holder.setText(R.id.tv_your_coupon_count, "0/" + item.getLimitPerUser());
                    } else {
                        holder.setText(R.id.tv_your_coupon_count, item.getUserPurchasedTotal() + "/" + item.getLimitPerUser());
                    }
                    holder.setText(R.id.tv_total_coupon_count, item.getTotalPurchased() + "");
                    holder.setText(R.id.tv_date, item.getCreatedDate());

                    if (item.getLuckyDrawWinners() != null && item.getLuckyDrawWinners().size() > 0) {
                        holder.setViewGone(R.id.ll_winner_container, false);
                        new WinnerCoverAsync(holder.getTextView(R.id.tv_winner), item).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        holder.setViewGone(R.id.ll_winner_container, true);
                    }

                    holder.setOnClickListener(R.id.rl_luckydraw, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ClickUtil.isFastDoubleClick()) return;
//                            if (item.isForPremium() && !user.isPremiumMember()) {
//                                StaticGroup.showPremiumMemberDialog(VoucherActivity.this);
//                            } else {
                            StaticGroup.goToLuckyDrawDetailActivity(LuckyDrawWinnerActivity.this, item, holder.getImageView(R.id.iv_luckydraw_image));
//                            }
                        }
                    });
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
            mAdapter.setData(data);
        }

        if (data.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mIvError.setImageResource(R.drawable.voucher_empty);
            mTvErrorHead.setText(getString(R.string.error_luckydraw_empty_header));
            mTvErrorBody.setText(getString(R.string.error_luckydraw_empty_body));

            mRecyclerview.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);

            if (mRecyclerview.getVisibility() == View.GONE) {
                mRecyclerview.setVisibility(View.VISIBLE);
                mRecyclerview.startAnimation(mFadeIn);
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            super.onBackPressed();
        } else {
            mPanelScrollview.scrollTo(0, 0);
            mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantGroup.EDIT_FILTER) {
            if (data != null && data.hasExtra("condition")) {
                String condition = data.getStringExtra("condition");
                SortFilterCondition s = (SortFilterCondition) JsonUtil.toObject(condition, SortFilterCondition.class);
                if (!sortFilterCondition.isEquallsToCondition(s)) {
                    sortFilterCondition = s;
                    isSortFilterUpdate = true;
                }
            }
            switch (resultCode) {
                case ConstantGroup.EDIT_FILTER_CATEGORY_RESULT_CODE:
                    break;
                case ConstantGroup.EDIT_FILTER_VOUCHER_TYPE_RESULT_CODE:
                    break;
                case ConstantGroup.EDIT_FILTER_MEMBER_TYPE_RESULT_CODE:
                    break;
                case ConstantGroup.EDIT_FILTER_PAYMENT_TYPE_RESULT_CODE:
                    break;
                case ConstantGroup.EDIT_FILTER_LOCATION_RESULT_CODE:
                    break;
            }

            setFilterItem(R.id.tg_category, R.id.filter_category, "category");
            setFilterItem(R.id.tg_member, R.id.filter_member, "member");
            setFilterItem(R.id.tg_payment, R.id.filter_payment, "payment");
            setFilterItem(R.id.tg_location, R.id.filter_location, "location");
        }
    }

    @Override
    public void showProgress() {
        super.showProgress();
        mRecyclerview.setVisibility(View.GONE);
        //mAvi.smoothToShow();
        mAviContainer.setVisibility(View.VISIBLE);
        mAviContainer.startAnimation(mFadeIn);
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setEnabled(true);
        //mAvi.smoothToHide();
        mAviContainer.startAnimation(mFadeOut);
    }

    private class LoadLuckyDrawAsync extends AsyncTask<Void, Void, ArrayList<LuckyDraw>> {

        @Override
        protected void onPreExecute() {
            mRecyclerview.setVisibility(View.GONE);
            //mAvi.smoothToShow();
            mErrorView.setVisibility(View.GONE);
            mAviContainer.setVisibility(View.VISIBLE);
            mAviContainer.startAnimation(mFadeIn);
        }

        @Override
        protected ArrayList<LuckyDraw> doInBackground(Void... strings) {
            return StaticGroup.getFilteredLuckyDraw(completedLuckyDraws, sortFilterCondition);
        }

        @Override
        protected void onPostExecute(final ArrayList<LuckyDraw> filteredLuckyDraw) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setEnabled(true);
                    //mAvi.smoothToHide();
                    isSortFilterUpdate = false;
                    mAviContainer.startAnimation(mFadeOut);
                    if (filteredLuckyDraw != null) {
                        setLuckyDrawList(filteredLuckyDraw);
                    }
                }
            }, 300);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
