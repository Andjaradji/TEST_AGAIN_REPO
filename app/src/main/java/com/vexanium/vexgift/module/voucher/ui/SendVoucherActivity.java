package com.vexanium.vexgift.module.voucher.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.model.VoucherCode;
import com.vexanium.vexgift.bean.model.VoucherGiftCode;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenter;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenterImpl;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

import static com.vexanium.vexgift.app.StaticGroup.isAppAvailable;

@ActivityFragmentInject(contentViewId = R.layout.activity_send_voucher, toolbarTitle = R.string.exchange_send_voucher, withLoadingAnim = true)
public class SendVoucherActivity extends BaseActivity<IVoucherPresenter> implements IVoucherView {

    private VoucherCode voucherCode;
    private Voucher voucher;
    private User user;
    private boolean isGenerated;
    private String code;

    @Override
    protected void initView() {
        mPresenter = new IVoucherPresenterImpl(this);
        user = User.getCurrentUser(this);
        if (getIntent().hasExtra("voucher")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("voucher"))) {
                voucherCode = (VoucherCode) JsonUtil.toObject(getIntent().getStringExtra("voucher"), VoucherCode.class);
                voucher = voucherCode.getVoucher();
                isGenerated = voucherCode.isBeingGifted();
                if (isGenerated) {
                    doViewCode();
                }
            }
        }
        if (voucher != null) {
            ViewUtil.setImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
            ViewUtil.setText(this, R.id.tv_coupon_title, voucher.getTitle());
            ViewUtil.setText(this, R.id.tv_coupon_exp, voucher.getExpiredDate());
        }

        updateView();

        findViewById(R.id.btn_generate_code).setOnClickListener(this);
        findViewById(R.id.ll_send_code_container).setOnClickListener(this);
        ViewUtil.setOnClickListener(this, this,
                R.id.btn_generate_code,
                R.id.ll_send_code_container,
                R.id.send_voucher_share_fb_button,
                R.id.send_voucher_share_line_button,
                R.id.send_voucher_share_telegram_button,
                R.id.send_voucher_share_twitter_button,
                R.id.send_voucher_share_whatsapp_button,
                R.id.send_voucher_share_sm_button
        );
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof VoucherGiftCode) {
                VoucherGiftCode voucherGiftCode = (VoucherGiftCode) data;
                if (voucherGiftCode != null) {
                    code = voucherGiftCode.getCode();
                    isGenerated = true;
                    updateView();
                }
            }

        } else if (errorResponse != null) {
            if (errorResponse.getMeta() != null) {
                if (errorResponse.getMeta().getStatus() / 100 == 4) {
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
                } else {
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
                }
            }
        }

    }

    private void updateView() {
        if (isGenerated) {
            findViewById(R.id.btn_generate_code).setVisibility(View.GONE);
            findViewById(R.id.ll_send_code_container).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_share_container).setVisibility(View.VISIBLE);
            ViewUtil.setText(this, R.id.tv_guidance, getString(R.string.exchange_send_voucher_guide));
            ViewUtil.setText(this, R.id.tv_code, code);
        } else {
            findViewById(R.id.btn_generate_code).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_send_code_container).setVisibility(View.GONE);
            findViewById(R.id.ll_share_container).setVisibility(View.GONE);
            ViewUtil.setText(this, R.id.tv_guidance, getString(R.string.exchange_send_voucher_generated_guide));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        Context context = SendVoucherActivity.this;
        String text = "";
        switch (v.getId()) {
            case R.id.btn_generate_code:
                if (!user.isAuthenticatorEnable() || !user.isKycApprove()) {
                    StaticGroup.openRequirementDialog(SendVoucherActivity.this);
                } else {
                    new VexDialog.Builder(this)
                            .title(getString(R.string.exchange_send_voucher_warning_dialog_title))
                            .content(getString(R.string.exchange_send_voucher_warning_dialog_desc))
                            .optionType(DialogOptionType.YES_NO)
                            .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                @Override
                                public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                    if (ClickUtil.isFastDoubleClick()) return;
                                    doGenerate();
                                }
                            })
                            .autoDismiss(true)
                            .show();
                }
                break;
            case R.id.ll_send_code_container:
                StaticGroup.copyToClipboard(this, code);
                break;
            case R.id.send_voucher_share_fb_button:
                shareFacebook(text);
                break;
            case R.id.send_voucher_share_line_button:
                shareLine(text);
                break;
            case R.id.send_voucher_share_telegram_button:
                shareTelegram(text);
                break;
            case R.id.send_voucher_share_twitter_button:
                shareTwitter(text);
                break;
            case R.id.send_voucher_share_whatsapp_button:
                shareWhatsApp(text);
                break;
            case R.id.send_voucher_share_sm_button:
                shareSm(text);
                break;

        }
    }

    private void doGenerate() {
        View view = View.inflate(this, R.layout.include_send_voucher_ga, null);
        final EditText etGA = view.findViewById(R.id.et_ga);

        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.exchange_send_voucher_ga_title))
                .content(getString(R.string.exchange_send_voucher_ga_body))
                .addCustomView(view)
                .positiveText(getString(R.string.exchange_send_voucher_button_generate))
                .negativeText(getString(R.string.exchange_send_voucher_button_cancel))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        if (TextUtils.isEmpty(etGA.getText().toString())) {
                            etGA.setError(getString(R.string.validate_empty_field));
                        } else {
                            mPresenter.requestGetGiftCode(user.getId(), voucherCode.getId(), etGA.getText().toString());
                        }
                    }
                })
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .autoDismiss(true)
                .canceledOnTouchOutside(false)
                .show();
    }

    private void doViewCode() {
        View view = View.inflate(this, R.layout.include_send_voucher_ga, null);
        final EditText etGA = view.findViewById(R.id.et_ga);

        new VexDialog.Builder(this)
                .optionType(DialogOptionType.YES_NO)
                .title(getString(R.string.exchange_send_voucher_ga_title))
                .content(getString(R.string.exchange_send_voucher_view_code_ga_body))
                .addCustomView(view)
                .positiveText(getString(R.string.exchange_send_voucher_button_view))
                .negativeText(getString(R.string.exchange_send_voucher_button_cancel))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        if (TextUtils.isEmpty(etGA.getText().toString())) {
                            etGA.setError(getString(R.string.validate_empty_field));
                        } else {
                            mPresenter.requestGetGiftCode(user.getId(), voucherCode.getId(), etGA.getText().toString());
                        }
                    }
                })
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                    }
                })
                .autoDismiss(true)
                .canceledOnTouchOutside(false)
                .show();
    }


    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    // TODO: 20/08/18 refactoring
    private void shareSm(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                text);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    private void shareFacebook(String text) {
        String fullUrl = "https://m.facebook.com/sharer.php?u=..";
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.facebook.katana",
                    "com.facebook.katana.ShareLinkActivity");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(sharingIntent);

        } catch (Exception e) {
            try {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                shareIntent.setPackage("com.facebook.lite");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
            } catch (Exception er) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.putExtra(android.content.Intent.EXTRA_TEXT, text);
                i.setData(Uri.parse(fullUrl));
                startActivity(i);
            }

        }
    }

    private void shareTwitter(String text) {
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.twitter.android", "com.twitter.android.PostActivity");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(sharingIntent);
        } catch (Exception e) {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://mobile.twitter.com/compose/tweet"));
            startActivity(i);
        }
    }

    void shareLine(String text) {
        final String appName = "jp.naver.line.android";
        final boolean isAppInstalled = isAppAvailable(this, appName);
        if (isAppInstalled) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.setPackage(appName);
            myIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(myIntent, "Share with"));
        } else {
            Toast.makeText(this, "Line not Installed", Toast.LENGTH_SHORT).show();
        }
    }

    void shareTelegram(String text) {
        final String appName = "org.telegram.messenger";
        final boolean isAppInstalled = isAppAvailable(this, appName);
        if (isAppInstalled) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.setPackage(appName);
            myIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(myIntent, "Share with"));
        } else {
            Toast.makeText(this, "Telegram not Installed", Toast.LENGTH_SHORT).show();
        }
    }

    void shareWhatsApp(String text) {
        final String appName = "com.whatsapp";
        final boolean isAppInstalled = isAppAvailable(this, appName);
        if (isAppInstalled) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            myIntent.setPackage(appName);
            myIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(myIntent, "Share with"));
        } else {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }

}
