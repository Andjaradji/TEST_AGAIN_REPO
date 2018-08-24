package com.vexanium.vexgift.module.home.ui;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator;
import com.socks.library.KLog;
import com.vexanium.vexgift.BuildConfig;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseFragment;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.BestVoucher;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.BestVoucherResponse;
import com.vexanium.vexgift.bean.response.FeaturedVoucherResponse;
import com.vexanium.vexgift.bean.response.HomeFeedResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.SettingResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.database.TablePrefDaoUtil;
import com.vexanium.vexgift.module.home.presenter.IHomePresenter;
import com.vexanium.vexgift.module.home.presenter.IHomePresenterImpl;
import com.vexanium.vexgift.module.home.view.IHomeView;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.profile.ui.MyProfileActivity;
import com.vexanium.vexgift.module.vexpoint.ui.VexPointActivity;
import com.vexanium.vexgift.module.voucher.ui.VoucherActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.NetworkUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.SwipeRefreshUtil;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.VexDialog;
import com.vexanium.vexgift.widget.discretescrollview.DSVOrientation;
import com.vexanium.vexgift.widget.discretescrollview.DiscreteScrollView;
import com.vexanium.vexgift.widget.discretescrollview.transform.Pivot;
import com.vexanium.vexgift.widget.discretescrollview.transform.ScaleTransformer;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.StaticGroup.CATEGORY_BAR;
import static com.vexanium.vexgift.app.StaticGroup.COMPLETE_FORM;
import static com.vexanium.vexgift.app.StaticGroup.CONNECT_FB;
import static com.vexanium.vexgift.app.StaticGroup.EXPLORE_BAR;
import static com.vexanium.vexgift.app.StaticGroup.HOT_LIST;
import static com.vexanium.vexgift.app.StaticGroup.NORMAL_COUPON;
import static com.vexanium.vexgift.app.StaticGroup.SHORTCUT_BAR;
import static com.vexanium.vexgift.app.StaticGroup.convertVpFormat;

@ActivityFragmentInject(contentViewId = R.layout.fragment_home)
public class HomeFragment extends BaseFragment<IHomePresenter> implements IHomeView {

    private AVLoadingIndicatorView mAvi;
    private SwipeRefreshLayout mSrlHome;
    private LinearLayout mVexPointButton;
    private TextView mVexPointText;
    private GridLayoutManager layoutListManager;
    private RecyclerView mRecyclerview;

    private LinearLayout mLlErrorView;
    private ImageView mIvError;
    private TextView mTvErrorTitle, mTvErrorBody;

    private BaseRecyclerAdapter<HomeFeedResponse> mAdapter;
    private ArrayList<HomeFeedResponse> data;
    private ArrayList<Voucher> vouchers;

    private ArrayList<BestVoucher> featuredVoucherList;
    private ArrayList<BestVoucher> bestVoucherList;
    private BaseRecyclerAdapter<BestVoucher> mFeaturedAdapter;
    private User user;
    private Animation mFadeIn;

    private Observable<Integer> mVexPointObservable;

    private Handler mRefreshHandler;
    private Runnable mRefreshRunnable;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected void initView(View fragmentRootView) {
        mPresenter = new IHomePresenterImpl(this);
        user = User.getCurrentUser(this.getActivity());
        mFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);

        mLlErrorView = fragmentRootView.findViewById(R.id.ll_error_view);
        mIvError = fragmentRootView.findViewById(R.id.iv_error_view);
        mTvErrorTitle = fragmentRootView.findViewById(R.id.tv_error_head);
        mTvErrorBody = fragmentRootView.findViewById(R.id.tv_error_body);

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
        mVexPointText.setText(convertVpFormat(user.getVexPoint()));

        BestVoucherResponse bestVoucherResponse = TableContentDaoUtil.getInstance().getBestVouchers();
        if (bestVoucherResponse != null)
            bestVoucherList = bestVoucherResponse.getBestVouchers();
        else {
            bestVoucherList = new ArrayList<>();
        }
        FeaturedVoucherResponse featuredVoucherResponse = TableContentDaoUtil.getInstance().getFeaturedVouchers();
        if (featuredVoucherResponse != null) {
            featuredVoucherList = featuredVoucherResponse.getFeaturedVoucher();
        } else {
            featuredVoucherList = new ArrayList<>();
        }

