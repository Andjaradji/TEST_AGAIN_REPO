package com.vexanium.vexgift.module.luckydraw.ui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.vexanium.vexgift.bean.model.LuckyDraw;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Vendor;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.luckydraw.helper.WinnerCoverAsync;
import com.vexanium.vexgift.module.luckydraw.presenter.ILuckyDrawPresenter;
import com.vexanium.vexgift.module.luckydraw.presenter.ILuckyDrawPresenterImpl;
import com.vexanium.vexgift.module.luckydraw.view.ILuckyDrawView;
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
public class LuckyDrawDetailActivity extends BaseActivity<ILuckyDrawPresenter> implements ILuckyDrawView {

    private LuckyDraw luckyDraw;
    private User user;

    private int amount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new ILuckyDrawPresenterImpl(this);
        user = User.getCurrentUser(this);

        if (getIntent().hasExtra("luckyDraw")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("luckyDraw"))) {
                luckyDraw = (LuckyDraw) JsonUtil.toObject(getIntent().getStringExtra("luckyDraw"), LuckyDraw.class);
            }
        }

        //TODO show share button
        ((ImageView) findViewById(R.id.share_button)).setVisibility(View.GONE);

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        ((AppBarLayout) toolbarLayout.getParent()).addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    findViewById(R.id.voucher_title).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(LuckyDrawDetailActivity.this, R.color.material_black));
                    ((ImageView) findViewById(R.id.share_button)).setColorFilter(ContextCompat.getColor(LuckyDrawDetailActivity.this, R.color.material_black));
                } else {
                    findViewById(R.id.voucher_title).setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(LuckyDrawDetailActivity.this, R.color.material_white));
                    ((ImageView) findViewById(R.id.share_button)).setColorFilter(ContextCompat.getColor(LuckyDrawDetailActivity.this, R.color.material_white));
                }
            }
        });
        if (luckyDraw != null) {
            Vendor vendor = luckyDraw.getVendor();
            ViewUtil.setImageUrl(this, R.id.iv_coupon_image, luckyDraw.getThumbnail(), R.drawable.placeholder);
            if (vendor != null) {
                ViewUtil.setImageUrl(this, R.id.iv_brand_image, vendor.getThumbnail(), R.drawable.placeholder);
                ViewUtil.setText(this, R.id.tv_brand, vendor.getName());
                toolbarLayout.setTitle(vendor.getName());
                ((TextView) toolbar.findViewById(R.id.tv_toolbar_title)).setText(vendor.getName());
            }

            ViewUtil.setText(this, R.id.tv_coupon_title, luckyDraw.getTitle());

            findViewById(R.id.ll_voucher_quantity_container).setVisibility(View.GONE);
            findViewById(R.id.ll_luckydraw_quantity_container).setVisibility(View.VISIBLE);

            if (luckyDraw.getUserPurchasedTotal() < 0) {
                ViewUtil.setText(this, R.id.tv_your_coupon_count, "0");
            } else {
                ViewUtil.setText(this, R.id.tv_your_coupon_count, luckyDraw.getUserPurchasedTotal() + "");
            }
            ViewUtil.setText(this, R.id.tv_your_coupon_max, "max: " + luckyDraw.getLimitPerUser());
            ViewUtil.setText(this, R.id.tv_total_coupon_max, "min: " + luckyDraw.getMinTicket() + ", max: " + luckyDraw.getMaxTicket());
            ViewUtil.setText(this, R.id.tv_total_coupon_count, luckyDraw.getTotalPurchased() + "");

            if (luckyDraw.getLuckyDrawWinners() != null && luckyDraw.getLuckyDrawWinners().size() > 0) {
                ViewUtil.findViewById(this, R.id.tv_luckydraw_winner_header).setVisibility(View.VISIBLE);
                ViewUtil.findViewById(this, R.id.fl_luckydraw_winner_container).setVisibility(View.VISIBLE);
                ViewUtil.findViewById(this, R.id.btn_claim).setVisibility(View.GONE);
                ViewUtil.findViewById(this, R.id.cb_agree).setVisibility(View.GONE);
                new WinnerCoverAsync((TextView) ViewUtil.findViewById(LuckyDrawDetailActivity.this, R.id.tv_luckydraw_winner_body), luckyDraw).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                ViewUtil.findViewById(this, R.id.tv_luckydraw_winner_header).setVisibility(View.GONE);
                ViewUtil.findViewById(this, R.id.fl_luckydraw_winner_container).setVisibility(View.GONE);
                ViewUtil.findViewById(this, R.id.btn_claim).setVisibility(View.VISIBLE);
                ViewUtil.findViewById(this, R.id.cb_agree).setVisibility(View.VISIBLE);
            }

            ViewUtil.setText(this, R.id.tv_desc_title, getString(R.string.luckydraw_detail_desc_title));
            ViewUtil.setText(this, R.id.tv_desc, luckyDraw.getLongDecription());
            ViewUtil.setText(this, R.id.tv_terms, luckyDraw.getTermsAndCond());

            ViewUtil.setText(this, R.id.tv_btn_text, getString(R.string.luckydraw_detail_claim_coupon));

            Linkify.addLinks((TextView) findViewById(R.id.tv_desc), Linkify.ALL);
            Linkify.addLinks((TextView) findViewById(R.id.tv_terms), Linkify.ALL);

            ViewUtil.setText(this, R.id.tv_time, "Available until " + luckyDraw.getExpiredDate());
            if (luckyDraw.getPrice() == 0) {
                ViewUtil.setText(this, R.id.tv_price, getString(R.string.free));
                findViewById(R.id.tv_price_info).setVisibility(View.GONE);
            } else {
                findViewById(R.id.tv_per_coupon).setVisibility(View.VISIBLE);
                ViewUtil.setText(this, R.id.tv_price, luckyDraw.getPrice() + " VP");
            }
            if (luckyDraw.getLoyaltyPointRequired() > 0) {
                ViewUtil.setText(this, R.id.tv_minimum_lp, String.format(getString(R.string.voucher_minimum_lp), luckyDraw.getLoyaltyPointRequired()));
            } else {
                findViewById(R.id.tv_minimum_lp).setVisibility(View.GONE);
            }

            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("LuckyDraw Detail View " + luckyDraw.getTitle())
                    .putContentType("LuckyDraw Detail View")
                    .putContentId("viewDetail" + luckyDraw.getId()));
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
                deepUrl = String.format(Locale.getDefault(), StaticGroup.FULL_DEEPLINK + "/luckyDraw?id=%d", luckyDraw.getId());
                String message = String.format(getString(R.string.share_voucher_template), deepUrl);
                StaticGroup.shareWithShareDialog(App.getContext(), message, "Vex Gift");

                Answers.getInstance().logShare(new ShareEvent()
                        .putMethod("Common")
                        .putContentName("Share Voucher " + luckyDraw.getTitle())
                        .putContentType("Share Voucher")
                        .putContentId("" + luckyDraw.getId()));
                break;
            case R.id.btn_claim:
                if (luckyDraw.getLuckyDrawWinners() != null && luckyDraw.getLuckyDrawWinners().size() > 0) {
                    return;
                }
                if (luckyDraw.isForPremium() && !user.isPremiumMember()) {
                    StaticGroup.showPremiumMemberDialog(this);
                } else {
                    CheckBox cbAggree = findViewById(R.id.cb_agree);
                    if (cbAggree.isChecked()) {
                        if (!user.isAuthenticatorEnable() || !user.isKycApprove()) {
                            StaticGroup.openRequirementDialog(LuckyDrawDetailActivity.this, false);
                        } else {
                            doEnterAmount();
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

                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            KLog.v("LuckyDrawDetailActivity", "handleResult: " + JsonUtil.toString(data));
        } else if (errorResponse != null) {
            if (NetworkUtil.isOnline(this)) {
                StaticGroup.showCommonErrorDialog(this, errorResponse);
            } else {
                StaticGroup.showCommonErrorDialog(this, getString(R.string.error_internet_header), getString(R.string.error_internet_body));
            }
        } else {
            new VexDialog.Builder(LuckyDrawDetailActivity.this)
                    .optionType(DialogOptionType.OK)
                    .title("Get Lucky Draw")
                    .content("Successfully get Lucky Draw")
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            LuckyDrawDetailActivity.this.finish();
                            getLuckyDrawSuccess();
                        }
                    })
                    .cancelable(false)
                    .autoDismiss(true)
                    .show();
        }
    }

    private void doEnterAmount() {
        View view = View.inflate(this, R.layout.include_luckydraw_dialog_amount, null);
        final EditText etAmount = view.findViewById(R.id.et_amount);
        final TextView tvTotal = view.findViewById(R.id.tv_amount_count_total);
        tvTotal.setText("-");
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etAmount.getText().toString().length() > 0) {
                    try {
                        amount = Integer.parseInt(etAmount.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String purchasedTotal = "Total = " + amount + " x " + luckyDraw.getPrice() + " = " + amount * luckyDraw.getPrice();
                    tvTotal.setText(purchasedTotal);
                } else {
                    tvTotal.setText("-");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.luckydraw_coupon_amount))
                .content(getString(R.string.luckydraw_coupon_amount_body))
                .addCustomView(view)
                .positiveText(getString(R.string.dialog_get_now))
                .negativeText(getString(R.string.dialog_cancel))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        if (TextUtils.isEmpty(etAmount.getText().toString())) {
                            etAmount.setError(getString(R.string.validate_empty_field));
                        } else {
                            dialog.dismiss();
                            doGoogle2fa(Integer.parseInt(etAmount.getText().toString()));
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

    private void doGoogle2fa(final int amount) {
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
                            doGetLuckyDraw(etPin.getText().toString(), amount);
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

    private void doGetLuckyDraw(String pin, int amount) {
        User user = User.getCurrentUser(this);
        mPresenter.buyLuckyDraw(user.getId(), luckyDraw.getId(), amount, pin);
    }

    private void getLuckyDrawSuccess() {

        RxBus.get().post(RxBus.KEY_NOTIF_ADDED, 1);
        RxBus.get().post(RxBus.KEY_BOX_CHANGED, 1);
        RxBus.get().post(RxBus.KEY_VEXPOINT_UPDATE, 1);

        final String title = "Get Lucky Draw Success";
        final String message = String.format("Congratulation! You got %s ", luckyDraw.getTitle());
        final String url = "vexgift://notif";

        Glide.with(this)
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .format(DecodeFormat.PREFER_RGB_565))
                .load(luckyDraw.getThumbnail())
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
