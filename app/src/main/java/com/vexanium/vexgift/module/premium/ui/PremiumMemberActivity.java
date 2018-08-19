package com.vexanium.vexgift.module.premium.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.rd.PageIndicatorView;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.PremiumPlan;
import com.vexanium.vexgift.bean.model.PremiumPurchase;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.UserAddress;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.PremiumHistoryResponse;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.bean.response.PremiumPurchaseResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.module.premium.presenter.IPremiumPresenter;
import com.vexanium.vexgift.module.premium.presenter.IPremiumPresenterImpl;
import com.vexanium.vexgift.module.premium.ui.adapter.PremiumPlanAdapter;
import com.vexanium.vexgift.module.premium.ui.helper.AdapterBuyOnClick;
import com.vexanium.vexgift.module.profile.ui.MyProfileActivity;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.module.security.ui.SecurityActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.FixedSpeedScroller;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

@ActivityFragmentInject(contentViewId = R.layout.activity_premium_member, toolbarTitle = R.string.premium_member)
public class PremiumMemberActivity extends BaseActivity<IPremiumPresenter> implements IProfileView, AdapterBuyOnClick {

    public static final int FRAGMENT_FIRST = 0;
    public static final int FRAGMENT_SECOND = 1;
    public static final int FRAGMENT_THIRD = 2;
    public static final int FRAGMENT_FOURTH = 3;
    public static final int PAGE_COUNT = 4;

    LoopingViewPager mVpPremium;
    PageIndicatorView mPiPremium;
    RecyclerView mRvPremiumPlan;

    ImageButton mHistoryButton;
    RelativeLayout mRlBecomePremiumTopContainer;
    LinearLayout mLlAlreadyPremiumTopContainer, mLlBuyPremiumContainer;

    ArrayList<PremiumPurchase> mPremiumHistoryList = new ArrayList<>();

    PremiumPlanAdapter mAdapter;

    User user;
    Calendar verifTimeLeft;
    UserAddress userAddress;

