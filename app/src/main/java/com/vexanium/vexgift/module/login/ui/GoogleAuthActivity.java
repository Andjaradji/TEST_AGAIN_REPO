package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.presenter.IGoogleAuthPresenter;
import com.vexanium.vexgift.module.login.presenter.IGoogleAuthPresenterImpl;
import com.vexanium.vexgift.module.login.view.IGoogleAuthView;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_google_auth, withLoadingAnim = true)
public class GoogleAuthActivity extends BaseActivity<IGoogleAuthPresenter> implements IGoogleAuthView {

    @Override
    protected void initView() {
        mPresenter = new IGoogleAuthPresenterImpl(this);
        findViewById(R.id.login_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.login_button:
                doCheckToken();
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {

        } else if (errorResponse != null) {
            KLog.v("GoogleAuthActivity handleResult error : " + errorResponse.getMeta().getMessage());
            toast(errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
            if (errorResponse.getMeta().getStatus() != 200) {
                new VexDialog.Builder(GoogleAuthActivity.this)
                        .optionType(DialogOptionType.OK)
                        .okText("OK")
                        .title("Error")
                        .content(errorResponse.getMeta().getMessage())
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                ((EditText) findViewById(R.id.et_pin)).setText("");
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        } else {
            TpUtil tpUtil = new TpUtil(App.getContext());
            tpUtil.put(TpUtil.KEY_GOOGLE2FA_LOCK, false);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();

        }
    }

    private void doCheckToken() {
        String token = ((EditText) findViewById(R.id.et_pin)).getText().toString();
        User user = User.getCurrentUser(this);
        boolean isValid = true;
        int id = -1;

        if (user != null) {
            id = user.getId();
        } else {
            isValid = false;
        }

        if (TextUtils.isEmpty(token)) {
            ((EditText) findViewById(R.id.et_pin)).setError(getString(R.string.validate_empty_field));
            isValid = false;
        }

        if (isValid) {
            mPresenter.checkToken(id, token);
        }
    }
}
