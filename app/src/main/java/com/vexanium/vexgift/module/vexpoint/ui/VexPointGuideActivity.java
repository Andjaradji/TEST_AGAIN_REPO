package com.vexanium.vexgift.module.vexpoint.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.rd.PageIndicatorView;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.bean.response.InviteCardResponse;
import com.vexanium.vexgift.module.vexpoint.ui.card.InviteCardPagerAdapter;
import com.vexanium.vexgift.module.vexpoint.ui.card.ShadowTransformer;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.TpUtil;

import java.util.ArrayList;

public class VexPointGuideActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private PageIndicatorView pageIndicatorView;

    private TextView h1TextView;

    private InviteCardPagerAdapter mCardAdapter;
    ViewPager.OnPageChangeListener pagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            KLog.v("Invite card Selected : " + position);
            if (mCardAdapter != null && mCardAdapter.getDataAt(position) != null) {
                if (!TextUtils.isEmpty(mCardAdapter.getDataAt(position).h1)) {
                    h1TextView.setText(mCardAdapter.getDataAt(position).h1);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private ShadowTransformer mCardShadowTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vex_point_guide);

        final TpUtil tpUtil = new TpUtil(this);
        tpUtil.put(TpUtil.KEY_LEAVE_APP, 0);

        mViewPager = findViewById(R.id.viewPager);

        mCardAdapter = new InviteCardPagerAdapter();

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(pagerListener);

        pageIndicatorView = findViewById(R.id.invite_card_pager_indicator);
        pageIndicatorView.setSelectedColor(ColorUtil.getColor(this, R.color.material_white));
        pageIndicatorView.setUnselectedColor(Color.parseColor("#1Affffff"));
        pageIndicatorView.setRadius(5);
        pageIndicatorView.setVisibility(View.VISIBLE);

        h1TextView = findViewById(R.id.invite_card_h1);

        final ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        int cardWidth = MeasureUtil.getScreenSize(this).x - MeasureUtil.dip2px(this, 35) * 2;
        int cardHeight = 416 * cardWidth / 298;

        params.width = MeasureUtil.getScreenSize(this).x;
        params.height = cardHeight;
        mViewPager.setLayoutParams(params);

        findViewById(R.id.invite_card_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                finish();
            }
        });

//        RetrofitManager.getInstance(HostType.STATIC_CDN_API).getInviteCardObservable()
//                .subscribe(new Subscriber<InviteCardResponse>() {
//                    @Override
//                    public void onCompleted() {}
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//                    @Override
//                    public void onNext(InviteCardResponse response) {
//                        KLog.v("InviteCardResponse : " + JsonUtil.toString(response));
//                        if (response != null && response.cardList != null) {
//                            mCardAdapter.setData(response.cardList);
//                            mViewPager.setAdapter(mCardAdapter);
//                            pageIndicatorView.setViewPager(mViewPager);
//                            if (response.cardList.get(0) != null && !TextUtils.isEmpty(response.cardList.get(0).h1)) {
//                                h1TextView.setText(response.cardList.get(0).h1);
//                            }
//                        }
//                    }
//                });
        loadData();
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Open VexPoint Guidance")
                .putContentType("Guidance")
                .putContentId("guide"));
    }

    private void loadData() {
        ArrayList<InviteCardResponse.InviteCard> cardList = new ArrayList<>();
        cardList.add(new InviteCardResponse.InviteCard(
                getString(R.string.guidance_vp_title_1),
                "https://s3-ap-southeast-1.amazonaws.com/vexgift/guide/pic1.webp",
                getString(R.string.guidance_vp_subtitle_1),
                getString(R.string.guidance_vp_desc_1),
                ""));
        cardList.add(new InviteCardResponse.InviteCard(
                getString(R.string.guidance_vp_title_2),
                "https://s3-ap-southeast-1.amazonaws.com/vexgift/guide/pic2.webp",
                getString(R.string.guidance_vp_subtitle_2),
                getString(R.string.guidance_vp_desc_2),
                ""));
        cardList.add(new InviteCardResponse.InviteCard(
                getString(R.string.guidance_vp_title_3),
                "https://s3-ap-southeast-1.amazonaws.com/vexgift/guide/pic3.webp",
                getString(R.string.guidance_vp_subtitle_3),
                getString(R.string.guidance_vp_desc_3),
                getString(R.string.guidance_vp_note_3)));
        cardList.add(new InviteCardResponse.InviteCard(
                getString(R.string.guidance_vp_title_4),
                "https://s3-ap-southeast-1.amazonaws.com/vexgift/guide/pic4.webp",
                getString(R.string.guidance_vp_subtitle_4),
                getString(R.string.guidance_vp_desc_4),
                ""));


        if (cardList != null) {
            mCardAdapter.setData(cardList);
            mViewPager.setAdapter(mCardAdapter);
            pageIndicatorView.setViewPager(mViewPager);
            if (cardList.get(0) != null && !TextUtils.isEmpty(cardList.get(0).h1)) {
                h1TextView.setText(cardList.get(0).h1);
            }
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        KLog.v("onUserLeaveHint");

        final TpUtil tPref = new TpUtil(this);
        tPref.put(TpUtil.KEY_LEAVE_APP, Process.myPid());
    }
}
