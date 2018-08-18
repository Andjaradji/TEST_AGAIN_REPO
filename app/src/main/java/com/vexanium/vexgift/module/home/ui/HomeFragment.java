package com.vexanium.vexgift.module.home.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.HomeFeedResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.home.presenter.IHomePresenter;
import com.vexanium.vexgift.module.home.presenter.IHomePresenterImpl;
import com.vexanium.vexgift.module.home.view.IHomeView;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.profile.ui.KycActivity;
import com.vexanium.vexgift.module.profile.ui.MyProfileActivity;
import com.vexanium.vexgift.module.security.ui.SecurityActivity;
import com.vexanium.vexgift.module.token.ui.TokenActivity;
import com.vexanium.vexgift.module.vexpoint.ui.VexPointActivity;
import com.vexanium.vexgift.module.voucher.ui.VoucherActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;
import com.vexanium.vexgift.widget.discretescrollview.DSVOrientation;
import com.vexanium.vexgift.widget.discretescrollview.DiscreteScrollView;
import com.vexanium.vexgift.widget.discretescrollview.transform.Pivot;
import com.vexanium.vexgift.widget.discretescrollview.transform.ScaleTransformer;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import rx.Observable;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.StaticGroup.CATEGORY_BAR;
import static com.vexanium.vexgift.app.StaticGroup.COMPLETE_FORM;
import static com.vexanium.vexgift.app.StaticGroup.CONNECT_FB;
import static com.vexanium.vexgift.app.StaticGroup.EXPLORE_BAR;
import static com.vexanium.vexgift.app.StaticGroup.HOT_LIST;
import static com.vexanium.vexgift.app.StaticGroup.NORMAL_COUPON;
import static com.vexanium.vexgift.app.StaticGroup.SHORTCUT_BAR;
import static com.vexanium.vexgift.app.StaticGroup.goToVoucherDetailActivity;

@ActivityFragmentInject(contentViewId = R.layout.fragment_home)
public class HomeFragment extends BaseFragment<IHomePresenter> implements IHomeView {

    private AVLoadingIndicatorView mAvi;
    private SwipeRefreshLayout mSrlHome;
    private LinearLayout mVexPointButton;
    private TextView mVexPointText;
    private GridLayoutManager layoutListManager;
    private RecyclerView mRecyclerview;

    private BaseRecyclerAdapter<HomeFeedResponse> mAdapter;
    private ArrayList<HomeFeedResponse> data;
    private ArrayList<Voucher> vouchers;
    private Random random;

    private ArrayList<Voucher> hotVoucherList;
    private BaseRecyclerAdapter<Voucher> mHotAdapter;
    private User user;
    private Animation mFadeIn;

