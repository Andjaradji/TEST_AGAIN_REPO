package com.vexanium.vexgift.module.voucher.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Vendor;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenter;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenterImpl;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.NetworkUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.util.Locale;


@ActivityFragmentInject(contentViewId = R.layout.activity_voucher_detail)
public class VoucherDetailActivity extends BaseActivity<IVoucherPresenter> implements IVoucherView {

    private Voucher voucher;
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
                voucher = (Voucher) JsonUtil.toObject(getIntent().getStringExtra("voucher"), Voucher.class);
            }
        }

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsingToolbar);
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
        if (voucher != null) {
            Vendor vendor = voucher.getVendor();
            ViewUtil.setImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);

            if (vendor != null) {
                ViewUtil.setImageUrl(this, R.id.iv_brand_image, vendor.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_brand, vendor.getName());
                ((TextView) toolbar.findViewById(R.id.tv_toolbar_title)).setText(vendor.getName());
                toolbarLayout.setTitle(vendor.getName());
            }
            ViewUtil.setText(this, R.id.tv_coupon_title, voucher.getTitle());

            ViewUtil.setText(this, R.id.tv_avail, String.format(getString(R.string.voucher_availability), voucher.getQtyAvailable(), voucher.getQtyLeft()));
            ViewUtil.setText(this, R.id.tv_desc, voucher.getLongDecription());
            ViewUtil.setText(this, R.id.tv_terms, voucher.getTermsAndCond());

            Linkify.addLinks((TextView) findViewById(R.id.tv_desc), Linkify.ALL);
            Linkify.addLinks((TextView) findViewById(R.id.tv_terms), Linkify.ALL);

            if (voucher.getVoucherTypeId() != 5) {
                ViewUtil.setText(this, R.id.tv_time, "Available until " + voucher.getExpiredDate());
                if (voucher.getPrice() == 0) {
                    ViewUtil.setText(this, R.id.tv_price, getString(R.string.free));
                    findViewById(R.id.tv_price_info).setVisibility(View.GONE);
                } else {
                    ViewUtil.setText(this, R.id.tv_price, voucher.getPrice() + " VP");
                }
                if (voucher.getLoyaltyPointRequired() > 0) {
                    ViewUtil.setText(this, R.id.tv_minimum_lp, String.format(getString(R.string.voucher_minimum_lp), voucher.getLoyaltyPointRequired()));
                } else {
                    findViewById(R.id.tv_minimum_lp).setVisibility(View.GONE);
                }
            } else {
                ViewUtil.setText(this, R.id.tv_price, getString(R.string.coming_soon));
                findViewById(R.id.tv_price_info).setVisibility(View.GONE);

                //hide checkbox
                findViewById(R.id.cb_agree).setVisibility(View.GONE);
            }
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Voucher Detail View " + voucher.getTitle())
                    .putContentType("Voucher Detail View")
                    .putContentId("viewDetail" + voucher.getId()));
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
                String deepUrl;
                if (voucher.isToken()) {
                    deepUrl = String.format(Locale.getDefault(), StaticGroup.FULL_DEEPLINK + "/token?id=%d", voucher.getId());
                } else {
                    deepUrl = String.format(Locale.getDefault(), StaticGroup.FULL_DEEPLINK + "/voucher?id=%d", voucher.getId());
                }
                String message = String.format(getString(R.string.share_voucher_template), deepUrl);
                StaticGroup.shareWithShareDialog(App.getContext(), message, "Vex Gift");

                Answers.getInstance().logShare(new ShareEvent()
                        .putMethod("Common")
                        .putContentName("Share Voucher " + voucher.getTitle())
                        .putContentType("Share Voucher")
                        .putContentId("" + voucher.getId()));
                break;
            case R.id.btn_claim:
                if (voucher.getVoucherTypeId() != 5) {
                    if (voucher.isForPremium() && !user.isPremiumMember()) {
                        StaticGroup.showPremiumMemberDialog(this);
                    } else if (voucher.getQtyAvailable() == 0) {
                        new VexDialog.Builder(this)
                                .optionType(DialogOptionType.OK)
                                .title(getString(R.string.voucher_soldout_dialog_title))
                                .content(getString(R.string.voucher_soldout_dialog_desc))
                                .autoDismiss(true)
                                .show();
                    } else {
                        CheckBox cbAggree = findViewById(R.id.cb_agree);
                        if (cbAggree.isChecked()) {
                            if (!user.isAuthenticatorEnable() || !user.isKycApprove()) {
                                StaticGroup.openRequirementDialog(VoucherDetailActivity.this, false);
                            } else {
                                doGoogle2fa();
                            }
                        } else {
                            new VexDialog.Builder(this)
                                    .optionType(DialogOptionType.OK)
                                    .title(getString(R.string.validate_checkbox_aggree_title))
                                    .content(getString(R.string.validate_checkbox_aggree_content))
                                    .autoDismiss(true)
                                    .show();
                        }
                    }
                } else {
                    new VexDialog.Builder(VoucherDetailActivity.this)
                            .optionType(DialogOptionType.OK)
                            .title("Coming soon")
                            .content("This voucher is currently unavailable")
                            .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                @Override
                                public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .cancelable(true)
                            .autoDismiss(true)
                            .show();
                }
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            KLog.v("VoucherDetailActivity", "handleResult: " + JsonUtil.toString(data));
        } else if (errorResponse != null) {
            if (NetworkUtil.isOnline(this)) {
                StaticGroup.showCommonErrorDialog(this, errorResponse);
            } else {
                StaticGroup.showCommonErrorDialog(this, getString(R.string.error_internet_header), getString(R.string.error_internet_body));
            }
        } else {
            new VexDialog.Builder(VoucherDetailActivity.this)
                    .optionType(DialogOptionType.OK)
                    .title("Get Voucher")
                    .content("Successfully get voucher")
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            VoucherDetailActivity.this.finish();
                            getVoucherSuccess();
                        }
                    })
                    .cancelable(false)
                    .autoDismiss(true)
                    .show();
        }
    }

    private void doGoogle2fa() {
        View view = View.inflate(this, R.layout.include_g2fa_get_voucher, null);
        final EditText etPin = view.findViewById(R.id.et_pin);

        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.google2fa_dialog_title))
                .content(getString(R.string.google2fa_dialog_desc))
                .addCustomView(view)
                .positiveText(getString(R.string.dialog_get_now))
                .negativeText(getString(R.string.dialog_cancel))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        if (TextUtils.isEmpty(etPin.getText().toString())) {
                            etPin.setError(getString(R.string.validate_empty_field));
                        } else {
                            dialog.dismiss();
                            doGetVoucher(etPin.getText().toString());
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

    private void doGetVoucher(String pin) {
        User user = User.getCurrentUser(this);
        mPresenter.requestBuyVoucher(user.getId(), voucher.getId(), pin);
    }

    private void getVoucherSuccess() {

        RxBus.get().post(RxBus.KEY_NOTIF_ADDED, 1);
        RxBus.get().post(RxBus.KEY_BOX_CHANGED, 1);
        RxBus.get().post(RxBus.KEY_VEXPOINT_UPDATE, 1);

        final String title = "Get Voucher Success";
        final String message = String.format("Congratulation! You got %s ", voucher.getTitle());
        final String url = "vexgift://notif";

        Glide.with(this)
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .format(DecodeFormat.PREFER_RGB_565))
                .load(voucher.getThumbnail())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        StaticGroup.sendLocalNotificationWithBigImage(App.getContext(), title, message, url, resource);
                    }
                });

    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
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
