package com.vexanium.vexgift.module.voucher.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.module.voucher.presenter.IVoucherPresenter;
import com.vexanium.vexgift.module.voucher.view.IVoucherView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_send_voucher, toolbarTitle = R.string.exchange_send_voucher)
public class SendVoucherActivity extends BaseActivity<IVoucherPresenter> implements IVoucherView {

    private Voucher voucher;

    @Override
    protected void initView() {
        if (getIntent().hasExtra("voucher")) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("voucher"))) {
                voucher = (Voucher) JsonUtil.toObject(getIntent().getStringExtra("voucher"), Voucher.class);
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
                        //if etGA true = doSomething()
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


    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

}
