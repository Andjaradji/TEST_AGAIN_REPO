package com.vexanium.vexgift.module.premium.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.rd.PageIndicatorView;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.PremiumPlan;
import com.vexanium.vexgift.module.premium.ui.adapter.PremiumPlanAdapter;

import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_premium_member, toolbarTitle = R.string.premium_member)
public class PremiumMemberActivity extends BaseActivity {

    public static final int FRAGMENT_FIRST = 0;
    public static final int FRAGMENT_SECOND = 1;
    public static final int FRAGMENT_THIRD = 2;
    public static final int FRAGMENT_FOURTH = 3;
    public static final int PAGE_COUNT = 4;

    LoopingViewPager mVpPremium;
    PageIndicatorView mPiPremium;
    RecyclerView mRvPremiumPlan;

    @Override
    protected void initView() {
        mVpPremium = (LoopingViewPager) findViewById(R.id.vp_premium_member);
        mPiPremium = (PageIndicatorView) findViewById(R.id.pi_premium_member);
        mRvPremiumPlan = (RecyclerView) findViewById(R.id.rv_premium);

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

        PremiumPlanAdapter adapter = new PremiumPlanAdapter(this,itemList);
        mRvPremiumPlan.setAdapter(adapter);

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