        loadData(bestVoucherList, featuredVoucherList);
        initHomeList();


        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                mSrlHome.setEnabled(layoutListManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });

        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mRecyclerview.computeVerticalScrollOffset() <= 10 && appBarLayout.getElevation() != 0) {
                    animator.start();
                } else if (mRecyclerview.computeVerticalScrollOffset() > 10 && appBarLayout.getElevation() != 10) {
                    appBarLayout.setElevation(10);
                }
            }
        });

        mVexPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;

                User user1 = User.getCurrentUser(HomeFragment.this.getActivity());

                if (!user1.isAuthenticatorEnable() || !user1.isKycApprove()) {
                    StaticGroup.openRequirementDialog(HomeFragment.this.getActivity(), false);
                } else {

                    Intent intent = new Intent(getActivity(), VexPointActivity.class);
                    Pair<View, String> p1 = Pair.create((View) mVexPointButton, "vexpoint_button");
                    Pair<View, String> p2 = Pair.create((View) mVexPointText, "vexpoint_amount");

                    if (getActivity() != null) {
                        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(), p1, p2);
                        startActivity(intent, options.toBundle());
                    }
                }
            }
        });

        mRefreshHandler = new Handler();
        mRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                mSrlHome.setEnabled(true);
            }
        };

        // Setup refresh listener which triggers new data loading
        mSrlHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mRefreshHandler!=null){
                    mRefreshHandler.removeCallbacks(mRefreshRunnable);
                }
                mLlErrorView.setVisibility(View.GONE);
                mSrlHome.setEnabled(false);
                mSrlHome.setRefreshing(false);
                if (NetworkUtil.isOnline(getActivity())) {
                    updateData();
                } else {
                    StaticGroup.showCommonErrorDialog(HomeFragment.this.getActivity(), getString(R.string.error_internet_header), getString(R.string.error_internet_body));
                }
            }
        });

        mPresenter.requestBestVoucherList(user.getId());
        mPresenter.requestFeaturedVoucherList(user.getId());
        mPresenter.requestKyc(user.getId());
        mPresenter.requestVoucherList(user.getId());

        mVexPointObservable = RxBus.get().register(RxBus.KEY_VEXPOINT_UPDATE, Integer.class);
        mVexPointObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer vexpoint) {
                mVexPointText.setText(String.valueOf(vexpoint));
            }
        });

        checkAppVersion();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.v("HomeFragment onCreateView");
        //super.onCreateView(inflater, container, savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

        if (data != null) {
            if (data instanceof BestVoucherResponse) {
                BestVoucherResponse bestVoucherResponse = (BestVoucherResponse) data;

                KLog.v("HomeFragment", "HPtes handleResult: Load Best Voucher");
                KLog.json(JsonUtil.toString(bestVoucherResponse));
                TableContentDaoUtil.getInstance().saveBestVoucherToDb(JsonUtil.toString(bestVoucherResponse));
                bestVoucherList = bestVoucherResponse.getBestVouchers();
                loadData(bestVoucherList, featuredVoucherList);

                setAdapterData();

            } else if (data instanceof FeaturedVoucherResponse) {
                FeaturedVoucherResponse bestVoucherResponse = (FeaturedVoucherResponse) data;
                KLog.v("HomeFragment", "HPtes handleResult: Load Featured Voucher");
                KLog.json(JsonUtil.toString(bestVoucherResponse));


                TableContentDaoUtil.getInstance().saveFeaturedVoucherToDb(JsonUtil.toString(bestVoucherResponse));
                featuredVoucherList = bestVoucherResponse.getFeaturedVoucher();
                loadData(bestVoucherList, featuredVoucherList);

                setAdapterData();
            } else if (data instanceof Kyc) {
                Kyc kyc = (Kyc) data;
                user.updateKyc(kyc);
                User.updateCurrentUser(HomeFragment.this.getActivity(), user);
                loadData(bestVoucherList, featuredVoucherList);

                setAdapterData();
            } else if(data instanceof VouchersResponse){
                VouchersResponse vouchersResponse = (VouchersResponse) data;
                TableContentDaoUtil.getInstance().saveVouchersToDb(JsonUtil.toString(vouchersResponse));
            }
        } else if (errorResponse != null) {
//            Toast.makeText(getActivity(), errorResponse.toString(), Toast.LENGTH_SHORT).show();
            if (errorResponse.getMeta() != null) {
                if (errorResponse.getMeta().isRequestError()) {
                    if (!errorResponse.getMeta().getMessage().contains("KYC"))
                        if (getActivity() != null)
                            StaticGroup.showCommonErrorDialog(HomeFragment.this.getActivity(), errorResponse.getMeta().getMessage());
                } else {
                    if (getActivity() != null)
                        StaticGroup.showCommonErrorDialog(HomeFragment.this.getActivity(), errorResponse.getMeta().getStatus());
                }
            } else {
                if (NetworkUtil.isOnline(getActivity())) {
                    if (getActivity() != null)
                        StaticGroup.showCommonErrorDialog(getActivity(), 0);
                } else {
                    if (mAdapter.getItemCount() > 0) {
                        StaticGroup.showCommonErrorDialog(HomeFragment.this.getActivity(), getString(R.string.error_internet_header), getString(R.string.error_internet_body));
                    } else {
                        if (mLlErrorView.getVisibility() != View.VISIBLE) {
                            mLlErrorView.setVisibility(View.VISIBLE);
                            mLlErrorView.startAnimation(mFadeIn);
                            mIvError.setImageResource(R.drawable.ic_no_connection);
                            mTvErrorTitle.setText(getText(R.string.error_internet_header));
                            mTvErrorBody.setText(getText(R.string.error_internet_body));
                        }
                    }
                }

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
        mAvi.hide();
    }

    public void checkAppVersion(){
        SettingResponse settingResponse = TablePrefDaoUtil.getInstance().getSettings();
        if (settingResponse != null && settingResponse.getSettings() != null && settingResponse.getSettings() != null) {
            final TpUtil tpUtil = new TpUtil(App.getContext());
            long latestCheckVersion = tpUtil.getLong(TpUtil.KEY_IS_LATEST_CHECK_VERSION, 0);
            long currentVersion = BuildConfig.VERSION_CODE;
            final long latestVersion = settingResponse.getSettingValByKey("newest_app_version");
            boolean isNeedUpdate = currentVersion < latestVersion && latestVersion != -1 && latestVersion != latestCheckVersion;
            if (isNeedUpdate) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new VexDialog.Builder(HomeFragment.this.getActivity())
                                .title(getString(R.string.appversion_dialog_title))
                                .content(getString(R.string.appversion_dialog_content))
                                .positiveText(getString(R.string.appversion_dialog_update_button))
                                .negativeText(getString(R.string.appversion_dialog_cancel_button))
                                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                        if (ClickUtil.isFastDoubleClick()) return;
                                        dialog.dismiss();
                                        StaticGroup.openVexgiftGooglePlay(HomeFragment.this.getActivity());
                                    }
                                })
                                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                        if (ClickUtil.isFastDoubleClick()) return;
                                        dialog.dismiss();
                                        tpUtil.put(TpUtil.KEY_IS_LATEST_CHECK_VERSION, latestVersion);
                                    }
                                })
                                .canceledOnTouchOutside(false)
                                .cancelable(false)
                                .autoDismiss(false)
                                .show();
                    }
                }, 300);
            }
        }
    }

    public void updateData() {
        //update data here

        data = new ArrayList<>();
        mAvi.smoothToShow();
        mRecyclerview.setVisibility(View.GONE);

        mPresenter.requestFeaturedVoucherList(user.getId());
        mPresenter.requestBestVoucherList(user.getId());
        mPresenter.requestKyc(user.getId());
    }

    public void loadData(ArrayList<BestVoucher> bestVouchers, ArrayList<BestVoucher> featuredVoucher) {

        data = new ArrayList<>();
        int idx = -1;

        data.add(++idx, new HomeFeedResponse(SHORTCUT_BAR));

        if (featuredVoucher != null && featuredVoucher.size() > 0) {
            data.add(++idx, new HomeFeedResponse(HOT_LIST, featuredVoucher));
        }

        data.add(++idx, new HomeFeedResponse(EXPLORE_BAR));

        if (bestVouchers != null && bestVouchers.size() > 0) {
            data.add(++idx, new HomeFeedResponse(CATEGORY_BAR, bestVouchers, "Best Voucher", "Today"));
        }

        if (user.getKyc() == null || !user.isKycApprove())
            data.add(++idx, new HomeFeedResponse(COMPLETE_FORM));

//        data.add(2, new HomeFeedResponse(COMPLETE_FORM));
//        data.add(3, new HomeFeedResponse(CONNECT_FB));
    }

    public void initHomeList(){
        mAdapter = new BaseRecyclerAdapter<HomeFeedResponse>(getActivity(), data, layoutListManager) {
            @Override
            public int getItemViewType(int position) {
                if(data!=null && data.size() > 0) {
                    return data.get(position).type;
                }
                return 0;
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
                        if (getActivity() != null) {
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
                        }
                        break;
                    case HOT_LIST:
                        if (item.object instanceof ArrayList<?>) {
                            setFeaturedVoucherList(holder, (ArrayList<BestVoucher>) item.object);
                        }
                        break;
                    case EXPLORE_BAR:
                        holder.setOnClickListener(R.id.token_button, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (ClickUtil.isFastDoubleClick()) return;
                                Intent intent = new Intent(HomeFragment.this.getActivity(), VoucherActivity.class);
                                intent.putExtra("isToken", true);
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
                        holder.setOnClickListener(R.id.category_show_button, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (ClickUtil.isFastDoubleClick()) return;
                                Intent intent = new Intent(HomeFragment.this.getActivity(), VoucherActivity.class);
                                startActivity(intent);
                            }
                        });
                        if (item.object instanceof ArrayList)
                            setVoucherList(holder, (ArrayList<BestVoucher>) item.object);
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
        if (this.getActivity() != null)
            mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this.getActivity(), 16)));
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
    }

    public void setAdapterData() {
        if (mAdapter != null) {
            mAdapter.setData(data);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshHandler.post(mRefreshRunnable);
                if (mAdapter.getItemCount() > 0 && mRecyclerview.getVisibility() == View.GONE) {
                    mRecyclerview.setVisibility(View.VISIBLE);
                    mRecyclerview.startAnimation(mFadeIn);
                }
            }
        }, 300);

    }

    public void setVoucherList(BaseRecyclerViewHolder holder, final ArrayList<BestVoucher> data) {
        final RecyclerView mRecyclerview = holder.getRecyclerView(R.id.recylerview);
        GridLayoutManager layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);
        BaseRecyclerAdapter<BestVoucher> mAdapter = new BaseRecyclerAdapter<BestVoucher>(getActivity(), data, layoutListManager) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_coupon_list;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final BestVoucher bestVoucher) {
                final Voucher item = bestVoucher.getVoucher();
                holder.setImageUrl(R.id.iv_coupon_image, item.getThumbnail(), R.drawable.placeholder);
                holder.setText(R.id.tv_coupon_title, item.getTitle());
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

                holder.setText(R.id.tv_coupon_exp, item.getExpiredDate());
                holder.setOnClickListener(R.id.rl_coupon, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        if (HomeFragment.this.getActivity() != null) {
                            if (item.isForPremium() && !user.isPremiumMember()) {
                                StaticGroup.showPremiumMemberDialog(HomeFragment.this.getActivity());
                            } else {
                                StaticGroup.goToVoucherDetailActivity(HomeFragment.this.getActivity(), item, holder.getImageView(R.id.iv_coupon_image));
                            }
                        }
                    }
                });

            }
        };
        mAdapter.setHasStableIds(true);
        mRecyclerview.setLayoutManager(layoutListManager);
        if (this.getActivity() != null)
            mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this.getActivity(), 16)));
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
    }

    public void setFeaturedVoucherList(BaseRecyclerViewHolder holder, ArrayList<BestVoucher> couponList) {
        DiscreteScrollView discreteScrollView = (DiscreteScrollView) holder.getView(R.id.scrollView);
        IndefinitePagerIndicator pagerIndicator = (IndefinitePagerIndicator) holder.getView(R.id.recyclerview_pager_indicator);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        mFeaturedAdapter = new BaseRecyclerAdapter<BestVoucher>(getActivity(), couponList, layoutManager) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_hot_list;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final BestVoucher bestVoucher) {
                final Voucher item = bestVoucher.getVoucher();
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
                        if (HomeFragment.this.getActivity() != null) {
                            if (item.isForPremium() && !user.isPremiumMember()) {
                                StaticGroup.showPremiumMemberDialog(HomeFragment.this.getActivity());
                            } else {
                                StaticGroup.goToVoucherDetailActivity(HomeFragment.this.getActivity(), item, holder.getImageView(R.id.iv_coupon_image));
                            }
                        }
                    }
                });
            }
        };
        discreteScrollView.setAdapter(mFeaturedAdapter);
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

}
