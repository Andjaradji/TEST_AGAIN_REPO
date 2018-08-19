package com.vexanium.vexgift.module.vexpoint.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.module.security.ui.SecurityActivity;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenter;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenterImpl;
import com.vexanium.vexgift.module.vexpoint.view.IVexpointView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

import rx.Subscription;

@ActivityFragmentInject(contentViewId = R.layout.activity_vex_address, toolbarTitle = R.string.vexpoint_fill_address)
public class VexAddressActivity extends BaseActivity<IVexpointPresenter> implements IVexpointView {

    User user;
    private Subscription timeSubsription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IVexpointPresenterImpl(this);

        user = User.getCurrentUser(this);

        updateView();

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof UserAddressResponse) {
                UserAddressResponse userAddressResponse = (UserAddressResponse) data;
                if (userAddressResponse.getUserAddress() != null && userAddressResponse.getUserAddress().getStatus() == 0) {
                    new VexDialog.Builder(this)
                            .title(getString(R.string.vexpoint_address_success_dialog_title))
                            .content(getString(R.string.vexpoint_address_success_dialog_content))
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
                }
            }
        } else if (errorResponse != null) {
            if (errorResponse.getMeta().getStatus() / 100 == 4) {
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
            case R.id.btn_submit:
                if (user.isAuthenticatorEnable()) {
                    doSubmitAddress();
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onPause() {
        KLog.v("VoucherRedeemActivity", "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStart() {
        KLog.v("VoucherRedeemActivity", "onStart: ");
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        KLog.v("VoucherRedeemActivity", "onDestroy: ");
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        KLog.v("VoucherRedeemActivity", "onResume: ");
        super.onResume();

    }

    private void doSubmitAddress() {
        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_vex_address, R.id.et_ga_pin);
        if (isValid) {
            String address = ((EditText) findViewById(R.id.et_vex_address)).getText().toString();
            String token = ((EditText) findViewById(R.id.et_ga_pin)).getText().toString();
            mPresenter.requestSetActAddress(user.getId(), address, token);
        }
    }

    private void handleVexAddress(UserAddressResponse userAddressResponse) {
        TpUtil tpUtil = new TpUtil(this);
        tpUtil.put(TpUtil.KEY_USER_ADDRESS, JsonUtil.toString(userAddressResponse));

        updateView();
    }

    private void updateView() {
        if (User.isVexAddVerifTimeEnded()) {
            KLog.v("VexAddressActivity", "updateView: HPtes vex address verif");

            if (!user.isAuthenticatorEnable()) {
                new VexDialog.Builder(this)
                        .title(getString(R.string.vexpoint_address_need_g2fa_dialog_title))
                        .content(getString(R.string.vexpoint_address_need_g2fa_dialog_desc))
                        .positiveText(getString(R.string.vexpoint_address_need_g2fa_dialog_button))
                        .negativeText(getString(R.string.dialog_cancel))
                        .optionType(DialogOptionType.YES_NO)
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent(VexAddressActivity.this, SecurityActivity.class);
                                startActivity(intent);
                            }
                        })
                        .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .autoDismiss(true)
                        .show();
            }

        }
    }

}
