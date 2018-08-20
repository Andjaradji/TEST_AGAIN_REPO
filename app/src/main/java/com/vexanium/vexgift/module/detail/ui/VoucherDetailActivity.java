package com.vexanium.vexgift.module.detail.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.Notification;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Vendor;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.detail.presenter.IDetailPresenter;
import com.vexanium.vexgift.module.detail.presenter.IDetailPresenterImpl;
import com.vexanium.vexgift.module.detail.view.IDetailView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.CaptchaImageView;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.util.ArrayList;


@ActivityFragmentInject(contentViewId = R.layout.activity_voucher_detail)
public class VoucherDetailActivity extends BaseActivity<IDetailPresenter> implements IDetailView {

    private Voucher voucher;
    private CollapsingToolbarLayout toolbarLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IDetailPresenterImpl(this);
        user = User.getCurrentUser(this);

        if (getIntent().hasExtra("voucher")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("voucher"))) {
                voucher = (Voucher) JsonUtil.toObject(getIntent().getStringExtra("voucher"), Voucher.class);
            }
        }

        toolbarLayout = findViewById(R.id.collapsingToolbar);
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
            ViewUtil.setImageUrl(this, R.id.iv_brand_image, vendor.getThumbnail(), R.drawable.placeholder);
            ViewUtil.setText(this, R.id.tv_brand, vendor.getName());
            ViewUtil.setText(this, R.id.tv_coupon_title, voucher.getTitle());
            ViewUtil.setText(this, R.id.tv_time, "Available until " + voucher.getExpiredDate());
            if (voucher.getPrice() == 0) {
                ViewUtil.setText(this, R.id.tv_price, getString(R.string.free));
                findViewById(R.id.tv_price_info).setVisibility(View.GONE);
            } else {
                ViewUtil.setText(this, R.id.tv_price, voucher.getPrice() + " VP");
            }
            ViewUtil.setText(this, R.id.tv_desc, voucher.getLongDecription());
            ViewUtil.setText(this, R.id.tv_terms, voucher.getTermsAndCond());
            ViewUtil.setText(this, R.id.tv_avail, String.format(getString(R.string.voucher_availability), voucher.getQtyAvailable(), voucher.getQtyTotal()));
            ((TextView) toolbar.findViewById(R.id.tv_toolbar_title)).setText(vendor.getName());
            toolbarLayout.setTitle(vendor.getName());
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
                String deepUrl = String.format("vexgift://voucher?id=%d", voucher.getId());
                String message = String.format(getString(R.string.share_voucher_template), deepUrl);
                StaticGroup.shareWithShareDialog(App.getContext(), message, "Vex Gift");
                break;
            case R.id.btn_claim:
                CheckBox cbAggree = findViewById(R.id.cb_aggree);
                if (cbAggree.isChecked()) {
                    if (!user.isAuthenticatorEnable() || !user.isKycApprove()) {
                        StaticGroup.openRequirementDialog(VoucherDetailActivity.this);
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
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            KLog.v("VoucherDetailActivity", "handleResult: " + JsonUtil.toString(data));
        } else if (errorResponse != null) {
            if (errorResponse.getMeta() != null) {
                if (errorResponse.getMeta().isRequestError()) {
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
                } else {
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
                }
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

    private void doGetVoucher(String pin) {
        User user = User.getCurrentUser(this);
        mPresenter.requestBuyVoucher(user.getId(), voucher.getId(), pin);
//        simulateGetVoucher();
    }

    private void simulateGetVoucher() {
        CountDownTimer countDownTimer = new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                new VexDialog.Builder(VoucherDetailActivity.this)
                        .optionType(DialogOptionType.OK)
                        .title("Get Voucher")
                        .content("Your request is in queue. We will inform you through notification.")
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                VoucherDetailActivity.this.finish();
                                simulateGetVoucherSuccess();
                            }
                        })
                        .cancelable(false)
                        .autoDismiss(true)
                        .show();
            }
        };
        countDownTimer.start();
    }

    private void getVoucherSuccess() {
        Notification notification = new Notification();
        notification.setVoucher(voucher);
        notification.setType(Notification.TYPE_GET_SUCCESS);
        notification.setTime(System.currentTimeMillis());
        notification.setUrl("vexgift://box");
        notification.setNew(true);

        ArrayList<Notification> notifications = TableContentDaoUtil.getInstance().getNotifs();
        if (notifications == null) notifications = new ArrayList<>();
        notifications.add(notification);

        TableContentDaoUtil.getInstance().saveNotifsToDb(JsonUtil.toString(notifications));

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
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        StaticGroup.sendLocalNotificationWithBigImage(App.getContext(), title, message, url, resource);
                    }
                });

    }

    private void simulateGetVoucherSuccess() {
        Notification notification = new Notification();
        notification.setVoucher(voucher);
        notification.setType(Notification.TYPE_GET_SUCCESS);
        notification.setTime(System.currentTimeMillis());
        notification.setUrl("vexgift://box");
        notification.setNew(true);

        ArrayList<Notification> notifications = TableContentDaoUtil.getInstance().getNotifs();
        if (notifications == null) notifications = new ArrayList<>();
        notifications.add(notification);

        TableContentDaoUtil.getInstance().saveNotifsToDb(JsonUtil.toString(notifications));

        KLog.v("Post", "call: HPtes masuk");
        RxBus.get().post(RxBus.KEY_NOTIF_ADDED, 1);
        RxBus.get().post(RxBus.KEY_BOX_CHANGED, 1);

        CountDownTimer countDownTimer = new CountDownTimer(2000, 500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                String title = "Get Voucher Success";
                String message = String.format("Congratulation! You got %s ", voucher.getTitle());
                String url = "vexgift://notif";

                StaticGroup.sendLocalNotification(App.getContext(), title, message, url);
            }
        };
        countDownTimer.start();
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

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void hideProgress() {
        super.hideProgress();
    }
}
