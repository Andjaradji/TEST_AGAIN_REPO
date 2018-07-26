package com.vexanium.vexgift.module.security.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.security.presenter.IGoogleAuthStatePresenter;
import com.vexanium.vexgift.module.security.presenter.IGoogleAuthStatePresenterImpl;
import com.vexanium.vexgift.module.security.view.IGoogleAuthStateView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_google_auth_enable, toolbarTitle = R.string.security_google_auth_label)
public class GoogleAuthStateActivity extends BaseActivity<IGoogleAuthStatePresenter> implements IGoogleAuthStateView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("state")) {
            Boolean state = getIntent().getBooleanExtra("state", true);
            setContent(state);
        }
    }

    @Override
    protected void initView() {
        mPresenter = new IGoogleAuthStatePresenterImpl(this);

        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_next:

                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

    }

    private void doUpdateState(boolean isSetToEnable){
        boolean isValid = true;

        String code = ((EditText)findViewById(R.id.et_code)).getText().toString();
        if(TextUtils.isEmpty(code)){
            ((EditText)findViewById(R.id.et_code)).setError("This field cannot be empty");
            isValid = false;
        }

        TpUtil tpUtil = new TpUtil(this);
        Google2faResponse google2faResponse = null ;
        String g2faString = tpUtil.getString(TpUtil.KEY_GOOGLE2FA,"");
        if(!TextUtils.isEmpty(g2faString)){
            google2faResponse = (Google2faResponse) JsonUtil.toObject(g2faString, Google2faResponse.class);
        }

        int id = 7;
        if (isValid && google2faResponse != null){
            mPresenter.updateGoogle2faState(7, google2faResponse.getAuthenticationCode(), code, isSetToEnable);
        }
    }

    private void setContent(boolean isEnable) {
        if (isEnable) {
            ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.google2fa_enable_input));
            ((TextView) findViewById(R.id.tv_desc)).setText(getString(R.string.google2fa_enable_input_desc));
            ((TextView) findViewById(R.id.tv_button)).setText(getString(R.string.security_google2fa_enable));
        } else {
            ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.google2fa_disable));
            ((TextView) findViewById(R.id.tv_desc)).setText(getString(R.string.google2fa_disable_desc));
            ((TextView) findViewById(R.id.tv_button)).setText(getString(R.string.security_google2fa_disable));
        }
    }
}
