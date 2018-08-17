package com.vexanium.vexgift.module.premium.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.rd.PageIndicatorView;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.PremiumPlan;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.premium.presenter.IPremiumPresenter;
import com.vexanium.vexgift.module.premium.presenter.IPremiumPresenterImpl;
import com.vexanium.vexgift.module.premium.ui.adapter.PremiumPlanAdapter;
import com.vexanium.vexgift.module.premium.ui.helper.AdapterBuyOnClick;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.widget.FixedSpeedScroller;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

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

    RelativeLayout mRlBecomePremiumTopContainer;
    LinearLayout mLlAlreadyPremiumTopContainer, mLlBuyPremiumContainer;

    @Override
    protected void initView() {
        mPresenter = new IPremiumPresenterImpl(this);

        mVpPremium = (LoopingViewPager) findViewById(R.id.vp_premium_member);
        mPiPremium = (PageIndicatorView) findViewById(R.id.pi_premium_member);
        mRvPremiumPlan = (RecyclerView) findViewById(R.id.rv_premium);
        mRlBecomePremiumTopContainer = (RelativeLayout) findViewById(R.id.rl_premium_top_become_premium);
        mLlAlreadyPremiumTopContainer = (LinearLayout) findViewById(R.id.ll_premium_top_already_premium);
        mLlBuyPremiumContainer = (LinearLayout) findViewById(R.id.ll_buy_premium);

        ArrayList<IconText> data = new ArrayList<>();
        data.add(new IconText(R.drawable.ic_premium_voucher,R.string.premium_access_voucher));
        data.add(new IconText(R.drawable.ic_premium_referral,R.string.premium_referral_bonus));
        data.add(new IconText(R.drawable.ic_premium_luckydraw,R.string.premium_lucky_draw));
        data.add(new IconText(R.drawable.ic_premium_airdrop,R.string.premium_airdrop_token));

        //WalkthroughAdapter mAdapter = new WalkthroughAdapter(this,data);
        PremiumPagerAdapter mAdapter = new PremiumPagerAdapter(this,data,true);

        mVpPremium.setAdapter(mAdapter);
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

        mRvPremiumPlan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        ArrayList<PremiumPlan> itemList = new ArrayList<>();
        itemList.add(new PremiumPlan(200,1));
        itemList.add(new PremiumPlan(150,7));
        itemList.add(new PremiumPlan(100,30));

        PremiumPlanAdapter adapter = new PremiumPlanAdapter(this,this, itemList);
        mRvPremiumPlan.setAdapter(adapter);

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

        updateView(0);

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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    @Override
    public void onClickBuy(PremiumPlan data) {
        doBuy(data);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        String string = "PremiumActivity handleResult : " + JsonUtil.toString(data);
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
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


    private void updateView(int test) {
        if(test == 0){
            //if not premium
            mLlAlreadyPremiumTopContainer.setVisibility(View.GONE);
            mLlBuyPremiumContainer.setVisibility(View.GONE);
            mRlBecomePremiumTopContainer.setVisibility(View.VISIBLE);
        }else if(test == 1){
            //if already premium
            mRlBecomePremiumTopContainer.setVisibility(View.GONE);
            mLlBuyPremiumContainer.setVisibility(View.GONE);
            mLlAlreadyPremiumTopContainer.setVisibility(View.VISIBLE);
        }else{
            //buying premium
            mRlBecomePremiumTopContainer.setVisibility(View.GONE);
            mLlAlreadyPremiumTopContainer.setVisibility(View.GONE);
            mLlBuyPremiumContainer.setVisibility(View.VISIBLE);
        }
    }

    private void doBuy(PremiumPlan data){
        View view = View.inflate(this, R.layout.include_buy_premium_confirmation, null);
        final TextView tvDay = view.findViewById(R.id.tv_premium_confirmation_day);
        final TextView tvVex = view.findViewById(R.id.tv_premium_confirmation_vex);
        final TextView tvTotal = view.findViewById(R.id.tv_premium_confirmation_total_amount);

        tvDay.setText(data.getDay()+ " "+getString(R.string.premium_buy_day));
        tvVex.setText(data.getPrice()+ " "+getString(R.string.premium_buy_vex));
        tvTotal.setText(data.getPrice()*data.getDay()+ " VEX");

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
                        updateView(2);
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

    public class IconText{
        int iconId;
        int stringId;

        public IconText(int iconId, int stringId){
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