    @Override
    protected void initView() {
        mPresenter = new IPremiumPresenterImpl(this);
        user = User.getCurrentUser(this);
        verifTimeLeft = Calendar.getInstance();


        mPresenter.getActAddress(user.getId());

        mHistoryButton = (ImageButton) findViewById(R.id.ib_history);
        mHistoryButton.setEnabled(false);
        mVpPremium = (LoopingViewPager) findViewById(R.id.vp_premium_member);
        mPiPremium = (PageIndicatorView) findViewById(R.id.pi_premium_member);
        mRvPremiumPlan = (RecyclerView) findViewById(R.id.rv_premium);
        mRlBecomePremiumTopContainer = (RelativeLayout) findViewById(R.id.rl_premium_top_become_premium);
        mLlAlreadyPremiumTopContainer = (LinearLayout) findViewById(R.id.ll_premium_top_already_premium);
        mLlBuyPremiumContainer = (LinearLayout) findViewById(R.id.ll_buy_premium);

        ArrayList<IconText> data = new ArrayList<>();
        data.add(new IconText(R.drawable.ic_premium_voucher, R.string.premium_access_voucher));
        data.add(new IconText(R.drawable.ic_premium_referral, R.string.premium_referral_bonus));
        data.add(new IconText(R.drawable.ic_premium_luckydraw, R.string.premium_lucky_draw));
        data.add(new IconText(R.drawable.ic_premium_airdrop, R.string.premium_airdrop_token));

        PremiumPagerAdapter adapter = new PremiumPagerAdapter(this, data, true);

        mVpPremium.setAdapter(adapter);
        mVpPremium.setOffscreenPageLimit(PAGE_COUNT);

        mPiPremium.setCount(mVpPremium.getIndicatorCount());

        //Set IndicatorPageChangeListener on LoopingViewPager.
        //When the methods are called, update the Indicator accordingly.
        mVpPremium.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
            @Override
            public void onIndicatorProgress(int selectingPosition, float progress) {
            }

            @Override
            public void onIndicatorPageChange(int newIndicatorPosition) {
                mPiPremium.setSelection(newIndicatorPosition);
            }
        });

        mRvPremiumPlan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new PremiumPlanAdapter(this, this);
        mRvPremiumPlan.setAdapter(mAdapter);

        mHistoryButton.setOnClickListener(this);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mVpPremium.getContext(), null);
            // scroller.setFixedDuration(5000);
            mScroller.set(mVpPremium, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {
        mVpPremium.pauseAutoScroll();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVpPremium.resumeAutoScroll();
        if (user.getPremiumDurationLeft() > 0) {
            updateView(1);
        } else {
            updateView(0);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.ib_history:
                Intent intent = new Intent(this,PremiumHistoryActivity.class);
                intent.putExtra("premium_history_list",mPremiumHistoryList);
                startActivity(intent);
                break;

        }

    }

    @Override
    public void onItemClick(PremiumPlan data) {
        if (!user.isAuthenticatorEnable() || !user.isKycApprove()) {
            StaticGroup.openRequirementDialog(PremiumMemberActivity.this);
        } else {
            if(mPremiumHistoryList.size() > 0 && mPremiumHistoryList.get(0).getStatus() != 0) {
                doBuy(data);
            }else{
                showPendingWarning();
            }
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof PremiumListResponse) {
                PremiumListResponse premiumListResponse = (PremiumListResponse) data;
                setPremiumPlanList(premiumListResponse);
            } else if( data instanceof PremiumPurchaseResponse) {
                PremiumPurchaseResponse premiumPurchaseResponse = (PremiumPurchaseResponse) data;
                updatePendingBuyView(premiumPurchaseResponse.getPremiumPurchase());
                updateView(2);
            } else if( data instanceof UserAddressResponse){
                UserAddressResponse userAddressResponse = (UserAddressResponse) data;
                userAddress = userAddressResponse.getUserAddress();
                callPremiumHistoryList();
            } else if( data instanceof PremiumHistoryResponse){
                mPremiumHistoryList = ((PremiumHistoryResponse) data).getPremiumPurchase();
                Collections.sort(mPremiumHistoryList, new Comparator<PremiumPurchase>() {
                    @Override
                    public int compare(PremiumPurchase t0, PremiumPurchase t1) {
                        return t1.getCreatedAtDate().compareTo(t0.getCreatedAt());
                    }
                });
                mHistoryButton.setEnabled(true);
                callPremiumPlanList();
            }

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
        }
    }

    public void openRequirementDialog() {
        View view = View.inflate(this, R.layout.include_requirement, null);
        final RelativeLayout rlReqKyc = view.findViewById(R.id.req_kyc);
        final RelativeLayout rlReqGoogle2fa = view.findViewById(R.id.req_g2fa);

        final ImageView ivReqKyc = view.findViewById(R.id.iv_kyc);
        final ImageView ivReqGoogle2fa = view.findViewById(R.id.iv_g2fa);

        final TextView tvReqKyc = view.findViewById(R.id.tv_kyc);
        final TextView tvReqGoogle2fa = view.findViewById(R.id.tv_g2fa);

        boolean isReqCompleted = true;

        if (!user.isKycApprove()) {
            rlReqKyc.setBackgroundResource(R.drawable.shape_white_round_rect_with_grey_border);
            tvReqKyc.setTextColor(ContextCompat.getColor(this, R.color.material_black_sub_text_color));
            ivReqKyc.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_check_n));
            isReqCompleted = false;
            rlReqKyc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    Intent intent = new Intent(PremiumMemberActivity.this, MyProfileActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            rlReqKyc.setBackgroundResource(R.drawable.shape_white_round_rect_with_black_border);
            tvReqKyc.setTextColor(ContextCompat.getColor(this, R.color.material_black_text_color));
            ivReqKyc.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_check_p));
        }
        if (!user.isAuthenticatorEnable()) {
            rlReqGoogle2fa.setBackgroundResource(R.drawable.shape_white_round_rect_with_grey_border);
            tvReqGoogle2fa.setTextColor(ContextCompat.getColor(this, R.color.material_black_sub_text_color));
            ivReqGoogle2fa.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_check_n));
            isReqCompleted = false;
            rlReqGoogle2fa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    Intent intent = new Intent(PremiumMemberActivity.this, SecurityActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            rlReqGoogle2fa.setBackgroundResource(R.drawable.shape_white_round_rect_with_black_border);
            tvReqGoogle2fa.setTextColor(ContextCompat.getColor(this, R.color.material_black_text_color));
            ivReqGoogle2fa.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_check_p));
        }

        if (isReqCompleted) return;

        new VexDialog.Builder(this)
                .title(getString(R.string.vexpoint_requirement_dialog_title))
                .content(getString(R.string.vexpoint_requirement_dialog_desc))
                .addCustomView(view)
                .optionType(DialogOptionType.OK)
                .autoDismiss(true)
                .show();
    }

    public void setPremiumPlanList(PremiumListResponse premiumListResponse) {
        mAdapter.addItemList(premiumListResponse.getPremiumPlans());
        mAdapter.notifyDataSetChanged();
    }

    public class PremiumPagerAdapter extends LoopingPagerAdapter<IconText> {

        public PremiumPagerAdapter(Context context, ArrayList<IconText> itemList, boolean isInfinite) {
            super(context, itemList, isInfinite);
        }

        @Override
        protected View inflateView(int viewType, ViewGroup container, int listPosition) {
            return LayoutInflater.from(context).inflate(R.layout.item_premium_pager, container, false);
        }

        @Override
        protected void bindView(View convertView, int listPosition, int viewType) {
            ImageView icon = convertView.findViewById(R.id.iv_premium);
            TextView description = convertView.findViewById(R.id.tv_premium);

            icon.setImageResource(itemList.get(listPosition).iconId);
            description.setText(context.getText(itemList.get(listPosition).stringId));
        }
    }

    private void callPremiumHistoryList(){
        mPresenter.requestUserPremiumHistory(user.getId());
    }

    private void callPremiumPlanList() {
        mPresenter.requestPremiumList(user.getId());
    }

    private void callPurchasePremium(PremiumPlan plan){
        mPresenter.purchasePremium(user.getId(),plan.getDuration(),plan.getPrice(),plan.getCurrency());
    }

    private void updateView(int viewType) {
        if (viewType == 0) {
            //if not premium
            mLlAlreadyPremiumTopContainer.setVisibility(View.GONE);
            mLlBuyPremiumContainer.setVisibility(View.GONE);
            mRlBecomePremiumTopContainer.setVisibility(View.VISIBLE);
        } else if (viewType == 1) {
            //if already premium
            mRlBecomePremiumTopContainer.setVisibility(View.GONE);
            mLlBuyPremiumContainer.setVisibility(View.GONE);
            mLlAlreadyPremiumTopContainer.setVisibility(View.VISIBLE);
        } else {
            //buying premium
            mRlBecomePremiumTopContainer.setVisibility(View.GONE);
            mLlAlreadyPremiumTopContainer.setVisibility(View.GONE);
            mLlBuyPremiumContainer.setVisibility(View.VISIBLE);
        }
    }

    private void updatePendingBuyView(final PremiumPurchase premiumPurchase){
        
        verifTimeLeft.setTimeInMillis((premiumPurchase.getPaidBefore() * 1000) + Calendar.getInstance().getTimeInMillis());
//        ((TextView)findViewById(R.id.tv_vex_address)).setText(userAddress.getActAddress());
//        ((TextView)findViewById(R.id.tv_vex_amount)).setText(premiumPurchase.getPaidAmount());
//        ((TextView)findViewById(R.id.tv_address_send_to)).setText(premiumPurchase.getPaidTo());

        ViewUtil.setText(this, R.id.tv_vex_address, userAddress.getActAddress());
        ViewUtil.setText(this, R.id.tv_vex_amount, premiumPurchase.getPaidAmount() +" VEX");
        ViewUtil.setText(this, R.id.tv_address_send_to, premiumPurchase.getPaidTo());

        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                StaticGroup.copyToClipboard(PremiumMemberActivity.this, premiumPurchase.getPaidTo());
            }
        });


    }

    private void showPendingWarning(){
        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.premium_already_in_progress_title))
                .content(R.string.premium_already_in_progress_body)
                .positiveText(getString(R.string.premium_already_in_progress_check))
                .negativeText(getString(R.string.premium_already_in_progress_cancel))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(PremiumMemberActivity.this,PremiumHistoryDetailActivity.class);
                        intent.putExtra("premium_history_detail",mPremiumHistoryList.get(0));
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    private void doBuy(final PremiumPlan plan){

        View view = View.inflate(this, R.layout.include_buy_premium_confirmation, null);
        final TextView tvDay = view.findViewById(R.id.tv_premium_confirmation_day);
        final TextView tvVex = view.findViewById(R.id.tv_premium_confirmation_vex);
        final TextView tvTotal = view.findViewById(R.id.tv_premium_confirmation_total_amount);

        int day = plan.getDuration() / 24 / 3600;

        tvDay.setText(plan.getName());
        tvVex.setText(plan.getPrice() + " " + getString(R.string.premium_buy_vex));
        tvTotal.setText(plan.getPrice() * day + " VEX");

        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.premium_buy_confirmation_title))
                .content(R.string.premium_buy_confirmation_body)
                .addCustomView(view)
                .positiveText(getString(R.string.premium_buy_now_button))
                .negativeText(getString(R.string.premium_buy_cancel_button))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        callPurchasePremium(plan);
                        dialog.dismiss();
                    }
                })
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    public class IconText {
        int iconId;
        int stringId;

        public IconText(int iconId, int stringId) {
            this.iconId = iconId;
            this.stringId = stringId;
        }

        public int getIconId() {
            return iconId;
        }

        public int getStringId() {
            return stringId;
        }
    }

}
