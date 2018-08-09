package com.vexanium.vexgift.module.exchange.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.Vendor;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.module.more.ui.MoreFragment;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;

@ActivityFragmentInject(contentViewId = R.layout.activity_send_voucher, toolbarTitle = R.string.exchange_send_voucher)
public class SendVoucherActivity extends BaseActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_generate_code:
                toast("Generate Code");
                break;
        }

    }


    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

}
