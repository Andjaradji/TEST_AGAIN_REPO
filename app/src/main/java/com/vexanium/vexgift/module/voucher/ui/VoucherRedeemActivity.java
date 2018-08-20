package com.vexanium.vexgift.module.voucher.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Vendor;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.model.VoucherCode;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.VoucherCodeResponse;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenter;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenterImpl;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.NetworkUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import net.glxn.qrgen.android.QRCode;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_voucher_redeem, withLoadingAnim = true)
public class VoucherRedeemActivity extends BaseActivity<IVoucherPresenter> implements IVoucherView {
    private static final int VOUCHER_ONLINE = 200;
    private static final int VOUCHER_ONLINE_REDEEMED = 201;
    private static final int VOUCHER_3RD = 300;
    private static final int VOUCHER_3RD_REDEEMED = 301;
    private static final int VOUCHER_VENDOR = 101;
    private static final int VOUCHER_VENDOR_IN_REDEEM_PROCESS = 102;
    private static final int VOUCHER_VENDOR_REDEEMED = 103;
    private static final int VOUCHER_EXPIRED = 104;
    private static final int VOUCHER_IS_BEING_GIFTED = 105;
    private int state = VOUCHER_VENDOR;

    private VoucherCode voucherCode;
    private Voucher voucher;
    private CollapsingToolbarLayout toolbarLayout;
    private Subscription timeSubsription;

    private String code;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IVoucherPresenterImpl(this);
        user = User.getCurrentUser(this);

