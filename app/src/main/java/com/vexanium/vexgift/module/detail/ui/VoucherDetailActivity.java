package com.vexanium.vexgift.module.detail.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.vexanium.vexgift.widget.CaptchaImageView;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;


@ActivityFragmentInject(contentViewId = R.layout.activity_voucher_detail)
public class VoucherDetailActivity extends BaseActivity {

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

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        ((AppBarLayout) toolbarLayout.getParent()).addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    findViewById(R.id.voucher_title).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(VoucherDetailActivity.this, R.color.material_black));
                    ((ImageView) findViewById(R.id.share_button)).setColorFilter(ContextCompat.getColor(VoucherDetailActivity.this, R.color.material_black));
                } else {
                    findViewById(R.id.voucher_title).setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(VoucherDetailActivity.this, R.color.material_white));
                    ((ImageView) findViewById(R.id.share_button)).setColorFilter(ContextCompat.getColor(VoucherDetailActivity.this, R.color.material_white));
                }
            }
        });
        if (voucherResponse != null) {
            Brand brand = voucherResponse.getVoucher().getBrand();
            ViewUtil.setImageUrl(this.getDecorView(), R.id.iv_coupon_image, voucherResponse.getVoucher().getPhoto(), R.drawable.placeholder);
            ViewUtil.setImageUrl(this.getDecorView(), R.id.iv_brand_image, brand.getPhoto(), R.drawable.placeholder);
            ViewUtil.setText(this.getDecorView(), R.id.tv_brand, brand.getTitle());
            ViewUtil.setText(this.getDecorView(), R.id.tv_coupon_title, voucherResponse.getVoucher().getTitle());
            ViewUtil.setText(this.getDecorView(), R.id.tv_time, voucherResponse.getVoucher().getExpiredDate());
            ((TextView) toolbar.findViewById(R.id.tv_toolbar_title)).setText(brand.getTitle());
            toolbarLayout.setTitle(brand.getTitle());
        } else {

        }

        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.share_button).setOnClickListener(this);
        findViewById(R.id.btn_claim).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.share_button:
                StaticGroup.shareWithShareDialog(App.getContext(), "Best Voucher from Vexanium", "Vex Gift");
                break;
            case R.id.btn_claim:
                CheckBox cbAggree = findViewById(R.id.cb_aggree);
                if(cbAggree.isChecked()) {
                    doCaptcha();
                }else{
                    new VexDialog.Builder(this)
                            .optionType(DialogOptionType.OK)
                            .title(getString(R.string.validate_checkbox_aggree_title))
                            .content(getString(R.string.validate_checkbox_aggree_content))
                            .autoDismiss(true)
                            .show();
                }
                break;
        }
    }

    private void doCaptcha() {
        View view = View.inflate(this, R.layout.include_captcha_get_voucher, null);
        final EditText etCaptcha = view.findViewById(R.id.et_captcha);
        final CaptchaImageView captchaImageView = view.findViewById(R.id.iv_captcha);
        view.findViewById(R.id.iv_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captchaImageView.regenerate();
            }
        });

        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.captcha_dialog_title))
                .content(getString(R.string.captcha_dialog_content))
                .addCustomView(view)
                .positiveText(getString(R.string.dialog_get_now))
                .negativeText(getString(R.string.dialog_cancel))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        if (TextUtils.isEmpty(etCaptcha.getText().toString())) {
                            etCaptcha.setError(getString(R.string.validate_empty_field));
                            captchaImageView.regenerate();
                        } else if (!etCaptcha.getText().toString().equalsIgnoreCase(captchaImageView.getCaptchaCode())) {
                            etCaptcha.setError(getString(R.string.captcha_validation_message));
                            etCaptcha.setText("");
                            captchaImageView.regenerate();
                        } else {
                            dialog.dismiss();
                            simulateGetVoucher();
                        }
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

    private void doGetVoucher() {
    }

    private void simulateGetVoucher() {
//        showProgress();
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        new VexDialog.Builder(VoucherDetailActivity.this.getApplicationContext())
                                .optionType(DialogOptionType.OK)
                                .title("Get Voucher Success")
                                .content("Congratulation! You get the voucher")
                                .autoDismiss(true)
                                .show();
                        StaticGroup.sendLocalNotification(App.getContext(),"Get Voucher Success", "Congratulation! You get the voucher. ","");
                    }
                });
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

        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
    }
}
