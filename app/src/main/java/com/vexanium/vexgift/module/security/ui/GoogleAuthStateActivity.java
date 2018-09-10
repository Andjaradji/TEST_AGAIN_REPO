package com.vexanium.vexgift.module.security.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.security.presenter.IGoogleAuthStatePresenter;
import com.vexanium.vexgift.module.security.presenter.IGoogleAuthStatePresenterImpl;
import com.vexanium.vexgift.module.security.view.IGoogleAuthStateView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_google_auth_enable, toolbarTitle = R.string.security_google_auth_label)
public class GoogleAuthStateActivity extends BaseActivity<IGoogleAuthStatePresenter> implements IGoogleAuthStateView {

    Boolean isEnable;
    User user;
    Google2faResponse google2faResponse;
    TpUtil tpUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("state")) {
            isEnable = getIntent().getBooleanExtra("state", true);
            setContent(isEnable);
        }
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IGoogleAuthStatePresenterImpl(this);
        tpUtil = new TpUtil(this);

        findViewById(R.id.btn_next).setOnClickListener(this);

        String g2faString = tpUtil.getString(TpUtil.KEY_GOOGLE2FA, "");
        if (!TextUtils.isEmpty(g2faString)) {
            google2faResponse = (Google2faResponse) JsonUtil.toObject(g2faString, Google2faResponse.class);
        }else{
            mPresenter.requestCode(user.getId());
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_next:
                doUpdateState(!isEnable);
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("KycActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {
            if(data instanceof Google2faResponse){
                google2faResponse = (Google2faResponse) data;
                tpUtil = new TpUtil(this);
                tpUtil.put(TpUtil.KEY_GOOGLE2FA, JsonUtil.toString(google2faResponse));
            }

        } else if (errorResponse != null) {
            KLog.v("MyProfileActivity handleResult error : " + errorResponse.getMeta().getMessage());
            if (errorResponse.getMeta().getStatus() == 404) {
                new VexDialog.Builder(GoogleAuthStateActivity.this)
                        .optionType(DialogOptionType.OK)
                        .okText("OK")
                        .title("Error")
                        .content("Google authenticator token invalid")
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                ((EditText) findViewById(R.id.et_code)).setText("");
                            }
                        })
                        .autoDismiss(true)
                        .show();
            } else {
                toast(errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
            }
        } else {
            isEnable = !isEnable;
            Toast.makeText(this, "isenable = "+isEnable, Toast.LENGTH_SHORT).show();
            User updatedUser = User.getCurrentUser(this);
            updatedUser.setAuthenticatorEnable(isEnable);
            User.updateCurrentUser(this, updatedUser);

            RxBus.get().post("google2faUpdateState", isEnable);

            new VexDialog.Builder(GoogleAuthStateActivity.this)
                    .optionType(DialogOptionType.OK)
                    .okText("OK")
                    .title("Success")
                    .content(getString(!isEnable ? R.string.google2fa_disable_success : R.string.google2fa_enable_success))
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            if (!isEnable) {
                                Intent intent = getIntent();
                                intent.putExtra("nested", true);
                                setResult(Activity.RESULT_OK, intent);
                            }
                            finish();
                        }
                    })
                    .autoDismiss(true)
                    .show();
        }
    }

    private void doUpdateState(boolean isSetToEnable) {
        boolean isValid = true;

        String code = ((EditText) findViewById(R.id.et_code)).getText().toString();
        if (TextUtils.isEmpty(code)) {
            ((EditText) findViewById(R.id.et_code)).setError("This field cannot be empty");
            isValid = false;
        }

        if (isValid && google2faResponse != null) {
            mPresenter.updateGoogle2faState(user.getId(), google2faResponse.getAuthenticationCode(), code, isSetToEnable);
        }else if(google2faResponse == null){
            mPresenter.requestCode(user.getId());
            StaticGroup.showCommonErrorDialog(this, "Google2fa key not found");
        }

    }

    private void setContent(boolean isEnable) {
        if (!isEnable) {
            ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.google2fa_enable_input));
            ((TextView) findViewById(R.id.tv_desc)).setText(getString(R.string.google2fa_enable_input_desc));
            ((TextView) findViewById(R.id.tv_button)).setText(getString(R.string.security_google2fa_enable));
        } else {
            ((TextView) findViewById(R.id.tv_title)).setText(getString(R.string.google2fa_disable));
            ((TextView) findViewById(R.id.tv_desc)).setText(getString(R.string.google2fa_disable_desc));
            ((TextView) findViewById(R.id.tv_button)).setText(getString(R.string.security_google2fa_disable));
        }
    }

    private void updateGoogle2faState(){

    }
}
