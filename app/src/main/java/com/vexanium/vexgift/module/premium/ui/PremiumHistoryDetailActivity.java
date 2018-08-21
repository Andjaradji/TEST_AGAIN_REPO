package com.vexanium.vexgift.module.premium.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.PremiumPurchase;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_premium_history_detail, toolbarTitle = R.string.premium_history)
public class PremiumHistoryDetailActivity extends BaseActivity {


    User user;
    RelativeLayout mMainDetailContainer;
    LinearLayout mLlBuyPremiumContainer;
    private Subscription timeSubsription;

    Calendar verifTimeLeft;

    private boolean isTimeUp = false;
    boolean isPending = false;

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        if(user.getUserAddress() == null){

        }
        verifTimeLeft = Calendar.getInstance();

        mMainDetailContainer = findViewById(R.id.rl_purchase_history_detail);
        mLlBuyPremiumContainer = findViewById(R.id.ll_buy_premium);

        PremiumPurchase data = (PremiumPurchase)getIntent().getSerializableExtra("premium_history_detail");

        if(data.getStatus() != 0) {
            isPending = false;
            if(mMainDetailContainer.getVisibility() != View.VISIBLE) {
                mMainDetailContainer.setVisibility(View.VISIBLE);
            }

            if(mLlBuyPremiumContainer.getVisibility() != View.GONE){
                mLlBuyPremiumContainer.setVisibility(View.GONE);
            }

            ViewUtil.setText(this, R.id.tv_purchase_history_detail_title, "PREMIUM #" + data.getId());
            ViewUtil.setText(this, R.id.tv_purchase_history_detail_date, data.getCreatedAtDate());
            if (data.getStatus() == 0) {
                ViewUtil.setText(this, R.id.tv_purchase_history_detail_status, getText(R.string.premium_purchase_pending));
            } else if (data.getStatus() == 1) {
                ViewUtil.setText(this, R.id.tv_purchase_history_detail_status, getText(R.string.premium_purchase_success));
            } else {
                ViewUtil.setText(this, R.id.tv_purchase_history_detail_status, getText(R.string.premium_purchase_failed));
            }
            ViewUtil.setText(this, R.id.tv_purchase_history_detail_plan, data.getPaidAmount() + " VEX/day (" + data.getDuration() / 24 / 3600 + " day)");
            ViewUtil.setText(this, R.id.tv_purchase_history_detail_paid_amount, data.getPaidAmount() + " VEX");
            ViewUtil.setText(this, R.id.tv_purchase_history_detail_paid_to, data.getPaidTo());
            ViewUtil.setText(this, R.id.tv_purchase_history_detail_paid_from, user.getUserAddress().getActAddress());

        }else{
            isPending = true;
            if(mMainDetailContainer.getVisibility() != View.GONE) {
                mMainDetailContainer.setVisibility(View.GONE);
            }

            if(mLlBuyPremiumContainer.getVisibility() != View.VISIBLE){
                mLlBuyPremiumContainer.setVisibility(View.VISIBLE);
            }

            updatePendingBuyView(data);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
            timeSubsription = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isPending)
            startDateTimer();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    private void setVerifTimeText() {
        TextView tvHour = findViewById(R.id.tv_time_left);

        Calendar now = Calendar.getInstance();

        long remainTime = verifTimeLeft.getTimeInMillis() - now.getTimeInMillis();
        remainTime = remainTime - TimeUnit.DAYS.toMillis(TimeUnit.MILLISECONDS.toDays(remainTime));

        if (remainTime < 0 && !isTimeUp) {
            isTimeUp = true;
        } else if (remainTime > 0) {
            isTimeUp = false;
        }

        String time = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));

        tvHour.setText(time);
    }

    private void startDateTimer() {
        if (timeSubsription == null && StaticGroup.isScreenOn(this, true)) {
            timeSubsription = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            //KLog.v("Date Time called");
                            if (!StaticGroup.isScreenOn(PremiumHistoryDetailActivity.this, true)) {
                                if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
                                    timeSubsription.unsubscribe();
                                }
                            } else {
                                setVerifTimeText();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                        }
                    });
        }
    }

    private void updatePendingBuyView(final PremiumPurchase premiumPurchase){

        verifTimeLeft.setTimeInMillis((premiumPurchase.getPaidBefore() * 1000) + Calendar.getInstance().getTimeInMillis());
//        ((TextView)findViewById(R.id.tv_vex_address)).setText(userAddress.getActAddress());
//        ((TextView)findViewById(R.id.tv_vex_amount)).setText(premiumPurchase.getPaidAmount());
//        ((TextView)findViewById(R.id.tv_address_send_to)).setText(premiumPurchase.getPaidTo());

        ViewUtil.setText(this, R.id.tv_vex_address, user.getUserAddress().getActAddress());
        ViewUtil.setText(this, R.id.tv_vex_amount, premiumPurchase.getPaidAmount() +" VEX");
        ViewUtil.setText(this, R.id.tv_address_send_to, premiumPurchase.getPaidTo());

        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                StaticGroup.copyToClipboard(PremiumHistoryDetailActivity.this, premiumPurchase.getPaidTo());
            }
        });


    }
}
