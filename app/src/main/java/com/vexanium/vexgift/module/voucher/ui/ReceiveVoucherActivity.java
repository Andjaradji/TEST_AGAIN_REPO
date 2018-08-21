package com.vexanium.vexgift.module.voucher.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenter;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenterImpl;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;
import com.vexanium.vexgift.widget.CaptchaImageView;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_receive_voucher, toolbarTitle = R.string.exchange_receive_voucher)
public class ReceiveVoucherActivity extends BaseActivity<IVoucherPresenter> implements IVoucherView {

    private User user;
    private String code;

    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IVoucherPresenterImpl(this);

        findViewById(R.id.btn_receive_voucher).setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            // TODO: 20/08/18 check Receive Voucher
            String content = String.format(getString(R.string.exchange_receive_voucher_success_dialog_content), "");

            new VexDialog.Builder(this)
                    .title(getString(R.string.exchange_receive_voucher_success_dialog_title))
                    .content(content)
                    .optionType(DialogOptionType.OK)
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();

        } else if (errorResponse != null && errorResponse.getMeta() != null) {
            if (errorResponse.getMeta().isRequestError()) {
                StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
            } else {
                StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_receive_voucher:
                code = ((EditText) findViewById(R.id.et_code)).getText().toString();
                if (TextUtils.isEmpty(code)) {
                    ((EditText) findViewById(R.id.et_code)).setError(getString(R.string.validate_empty_field));
                } else {
                    doCaptcha();
                }
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
                            mPresenter.requestClaimGiftCode(user.getId(), 1, code);
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

}