        if (getIntent().hasExtra("voucher")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("voucher"))) {
                voucherCode = (VoucherCode) JsonUtil.toObject(getIntent().getStringExtra("voucher"), VoucherCode.class);
                voucher = voucherCode.getVoucher();
                code = voucherCode.getVoucherCode();
            }
        }

        toolbarLayout = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        ((AppBarLayout) toolbarLayout.getParent()).addOnOffsetChangedListener(new VoucherRedeemActivity.AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, VoucherRedeemActivity.State state) {
                if (state == VoucherRedeemActivity.State.COLLAPSED) {
                    findViewById(R.id.voucher_title).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(App.getContext(), R.color.material_black));
                    ((ImageView) findViewById(R.id.send_button)).setColorFilter(ContextCompat.getColor(App.getContext(), R.color.material_black));
                } else {
                    findViewById(R.id.voucher_title).setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(App.getContext(), R.color.material_white));
                    ((ImageView) findViewById(R.id.send_button)).setColorFilter(ContextCompat.getColor(App.getContext(), R.color.material_white));
                }
            }
        });
        if (voucher != null) {
            Vendor vendor = voucher.getVendor();
            ViewUtil.setText(this, R.id.tv_brand, vendor.getName());
            ViewUtil.setText(this, R.id.tv_coupon_title, voucher.getTitle());
            ViewUtil.setText(this, R.id.tv_time, "Available until " + voucher.getExpiredDate());
            ViewUtil.setText(this, R.id.tv_desc, voucher.getLongDecription());
            ViewUtil.setText(this, R.id.tv_terms, voucher.getTermsAndCond());
            ((TextView) toolbar.findViewById(R.id.tv_toolbar_title)).setText(vendor.getName());
            toolbarLayout.setTitle(vendor.getName());

            if (voucherCode.isBeingGifted()) {
                state = VOUCHER_IS_BEING_GIFTED;
            } else if (voucherCode.isDeactivated()) {
                state = VOUCHER_VENDOR_REDEEMED;
            } else if (voucher.getValidUntil() < System.currentTimeMillis()) {
                state = VOUCHER_EXPIRED;
            } else if (voucher.isVendorCode()) {
                state = VOUCHER_VENDOR;
            } else if (voucher.isThirdParty()) {
                if (voucherCode.isClaimed()) {
                    state = VOUCHER_3RD_REDEEMED;
                } else {
                    state = VOUCHER_3RD;
                }
            } else if (voucher.isOnlineCode()) {
                if (voucherCode.isClaimed()) {
                    state = VOUCHER_ONLINE_REDEEMED;
                } else {
                    state = VOUCHER_ONLINE;
                }
            }
        } else {
            StaticGroup.showCommonErrorDialog(this, 0);
        }
        updateView();
        ViewUtil.setOnClickListener(this, this, R.id.back_button, R.id.send_button, R.id.btn_redeem);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.send_button:
                intent = new Intent(VoucherRedeemActivity.this, SendVoucherActivity.class);
                intent.putExtra("voucher", JsonUtil.toString(voucherCode));
                startActivity(intent);
                break;
            case R.id.btn_redeem:
                switch (state) {
                    case VOUCHER_3RD:
                        doSimulate3rdVoucherRedeem();
                        break;
                    case VOUCHER_ONLINE:
                    case VOUCHER_VENDOR:
                        doRedeem();
                        break;
                    case VOUCHER_VENDOR_IN_REDEEM_PROCESS:
                        doDeactive();
                        break;
                    case VOUCHER_3RD_REDEEMED:
                        intent = new Intent(VoucherRedeemActivity.this, VoucherWebViewActivity.class);
                        intent.putExtra("url", voucherCode.getVoucherCode());
                        intent.putExtra("voucher", JsonUtil.toString(voucher));
                        startActivity(intent);

                }
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof VoucherCodeResponse) {
                VoucherCodeResponse voucherCodeResponse = (VoucherCodeResponse) data;

                if (voucherCodeResponse != null && voucherCodeResponse.getVoucherCode() != null) {
                    if (voucherCodeResponse.getVoucherCode().getVoucher() != null) {

                        Voucher voucher = voucherCodeResponse.getVoucherCode().getVoucher();

                        if (voucher.isThirdParty()) {
                            state = VOUCHER_3RD_REDEEMED;
                        } else if (voucher.isOnlineCode()) {
                            state = VOUCHER_ONLINE_REDEEMED;
                        } else if (voucher.isVendorCode()) {
                            state = VOUCHER_VENDOR_IN_REDEEM_PROCESS;
                        }
                    } else if (voucherCodeResponse.getVoucherCode().isDeactivated()) {
                        state = VOUCHER_VENDOR_REDEEMED;
                    }
                    updateView();
                }
            }

        } else if (errorResponse != null) {
            if(NetworkUtil.isOnline(this)) {
                if (errorResponse.getMeta().isRequestError()) {
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
                } else {
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
                }
            }else{
                StaticGroup.showCommonErrorDialog(this, getString(R.string.error_internet_header), getString(R.string.error_internet_body));
            }
        }
    }

    private void doDeactive() {

        new VexDialog.Builder(VoucherRedeemActivity.this)
                .optionType(DialogOptionType.YES_NO)
                .positiveText("Deactivated")
                .negativeText("Cancel")
                .title("Confirmation")
                .content("You will deactive this voucher. Please make sure you have copy voucher code. This action cannot be undone")
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        mPresenter.requestDeactivatedVoucher(user.getId(), voucherCode.getId());
                    }
                })
                .cancelable(false)
                .autoDismiss(true)
                .show();
    }

    private void doSimulateDeactive() {

        new VexDialog.Builder(VoucherRedeemActivity.this)
                .optionType(DialogOptionType.YES_NO)
                .positiveText("Deactivated")
                .negativeText("Cancel")
                .title("Confirmation")
                .content("You will deactive this voucher. Please make sure you have copy voucher code. This action cannot be undone")
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        state = VOUCHER_VENDOR_REDEEMED;

                        RxBus.get().post(RxBus.KEY_BOX_HISTORY_ADDED, 1);
                        RxBus.get().post(RxBus.KEY_BOX_CHANGED, 1);

                        updateView();
                    }
                })
                .cancelable(false)
                .autoDismiss(true)
                .show();
    }

    private void doRedeem() {
        boolean isValid = true;
        final String merchantCode = ((EditText) findViewById(R.id.et_merchant_code)).getText().toString();
        String errMsg = "";
        boolean isVendorCode = voucher.isVendorCode();

        if (TextUtils.isEmpty(merchantCode) && isVendorCode) {
            isValid = false;
            ((EditText) findViewById(R.id.et_merchant_code)).setError(getString(R.string.validate_empty_field));
            errMsg = getString(R.string.validate_merchant_code_empty);
        }
        if (!isValid) {
            new VexDialog.Builder(VoucherRedeemActivity.this)
                    .optionType(DialogOptionType.OK)
                    .title("We Are Sorry")
                    .content(errMsg)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            ((EditText) findViewById(R.id.et_merchant_code)).setText("");
                        }
                    })
                    .cancelable(false)
                    .autoDismiss(true)
                    .show();
        } else {
//            doSimulateRedeem();
            new VexDialog.Builder(VoucherRedeemActivity.this)
                    .optionType(DialogOptionType.YES_NO)
                    .title("Get Voucher")
                    .positiveText("Redeem")
                    .negativeText("Cancel")
                    .content("You will redeem this voucher.")
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            mPresenter.requestRedeeemVoucher(user.getId(), voucherCode.getId(), merchantCode, code, voucher.getId());
                        }
                    })
                    .cancelable(false)
                    .autoDismiss(true)
                    .show();

        }
    }

    private void doSimulate3rdVoucherRedeem() {
        new VexDialog.Builder(VoucherRedeemActivity.this)
                .optionType(DialogOptionType.YES_NO)
                .title("Get Voucher")
                .positiveText("Redeem")
                .negativeText("Cancel")
                .content("You will redeem this voucher.")
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        state = VOUCHER_3RD_REDEEMED;
                        voucherCode.setVoucherCode("http://sepin.giftn.co.id/redeem/information?p=811914750254");
                        updateView();
                    }
                })
                .cancelable(false)
                .autoDismiss(true)
                .show();
    }

    private void doSimulateRedeem() {

        new VexDialog.Builder(VoucherRedeemActivity.this)
                .optionType(DialogOptionType.YES_NO)
                .title("Get Voucher")
                .positiveText("Redeem")
                .negativeText("Cancel")
                .content("You will redeem this voucher.")
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        state = VOUCHER_VENDOR_IN_REDEEM_PROCESS;
                        updateView();
                    }
                })
                .cancelable(false)
                .autoDismiss(true)
                .show();
    }

    private void updateView() {
        switch (state) {
            case VOUCHER_3RD:
            case VOUCHER_ONLINE:
                KLog.v("VoucherRedeemActivity", "updateView: VOUCHER NOT VENDOR ACTIVE");
                findViewById(R.id.send_button).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_active).setVisibility(View.GONE);
                findViewById(R.id.ll_countdown).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_inactived).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.GONE);
                findViewById(R.id.ll_merchant_info).setVisibility(View.GONE);
                ViewUtil.setImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setImageUrl(this, R.id.iv_brand_image, voucher.getVendor().getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_btn, getString(R.string.coupon_redeem_voucher));
                break;
            case VOUCHER_ONLINE_REDEEMED:
                KLog.v("VoucherRedeemActivity", "updateView: ONLINE VOUCHER ACTIVE");
                findViewById(R.id.send_button).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_countdown).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_active).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_inactived).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_merchant_info).setVisibility(View.GONE);
                findViewById(R.id.ll_online_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_button_container).setVisibility(View.VISIBLE);
                ViewUtil.setText(this, R.id.tv_online_voucher_info_desc, getString(R.string.voucher_online_info_desc));
                ViewUtil.setImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setImageUrl(this, R.id.iv_brand_image, voucher.getVendor().getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_btn, getString(R.string.voucher_online_button));
                ViewUtil.setText(this, R.id.tv_voucher_inactive, getString(R.string.coupon_redeemed));
                ViewUtil.setText(this, R.id.tv_inactive_time, voucherCode.getRedeemedDate());
                setCode(voucherCode.getVoucherCode());
                break;
            case VOUCHER_3RD_REDEEMED:
                KLog.v("VoucherRedeemActivity", "updateView: THIRD PARTY VOUCHER ACTIVE");
                findViewById(R.id.send_button).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_countdown).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_active).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_inactived).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.GONE);
                findViewById(R.id.ll_merchant_info).setVisibility(View.GONE);
                findViewById(R.id.ll_online_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_button_container).setVisibility(View.VISIBLE);
                ViewUtil.setImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setImageUrl(this, R.id.iv_brand_image, voucher.getVendor().getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_online_voucher_info_desc, getString(R.string.voucher_3rd_party_info_desc));
                ViewUtil.setText(this, R.id.tv_btn, getString(R.string.voucher_online_button));
                ViewUtil.setText(this, R.id.tv_voucher_inactive, getString(R.string.coupon_redeemed));
                ViewUtil.setText(this, R.id.tv_inactive_time, voucherCode.getRedeemedDate());
                break;
            case VOUCHER_VENDOR:
                KLog.v("VoucherRedeemActivity", "updateView: VOUCHER ACTIVE");
                findViewById(R.id.send_button).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_active).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_countdown).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_inactived).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.GONE);
                findViewById(R.id.ll_merchant_info).setVisibility(View.GONE);
                ViewUtil.setImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setImageUrl(this, R.id.iv_brand_image, voucher.getVendor().getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_btn, getString(R.string.coupon_redeem_voucher));
                break;
            case VOUCHER_VENDOR_IN_REDEEM_PROCESS:
                KLog.v("VoucherRedeemActivity", "updateView: VOUCHER IN REDEEMED PROCESS");
                findViewById(R.id.send_button).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_info).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_active).setVisibility(View.GONE);
                findViewById(R.id.ll_countdown).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_inactived).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_merchant_info).setVisibility(View.VISIBLE);
                ViewUtil.setImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setImageUrl(this, R.id.iv_brand_image, voucher.getVendor().getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_btn, getString(R.string.coupon_deactivated_voucher));
                setCode("811332791113");
                break;
            case VOUCHER_VENDOR_REDEEMED:
                KLog.v("VoucherRedeemActivity", "updateView: VOUCHER REDEEMED");
                findViewById(R.id.send_button).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_active).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_inactived).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.GONE);
                findViewById(R.id.ll_button_container).setVisibility(View.GONE);
                findViewById(R.id.ll_countdown).setVisibility(View.GONE);
                findViewById(R.id.ll_merchant_info).setVisibility(View.GONE);
                ViewUtil.setBnwImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setBnwImageUrl(this, R.id.iv_brand_image, voucher.getVendor().getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_voucher_inactive, getString(R.string.coupon_redeemed));
                ViewUtil.setText(this, R.id.tv_inactive_time, voucherCode.getRedeemedDate());
                break;
            case VOUCHER_IS_BEING_GIFTED:
                KLog.v("VoucherRedeemActivity", "updateView: VOUCHER IS BEING GIFTED");
                findViewById(R.id.send_button).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_active).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_inactived).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.GONE);
                findViewById(R.id.ll_button_container).setVisibility(View.GONE);
                findViewById(R.id.ll_countdown).setVisibility(View.GONE);
                findViewById(R.id.ll_merchant_info).setVisibility(View.GONE);
                ViewUtil.setBnwImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setBnwImageUrl(this, R.id.iv_brand_image, voucher.getVendor().getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_voucher_inactive, getString(R.string.coupon_gifted));
                ViewUtil.setText(this, R.id.tv_inactive_time, voucherCode.getRedeemedDate());
                break;
            case VOUCHER_EXPIRED:
                KLog.v("VoucherRedeemActivity", "updateView: VOUCHER EXPIRED");
                findViewById(R.id.send_button).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_info).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_active).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_inactived).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.GONE);
                findViewById(R.id.ll_voucher_show_to_merchant).setVisibility(View.GONE);
                findViewById(R.id.ll_countdown).setVisibility(View.GONE);
                findViewById(R.id.ll_button_container).setVisibility(View.GONE);
                findViewById(R.id.ll_merchant_info).setVisibility(View.GONE);
                ViewUtil.setBnwImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setBnwImageUrl(this, R.id.iv_brand_image, voucher.getVendor().getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_voucher_inactive, getString(R.string.coupon_expired));
                ViewUtil.setText(this, R.id.tv_inactive_time, voucher.getExpiredDate());
                break;
        }

    }

    private void setCode(String code) {
        if (code == null) code = "aKd124fA";
        ((TextView) findViewById(R.id.tv_code)).setText(code);
        Bitmap bitmap = QRCode.from(code).withSize(150, 150).bitmap();
        ImageView view = findViewById(R.id.iv_qr_code);
        Glide.with(App.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .centerCrop()
                )
                .load(bitmap)
                .into(view);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onPause() {
        KLog.v("VoucherRedeemActivity", "onPause: ");
        super.onPause();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
            timeSubsription = null;
        }
    }

    @Override
    protected void onStart() {
        KLog.v("VoucherRedeemActivity", "onStart: ");
        super.onStart();
        startDateTimer();
    }

    @Override
    protected void onDestroy() {
        KLog.v("VoucherRedeemActivity", "onDestroy: ");
        super.onDestroy();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        KLog.v("VoucherRedeemActivity", "onResume: ");
        super.onResume();
        startDateTimer();
    }


    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private void startDateTimer() {
        KLog.v("VoucherRedeemActivity", "startDateTimer: HPtes timer");
        if (timeSubsription == null && StaticGroup.isScreenOn(this, true)) {
            timeSubsription = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            if (!StaticGroup.isScreenOn(VoucherRedeemActivity.this, true)) {
                                if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
                                    timeSubsription.unsubscribe();
                                }
                            } else {
                                setWatchText();
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

    private void setWatchText() {
        TextView tvDay = findViewById(R.id.tv_day);
        TextView tvHour = findViewById(R.id.tv_hour);

        Calendar now = Calendar.getInstance();
        Calendar finish = Calendar.getInstance();
        finish.setTimeInMillis(voucher.getValidUntil());

        long remainTime = finish.getTimeInMillis() - now.getTimeInMillis();

        String day = String.format(getString(R.string.days), TimeUnit.MILLISECONDS.toDays(remainTime));

        long dayInMilis = TimeUnit.DAYS.toMillis(TimeUnit.MILLISECONDS.toDays(remainTime));
        remainTime = remainTime - dayInMilis;

        String time = String.format(Locale.getDefault(), "%02d : %02d : %02d",
                TimeUnit.MILLISECONDS.toHours(remainTime),
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));
        tvDay.setText(day);
        tvHour.setText(time);
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