    private Observable<Integer> mVexPointObservable;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        mPresenter = new IHomePresenterImpl(this);
        user = User.getCurrentUser(this.getActivity());
        mFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);

        mAvi = fragmentRootView.findViewById(R.id.avi);
        mSrlHome = fragmentRootView.findViewById(R.id.srl_home);
        mVexPointButton = fragmentRootView.findViewById(R.id.home_vp_button);
        mVexPointText = fragmentRootView.findViewById(R.id.home_vp_amount);
        mRecyclerview = fragmentRootView.findViewById(R.id.home_recyclerview);
        layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        final AppBarLayout appBarLayout = fragmentRootView.findViewById(R.id.app_bar);

        final ValueAnimator animator = ValueAnimator.ofFloat(10, 0);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                appBarLayout.setElevation((float) valueAnimator.getAnimatedValue());
                appBarLayout.requestLayout();
            }
        });

        App.setTextViewStyle((ViewGroup) fragmentRootView);

        final User user = User.getCurrentUser(HomeFragment.this.getActivity());
        mVexPointText.setText(String.valueOf(user.getVexPoint()));

        vouchers = TableContentDaoUtil.getInstance().getVouchers();
        if (vouchers != null) {
            loadData(vouchers);
            initHomeList();
        }

        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mRecyclerview.computeVerticalScrollOffset() == 0 && appBarLayout.getElevation() != 0) {
                    animator.start();
                } else if (mRecyclerview.computeVerticalScrollOffset() > 10 && appBarLayout.getElevation() != 10) {
                    appBarLayout.setElevation(10);
                }
                mSrlHome.setEnabled(layoutListManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });

        mVexPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;

                if (!user.isAuthenticatorEnable() || !user.isKycApprove()) {
                    openRequirementDialog();
                } else {

                    Intent intent = new Intent(getActivity(), VexPointActivity.class);
                    Pair<View, String> p1 = Pair.create((View) mVexPointButton, "vexpoint_button");
                    Pair<View, String> p2 = Pair.create((View) mVexPointText, "vexpoint_amount");

                    final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(), p1, p2);

                    //ActivityOptionsCompat options = ActivityOptionsCompat.
                    //        makeSceneTransitionAnimation(getActivity(), mVexPointButton, "vexpoint_button");
                    startActivity(intent, options.toBundle());
                }
            }
        });

        // Setup refresh listener which triggers new data loading
        mSrlHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });

        mPresenter.requestVoucherList(user.getId());

        mVexPointObservable = RxBus.get().register(RxBus.KEY_VEXPOINT_UPDATE, Integer.class);
        mVexPointObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer vexpoint) {
                mVexPointText.setText(String.valueOf(vexpoint));
            }
        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("HomeFragment onCreateView");
        //super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (mRecyclerview.getVisibility() == View.GONE) {
            mRecyclerview.setVisibility(View.VISIBLE);
            mRecyclerview.startAnimation(mFadeIn);
        }
        mSrlHome.setEnabled(true);
        if (data != null) {
            if (data instanceof VouchersResponse) {
                VouchersResponse vouchersResponse = (VouchersResponse) data;
                TableContentDaoUtil.getInstance().saveVouchersToDb(JsonUtil.toString(vouchersResponse));
                vouchers = vouchersResponse.getVouchers();

                loadData(vouchers);
                if (mAdapter == null) {
                    initHomeList();
                }
                mAdapter.notifyDataSetChanged();
                mPresenter.requestKyc(user.getId());
            } else if (data instanceof Kyc) {
                Kyc kyc = (Kyc) data;
                if (kyc != null) {
                    user.updateKyc(kyc);
                    User.updateCurrentUser(HomeFragment.this.getActivity(), user);
                }
            }
        } else if (errorResponse != null) {
//            Toast.makeText(getActivity(), errorResponse.toString(), Toast.LENGTH_SHORT).show();
            if (errorResponse.getMeta() != null) {
                if (errorResponse.getMeta().getStatus() / 100 == 4) {
                    if(!errorResponse.getMeta().getMessage().contains("KYC"))
                    StaticGroup.showCommonErrorDialog(HomeFragment.this.getActivity(), errorResponse.getMeta().getMessage());
                } else  {
                    StaticGroup.showCommonErrorDialog(HomeFragment.this.getActivity(), errorResponse.getMeta().getStatus());
                }
            } else {
                StaticGroup.showCommonErrorDialog(HomeFragment.this.getActivity(), 0);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVexPointObservable != null) {
            RxBus.get().unregister(RxBus.KEY_VEXPOINT_UPDATE, mVexPointObservable);
            mVexPointObservable = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        user = User.getCurrentUser(this.getActivity());
    }

    @Override
    public void hideProgress() {
        mAvi.smoothToHide();
    }

    public void openRequirementDialog() {
        View view = View.inflate(getActivity(), R.layout.include_requirement, null);
        final RelativeLayout rlReqKyc = view.findViewById(R.id.req_kyc);
        final RelativeLayout rlReqGoogle2fa = view.findViewById(R.id.req_g2fa);

        final ImageView ivReqKyc = view.findViewById(R.id.iv_kyc);
        final ImageView ivReqGoogle2fa = view.findViewById(R.id.iv_g2fa);

        final TextView tvReqKyc = view.findViewById(R.id.tv_kyc);
        final TextView tvReqGoogle2fa = view.findViewById(R.id.tv_g2fa);

        boolean isReqCompleted = true;

        if (!user.isKycApprove()) {
            rlReqKyc.setBackgroundResource(R.drawable.shape_white_round_rect_with_grey_border);
            tvReqKyc.setTextColor(ContextCompat.getColor(this.getContext(), R.color.material_black_sub_text_color));
            ivReqKyc.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.btn_check_n));
            isReqCompleted = false;
            rlReqKyc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    Intent intent = new Intent(HomeFragment.this.getActivity(), MyProfileActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            rlReqKyc.setBackgroundResource(R.drawable.shape_white_round_rect_with_black_border);
            tvReqKyc.setTextColor(ContextCompat.getColor(this.getContext(), R.color.material_black_text_color));
            ivReqKyc.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.btn_check_p));
        }
        if (!user.isAuthenticatorEnable()) {
            rlReqGoogle2fa.setBackgroundResource(R.drawable.shape_white_round_rect_with_grey_border);
            tvReqGoogle2fa.setTextColor(ContextCompat.getColor(this.getContext(), R.color.material_black_sub_text_color));
            ivReqGoogle2fa.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.btn_check_n));
            isReqCompleted = false;
            rlReqGoogle2fa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    Intent intent = new Intent(HomeFragment.this.getActivity(), SecurityActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            rlReqGoogle2fa.setBackgroundResource(R.drawable.shape_white_round_rect_with_black_border);
            tvReqGoogle2fa.setTextColor(ContextCompat.getColor(this.getContext(), R.color.material_black_text_color));
            ivReqGoogle2fa.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.btn_check_p));
        }

        if (isReqCompleted) return;

        new VexDialog.Builder(this.getContext())
                .title(getString(R.string.vexpoint_requirement_dialog_title))
                .content(getString(R.string.vexpoint_requirement_dialog_desc))
                .addCustomView(view)
                .optionType(DialogOptionType.OK)
                .autoDismiss(true)
                .show();
    }

    public void updateData() {
        //update data here
        mSrlHome.setEnabled(false);
        mSrlHome.setRefreshing(false);
        data = new ArrayList<>();
        mAvi.smoothToShow();
        mRecyclerview.setVisibility(View.GONE);
        vouchers = TableContentDaoUtil.getInstance().getVouchers();
        if (vouchers != null) {
            loadData(vouchers);
            initHomeList();
        }
        mPresenter.requestVoucherList(user.getId());
    }

    public void loadData(ArrayList<Voucher> vouchers) {
        hotVoucherList = StaticGroup.getVouchers(vouchers, 4);

        ArrayList<Voucher> voucherArrayList = StaticGroup.getVouchers(vouchers, 3);


        data = new ArrayList<>();
        int idx = -1;

        data.add(++idx, new HomeFeedResponse(SHORTCUT_BAR));

        if (hotVoucherList != null && hotVoucherList.size() > 0) {
            data.add(++idx, new HomeFeedResponse(HOT_LIST, hotVoucherList));
        }

        data.add(++idx, new HomeFeedResponse(EXPLORE_BAR));

        if (voucherArrayList != null && voucherArrayList.size() > 0) {
            data.add(++idx, new HomeFeedResponse(CATEGORY_BAR, voucherArrayList, "Best Voucher", "Today"));
        }

        if (user.getKyc() == null && user.getKyc().size() > 0)
            data.add(++idx, new HomeFeedResponse(COMPLETE_FORM));

//        data.add(2, new HomeFeedResponse(COMPLETE_FORM));
//        data.add(3, new HomeFeedResponse(CONNECT_FB));
    }

    public void initHomeList() {
        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<HomeFeedResponse>(getActivity(), data, layoutListManager) {
                @Override
                public int getItemViewType(int position) {
                    return data.get(position).type;
                }

                @Override
                public int getItemLayoutId(int viewType) {
                    switch (viewType) {
                        case SHORTCUT_BAR:
                            return R.layout.item_shortcut_bar;
                        case HOT_LIST:
                            return R.layout.item_hot_coupon_pager;
                        case EXPLORE_BAR:
                            return R.layout.item_explore_bar;
                        case CATEGORY_BAR:
                            return R.layout.item_category_home;
                        case COMPLETE_FORM:
                            return R.layout.item_fill_kyc;
                        case CONNECT_FB:
                            return R.layout.item_connect_fb;
                        case NORMAL_COUPON:
                        default:
                            return R.layout.item_coupon_list;
                    }
                }

                @Override
                public void bindData(BaseRecyclerViewHolder holder, int position, final HomeFeedResponse item) {
                    switch (getItemViewType(position)) {
                        case SHORTCUT_BAR:
                            holder.setOnClickListener(R.id.my_voucher_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ClickUtil.isFastDoubleClick()) return;
                                    ((MainActivity) getActivity()).gotoPage(1, 0);
                                }
                            });
                            holder.setOnClickListener(R.id.my_token_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ClickUtil.isFastDoubleClick()) return;
                                    ((MainActivity) getActivity()).gotoPage(1, 1);
                                }
                            });
                            holder.setOnClickListener(R.id.my_wallet_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ClickUtil.isFastDoubleClick()) return;
                                    ((MainActivity) getActivity()).gotoPage(2, 0);
                                }
                            });
                            break;
                        case HOT_LIST:
                            setHotVoucherList(holder, (ArrayList<Voucher>) item.object);
                            break;
                        case EXPLORE_BAR:
                            holder.setOnClickListener(R.id.token_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ClickUtil.isFastDoubleClick()) return;
                                    Intent intent = new Intent(HomeFragment.this.getActivity(), TokenActivity.class);
                                    startActivity(intent);
                                }
                            });
                            holder.setOnClickListener(R.id.voucher_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ClickUtil.isFastDoubleClick()) return;
                                    Intent intent = new Intent(HomeFragment.this.getActivity(), VoucherActivity.class);
                                    startActivity(intent);
                                }
                            });
                            break;
                        case CATEGORY_BAR:
                            holder.setText(R.id.tv_category_title, item.title);
                            holder.setText(R.id.tv_category_desc, item.desc);
                            if (item.object instanceof ArrayList)
                                setVoucherList(holder, (ArrayList<Voucher>) item.object);
                            break;
                        case COMPLETE_FORM:
                            holder.setOnClickListener(R.id.ll_fill_kyc_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ClickUtil.isFastDoubleClick()) return;
                                    Intent intent = new Intent(HomeFragment.this.getActivity(), MyProfileActivity.class);
                                    startActivity(intent);
                                }
                            });
                            break;
                        case NORMAL_COUPON:
                        default:
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
    }

    public void setVoucherList(BaseRecyclerViewHolder holder, final ArrayList<Voucher> data) {
        final RecyclerView mRecyclerview = holder.getRecyclerView(R.id.recylerview);
        GridLayoutManager layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);
        BaseRecyclerAdapter<Voucher> mAdapter = new BaseRecyclerAdapter<Voucher>(getActivity(), data, layoutListManager) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_coupon_list;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final Voucher item) {
                holder.setImageUrl(R.id.iv_coupon_image, item.getThumbnail(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_title, item.getTitle());
                holder.setBackground(R.id.ll_qty, item.getPrice() == 0 ? R.drawable.shape_price_bg : R.drawable.shape_price_bg);

                if (item.getQtyAvailable() == 0) {
                    holder.setText(R.id.tv_price, getString(R.string.out_of_stock));
                    holder.setBackground(R.id.ll_qty, R.drawable.shape_price_out_of_stock_bg);
                } else
                    holder.setText(R.id.tv_price, item.getPrice() == 0 ?
                            getString(R.string.free) :
                            String.format(getString(R.string.vex_point_format), item.getPrice()));

                if (item.isForPremium())
                    holder.setViewGone(R.id.iv_premium, false);
                else
                    holder.setViewGone(R.id.iv_premium, true);

                holder.setText(R.id.tv_coupon_exp, item.getExpiredDate());
                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        if (item.isForPremium() && !user.isPremiumMember()) {
                            StaticGroup.showPremiumMemberDialog(HomeFragment.this.getActivity());
                        } else {
                            StaticGroup.goToVoucherDetailActivity(HomeFragment.this.getActivity(), item, holder.getImageView(R.id.iv_coupon_image));
                        }
                    }
                });

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
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                App.setTextViewStyle(mRecyclerview);
            }
        });
    }

    public void setHotVoucherList(BaseRecyclerViewHolder holder, ArrayList<Voucher> couponList) {
        DiscreteScrollView discreteScrollView = (DiscreteScrollView) holder.getView(R.id.scrollView);
        IndefinitePagerIndicator pagerIndicator = (IndefinitePagerIndicator) holder.getView(R.id.recyclerview_pager_indicator);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        mHotAdapter = new BaseRecyclerAdapter<Voucher>(getActivity(), couponList, layoutManager) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_hot_list;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final Voucher item) {
                holder.setImageUrl(R.id.iv_coupon_image, item.getThumbnail(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_title, item.getTitle());
                holder.setText(R.id.tv_coupon_exp, item.getExpiredDate());
                holder.setBackground(R.id.ll_qty, item.getPrice() == 0 ? R.drawable.shape_price_free_bg : R.drawable.shape_price_bg);

                if (item.getQtyAvailable() == 0) {
                    holder.setText(R.id.tv_price, getString(R.string.out_of_stock));
                    holder.setBackground(R.id.ll_qty, R.drawable.shape_price_out_of_stock_bg);
                } else
                    holder.setText(R.id.tv_price, item.getPrice() == 0 ?
                            getString(R.string.free) :
                            String.format(getString(R.string.vex_point_format), item.getPrice()));

                if (item.isForPremium())
                    holder.setViewGone(R.id.iv_premium, false);
                else
                    holder.setViewGone(R.id.iv_premium, true);

                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isForPremium() && !user.isPremiumMember()) {
                            StaticGroup.showPremiumMemberDialog(HomeFragment.this.getActivity());
                        } else {
                            StaticGroup.goToVoucherDetailActivity(HomeFragment.this.getActivity(), item, holder.getImageView(R.id.iv_coupon_image));
                        }
                    }
                });
            }
        };
        discreteScrollView.setAdapter(mHotAdapter);
        discreteScrollView.setItemViewCacheSize(30);
        discreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
//        discreteScrollView.setSlideOnFling(true);
        discreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.CENTER)
                .build()
        );
        pagerIndicator.attachToRecyclerView(discreteScrollView);
        discreteScrollView.scrollToPosition(1);
    }

    private String getDate(long timeStamp){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

}
