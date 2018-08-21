package com.vexanium.vexgift.module.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
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
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.security.presenter.IGoogleAuthSettingPresenter;
import com.vexanium.vexgift.module.security.presenter.IGoogleAuthSettingPresenterImpl;
import com.vexanium.vexgift.module.security.view.IGoogleAuthSettingView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;

import net.glxn.qrgen.android.QRCode;

import java.io.Serializable;

import static com.vexanium.vexgift.app.ConstantGroup.GOOGLE2FA_STATE_RESULT_CODE;


@ActivityFragmentInject(contentViewId = R.layout.activity_google_auth_setting, toolbarTitle = R.string.security_google_auth_label)
public class GoogleAuthSettingActivity extends BaseActivity<IGoogleAuthSettingPresenter> implements IGoogleAuthSettingView {

    Google2faResponse google2faResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

        mPresenter = new IGoogleAuthSettingPresenterImpl(this);

        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_copy).setOnClickListener(this);
        findViewById(R.id.tv_code).setOnClickListener(this);
        findViewById(R.id.iv_qr_code).setOnClickListener(this);

        TpUtil tpUtil = new TpUtil(this);
        String google2fa = tpUtil.getString(TpUtil.KEY_GOOGLE2FA, "");
        if (!TextUtils.isEmpty(google2fa)) {
            google2faResponse = (Google2faResponse) JsonUtil.toObject(google2fa, Google2faResponse.class);
            setCode(google2faResponse);
        }

        if (google2faResponse == null) {
            User user = User.getCurrentUser(App.getContext());
            if (user != null)
                mPresenter.requestCode(user.getId());
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next:
                if (google2faResponse != null) {
                    Intent intent = new Intent(this, GoogleAuthStateActivity.class);
                    intent.putExtra("state", false);
                    startActivityForResult(intent, GOOGLE2FA_STATE_RESULT_CODE);
                }
                break;
            case R.id.tv_code:
            case R.id.iv_qr_code:
            case R.id.btn_copy:
                if (google2faResponse != null) {
                    StaticGroup.copyToClipboard(GoogleAuthSettingActivity.this, google2faResponse.getAuthenticationCode());
                }
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("GoogleAuthSettingActivity", "handleResult: " + JsonUtil.toString(data));
        if (data != null) {
            google2faResponse = (Google2faResponse) data;
            if (google2faResponse != null) {
                TpUtil tpUtil = new TpUtil(GoogleAuthSettingActivity.this);
                tpUtil.put(TpUtil.KEY_GOOGLE2FA, JsonUtil.toString(google2faResponse));

                setCode(google2faResponse);
            }
        } else if (errorResponse != null) {
            hideProgress();
            KLog.v("GoogleAuthSettingActivity handleResult error : " + errorResponse.getMeta().getMessage());
            toast(errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE2FA_STATE_RESULT_CODE) {
            if (requestCode == Activity.RESULT_OK) {
                if (data.hasExtra("nested")) {
                    finish();
                }
            }
        }
    }

    private void setCode(Google2faResponse google2faResponse) {
        ((TextView) findViewById(R.id.tv_code)).setText(google2faResponse.getAuthenticationCode());
        Bitmap bitmap = QRCode.from(google2faResponse.getAuthenticationUrl()).withSize(150, 150).bitmap();
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

}
