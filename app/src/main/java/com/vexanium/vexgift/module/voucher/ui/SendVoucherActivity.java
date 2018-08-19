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

@ActivityFragmentInject(contentViewId = R.layout.activity_send_voucher, toolbarTitle = R.string.exchange_send_voucher, withLoadingAnim = true)
public class SendVoucherActivity extends BaseActivity<IVoucherPresenter> implements IVoucherView {

    private VoucherCode voucherCode;
    private Voucher voucher;
    private User user;

    @Override
    protected void initView() {
        mPresenter = new IVoucherPresenterImpl(this);
        user = User.getCurrentUser(this);
        if (getIntent().hasExtra("voucher")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("voucher"))) {
                voucherCode = (VoucherCode) JsonUtil.toObject(getIntent().getStringExtra("voucher"), VoucherCode.class);
                voucher = voucherCode.getVoucher();
            }
        }
        if (voucher != null) {
            ViewUtil.setImageUrl(this, R.id.iv_coupon_image, voucher.getThumbnail(), R.drawable.placeholder);
            ViewUtil.setText(this, R.id.tv_coupon_title, voucher.getTitle());
            ViewUtil.setText(this, R.id.tv_coupon_exp, voucher.getExpiredDate());
        }

        findViewById(R.id.btn_generate_code).setOnClickListener(this);

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if(data != null){
            if(data instanceof VoucherGiftCode){
                updateView();
            }

        }else if(errorResponse != null){
            if(errorResponse.getMeta()!= null){
                if(errorResponse.getMeta().getStatus() / 100 == 4){
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
                }else{
                    StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
                }
            }
        }

    }

    private void updateView() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
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
                        if(ClickUtil.isFastDoubleClick())return;
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

}
