package com.vexanium.vexgift.module.redeem.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.Brand;
import com.vexanium.vexgift.bean.response.VoucherResponse;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;

@ActivityFragmentInject(contentViewId = R.layout.activity_voucher_redeem)
public class VoucherRedeemActivity extends BaseActivity {

    private VoucherResponse voucherResponse;
    private CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        if (getIntent().hasExtra("voucher")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("voucher"))) {
                voucherResponse = (VoucherResponse) JsonUtil.toObject(getIntent().getStringExtra("voucher"), VoucherResponse.class);
            }
        }

        toolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        ((AppBarLayout)toolbarLayout.getParent()).addOnOffsetChangedListener(new VoucherRedeemActivity.AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, VoucherRedeemActivity.State state) {
                if(state == VoucherRedeemActivity.State.COLLAPSED){
                    findViewById(R.id.voucher_title).setVisibility(View.VISIBLE);
                    ((ImageView)findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(App.getContext(), R.color.material_black));
                    ((ImageView)findViewById(R.id.share_button)).setColorFilter(ContextCompat.getColor(App.getContext(), R.color.material_black));
                }else{
                    findViewById(R.id.voucher_title).setVisibility(View.GONE);
                    ((ImageView)findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(App.getContext(), R.color.material_white));
                    ((ImageView)findViewById(R.id.share_button)).setColorFilter(ContextCompat.getColor(App.getContext(), R.color.material_white));
                }
            }
        });
        if (voucherResponse != null) {
            Brand brand = FixtureData.getRandomBrand();
            ViewUtil.setImageUrl(this.getDecorView(), R.id.iv_coupon_image, voucherResponse.getVoucher().getPhoto(), R.drawable.placeholder);
            ViewUtil.setImageUrl(this.getDecorView(), R.id.iv_brand_image, brand.getPhoto(), R.drawable.placeholder);
            ViewUtil.setText(this.getDecorView(), R.id.tv_brand, brand.getTitle());
            ViewUtil.setText(this.getDecorView(), R.id.tv_coupon_title, voucherResponse.getVoucher().getTitle());
            ViewUtil.setText(this.getDecorView(), R.id.tv_time, voucherResponse.getVoucher().getExpiredDate());
            ((TextView)toolbar.findViewById(R.id.tv_toolbar_title)).setText(FixtureData.getRandomBrand().getTitle());
            toolbarLayout.setTitle(brand.getTitle());
        }else{

        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;
            case R.id.share_button:
                StaticGroup.shareWithShareDialog(App.getContext(), "Best Voucher from Vexanium","Vex Gift");
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

        private VoucherRedeemActivity.State mCurrentState = VoucherRedeemActivity.State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != VoucherRedeemActivity.State.EXPANDED) {
                    onStateChanged(appBarLayout, VoucherRedeemActivity.State.EXPANDED);
                }
                mCurrentState = VoucherRedeemActivity.State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != VoucherRedeemActivity.State.COLLAPSED) {
                    onStateChanged(appBarLayout, VoucherRedeemActivity.State.COLLAPSED);
                }
                mCurrentState = VoucherRedeemActivity.State.COLLAPSED;
            } else {
                if (mCurrentState != VoucherRedeemActivity.State.IDLE) {
                    onStateChanged(appBarLayout, VoucherRedeemActivity.State.IDLE);
                }
                mCurrentState = VoucherRedeemActivity.State.IDLE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, VoucherRedeemActivity.State state);
    }
}
