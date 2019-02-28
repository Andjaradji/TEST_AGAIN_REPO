package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.dev21.fingerprintassistant.FingerprintAuthListener;
import com.dev21.fingerprintassistant.FingerprintHelper;
import com.dev21.fingerprintassistant.FingerprintResultsHandler;
import com.dev21.fingerprintassistant.ResponseCode;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.login.presenter.IGoogleAuthPresenter;
import com.vexanium.vexgift.module.login.presenter.IGoogleAuthPresenterImpl;
import com.vexanium.vexgift.module.login.view.IGoogleAuthView;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

import static com.vexanium.vexgift.app.ConstantGroup.SUPPORT_EMAIL;

@ActivityFragmentInject(contentViewId = R.layout.activity_google_auth, withLoadingAnim = true)
public class GoogleAuthActivity extends BaseActivity<IGoogleAuthPresenter> implements IGoogleAuthView, FingerprintAuthListener {

    FingerprintHelper fingerprintHelper;
    FingerprintResultsHandler fingerprintResultsHandler;
    VexDialog vexDialog;
    private User user;
    private boolean isFingerprintActive = false;

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IGoogleAuthPresenterImpl(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.tv_ask).setOnClickListener(this);
        findViewById(R.id.tv_signout).setOnClickListener(this);

        String account = String.format(getString(R.string.google2fa_account), user.getEmail());
        Spanned text = Html.fromHtml(account);
        ViewUtil.setText(this, R.id.tv_account, text);

        TpUtil tpUtil = new TpUtil(this);
        isFingerprintActive = tpUtil.getBoolean(TpUtil.KEY_FINGERPRINT_LOCK, false);

        boolean isAlreadyLogin = false;
        if (getIntent().hasExtra("isAlreadyLogin")) {
            isAlreadyLogin = getIntent().getBooleanExtra("isAlreadyLogin", false);
            KLog.v("GoogleAuthActivity", "initView: HMtes isAlreadyLogin " + isAlreadyLogin);
        }

        if (isAlreadyLogin) {
            checkFingerprint();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.tv_ask:
                String subject = String.format("[LOGIN] #%04d", user.getId());
                String message = "Hi Vexgift Support!\nI've problem with...";
                StaticGroup.shareWithEmail(this, SUPPORT_EMAIL, subject, message);
                break;
            case R.id.tv_signout:
                doLogout();
                break;
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
//            toast(errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
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

    @Override
    public void onAuthentication(int helpCode, CharSequence charSequence, FingerprintManager.AuthenticationResult authenticationResult, int authCode) {
        switch (authCode) {
            case ResponseCode.AUTH_ERROR:
                // Show appropriate message
                toast("Error");
                break;
            case ResponseCode.AUTH_FAILED:
                // Show appropriate message
                toast(getString(R.string.premium_purchase_failed));
                break;
            case ResponseCode.AUTH_HELP:
                // Show appropriate message
                toast("HELP " + helpCode);

                break;
            case ResponseCode.AUTH_SUCCESS:
                vexDialog.dismiss();
                TpUtil tpUtil = new TpUtil(App.getContext());
                tpUtil.put(TpUtil.KEY_GOOGLE2FA_LOCK, false);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                toast("Success");

                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void checkFingerprint() {
        if (isFingerprintActive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintHelper = new FingerprintHelper(this, "vexgift-fingerprint-setting");
                fingerprintHelper.checkAndEnableFingerPrintService();
                fingerprintResultsHandler = new FingerprintResultsHandler(this);
                fingerprintResultsHandler.setFingerprintAuthListener(this);
                fingerprintResultsHandler.startListening(fingerprintHelper.getFingerprintManager(), fingerprintHelper.getCryptoObject());

                showProgress();

                View view = View.inflate(this, R.layout.include_fingerprint_dialog, null);
                vexDialog = new VexDialog.Builder(GoogleAuthActivity.this)
                        .optionType(DialogOptionType.OK)
                        .okText(getString(R.string.cancel))
                        .title(getString(R.string.security_fingerprint_dialog_title))
                        .addCustomView(view)
                        .content(getString(R.string.security_fingerprint_dialog_detail))
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (fingerprintResultsHandler != null) {
                                        fingerprintResultsHandler.stopListening();
                                    }
                                }
                                hideProgress();
                            }
                        })
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .autoDismiss(false)
                        .show();
            }
        }
    }

    private void doLogout() {
        new VexDialog.Builder(this)
                .title(getString(R.string.logout_title))
                .content(getString(R.string.logout_desc))
                .optionType(DialogOptionType.YES_NO)
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        StaticGroup.logOutClear(GoogleAuthActivity.this, 0);
                    }
                })
                .autoDismiss(true).show();

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
