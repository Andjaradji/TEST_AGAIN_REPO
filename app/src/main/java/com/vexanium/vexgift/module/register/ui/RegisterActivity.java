package com.vexanium.vexgift.module.register.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rilixtech.CountryCodePicker;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.module.login.ui.GoogleAuthActivity;
import com.vexanium.vexgift.module.login.ui.LoginActivity;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.register.presenter.IRegisterPresenter;
import com.vexanium.vexgift.module.register.presenter.IRegisterPresenterImpl;
import com.vexanium.vexgift.module.register.view.IRegisterView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import org.json.JSONObject;

import java.io.Serializable;

import static com.vexanium.vexgift.app.ConstantGroup.SIGN_IN_REQUEST_CODE;

@ActivityFragmentInject(contentViewId = R.layout.activity_register, withLoadingAnim = true)
public class RegisterActivity extends BaseActivity<IRegisterPresenter> implements IRegisterView {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private GoogleApiClient googleApiClient;
    private CountryCodePicker countryCodePicker;
    private String refCode = "";
    private static boolean LOGIN_BY_EMAIL = true;
    private User loginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IRegisterPresenterImpl(this);

        fbLoginButton = findViewById(R.id.login_fb_button);
        countryCodePicker = findViewById(R.id.ccp_phone);

        ViewUtil.setOnClickListener(this, this,
                R.id.login_fake_fb_button,
                R.id.register_login_button,
                R.id.register_button,
                R.id.login_google_button,
                R.id.ll_phone_number,
                R.id.ll_email);

        if (!StaticGroup.isReferralActive()) {
            findViewById(R.id.ll_referral_field).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ll_referral_field).setVisibility(View.VISIBLE);
        }
        String referralCode = StaticGroup.checkReferrerData();
        if (!TextUtils.isEmpty(referralCode)) {
            refCode = referralCode;
            ((EditText) findViewById(R.id.et_referral_code)).setText(refCode);
        }

        updateViewRegisterBy();

        initialize();

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("RegisterActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {
            if (data instanceof UserLoginResponse) {
                StaticGroup.removeReferrerData();

                UserLoginResponse response = (UserLoginResponse) data;

                if (response.user != null) {
                    User.updateCurrentUser(this.getApplicationContext(), response.user);
                }

                executeMain(false);
            }
        } else if (errorResponse != null) {
            KLog.v("RegisterActivity handleResult error " + errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
            if (errorResponse.getMeta() != null && errorResponse.getMeta().isRequestError()) {
                StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
            } else if (errorResponse.getMeta() != null && errorResponse.getMeta().getStatus() == 200) {
                if(loginUser!=null) {
                    KLog.v("RegisterActivity", "onClick: HPtes user bypass 0");
                }
                new VexDialog.Builder(RegisterActivity.this)
                        .optionType(DialogOptionType.OK)
                        .okText("Login Now")
                        .title("Register Success")
                        .content("Your account has been registered. Login now")
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                if(loginUser!=null) {
                                    KLog.v("RegisterActivity","onClick: HPtes user bypass 1");
                                    intent.putExtra("user", JsonUtil.toString(loginUser));
                                }
                                RegisterActivity.this.startActivity(intent);
                            }
                        })
                        .autoDismiss(true)
                        .show();
            } else if (errorResponse.getMeta() != null) {
                StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getStatus());
            }

        } else {
            new VexDialog.Builder(RegisterActivity.this)
                    .optionType(DialogOptionType.OK)
                    .okText("Login Now")
                    .title("Register Success")
                    .content("Your account has been registered. Login now")
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            RegisterActivity.this.startActivity(intent);
                        }
                    })
                    .autoDismiss(true)
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                doRegister();
                break;
            case R.id.ll_phone_number:
                LOGIN_BY_EMAIL = false;
                updateViewRegisterBy();
                break;
            case R.id.ll_email:
                LOGIN_BY_EMAIL = true;
                updateViewRegisterBy();
                break;
            case R.id.login_fake_fb_button:
                if (StaticGroup.isReferralActive()) {
                    View view = View.inflate(this, R.layout.include_g2fa_get_voucher, null);
                    final EditText etPin = view.findViewById(R.id.et_pin);
                    etPin.setHint(getString(R.string.referral_code_field_dialog_et));
                    etPin.setInputType(InputType.TYPE_CLASS_TEXT);
                    if (!TextUtils.isEmpty(refCode)) {
                        etPin.setText(refCode);
                    } else if (!TextUtils.isEmpty(((EditText) findViewById(R.id.et_referral_code)).getText().toString())) {
                        etPin.setText(((EditText) findViewById(R.id.et_referral_code)).getText().toString());
                    }

                    new VexDialog.Builder(this)
                            .optionType(DialogOptionType.YES_NO)
                            .title(getString(R.string.referral_code_field_dialog_title))
                            .content(getString(R.string.referral_code_field_dialog_desc))
                            .addCustomView(view)
                            .positiveText(getString(R.string.ok))
                            .negativeText(getString(R.string.dialog_cancel))
                            .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                @Override
                                public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    if (refCode == null) {
                                        refCode = "";
                                    }
                                    if (!refCode.equalsIgnoreCase(etPin.getText().toString())) {
                                        refCode = etPin.getText().toString();
                                    }
                                    requestFacebookLogin();
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

                } else {
                    requestFacebookLogin();
                }
                break;
            case R.id.login_google_button:
                if (StaticGroup.isReferralActive()) {
                    View view = View.inflate(this, R.layout.include_g2fa_get_voucher, null);
                    final EditText etPin = view.findViewById(R.id.et_pin);
                    etPin.setHint(getString(R.string.referral_code_field_dialog_et));
                    etPin.setInputType(InputType.TYPE_CLASS_TEXT);
                    if (!TextUtils.isEmpty(refCode)) {
                        etPin.setText(refCode);
                    } else if (!TextUtils.isEmpty(((EditText) findViewById(R.id.et_referral_code)).getText().toString())) {
                        etPin.setText(((EditText) findViewById(R.id.et_referral_code)).getText().toString());
                    }

                    new VexDialog.Builder(this)
                            .optionType(DialogOptionType.YES_NO)
                            .title(getString(R.string.referral_code_field_dialog_title))
                            .content(getString(R.string.referral_code_field_dialog_desc))
                            .addCustomView(view)
                            .positiveText(getString(R.string.ok))
                            .negativeText(getString(R.string.dialog_cancel))
                            .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                @Override
                                public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    if (refCode == null) {
                                        refCode = "";
                                    }
                                    if (!refCode.equalsIgnoreCase(etPin.getText().toString())) {
                                        refCode = etPin.getText().toString();
                                    }
                                    requestGoogleLogin();
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

                } else {
                    requestGoogleLogin();
                }
                break;
            case R.id.register_login_button:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                RegisterActivity.this.startActivity(intent);
                break;
            default:
        }
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
            hideProgress();
        } else if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                User user = User.createWithGoogle(account);
                if (user != null)
                    if (user.getGoogleToken() != null && !TextUtils.isEmpty(user.getGoogleToken())) {

                        String referralCode = StaticGroup.checkReferrerData();
                        String rc = refCode;
                        if (!TextUtils.isEmpty(rc)) {
                            user.setReferralCode(rc);
                        } else if (!TextUtils.isEmpty(referralCode)) {
                            user.setReferralCode(referralCode);
                        }

                        mPresenter.requestLogin(user);
                    } else {
                        StaticGroup.showCommonErrorDialog(this, result.getStatus().getStatusCode());
                    }
            }
            KLog.v("Google Signin success : " + result.getStatus().getStatusCode() + " " + result.getStatus().getStatusMessage());
        } else {
            KLog.v("Google Signin error : " + result.getStatus().getStatusCode() + " " + result.getStatus().getStatus().getStatusMessage());
            StaticGroup.showCommonErrorDialog(this, result.getStatus().getStatusCode());
        }
    }

    private void updateViewRegisterBy() {
        TextView tvEmail = findViewById(R.id.tv_email);
        TextView tvPhone = findViewById(R.id.tv_phone_number);
        tvEmail.setTextColor(ContextCompat.getColor(this, !LOGIN_BY_EMAIL ? R.color.material_black_sub_text_color : R.color.material_black_text_color));
        tvPhone.setTextColor(ContextCompat.getColor(this, LOGIN_BY_EMAIL ? R.color.material_black_sub_text_color : R.color.material_black_text_color));

        App.setTextViewStyle(LOGIN_BY_EMAIL ? App.bold : App.regular, tvEmail);
        App.setTextViewStyle(!LOGIN_BY_EMAIL ? App.bold : App.regular, tvPhone);

        findViewById(R.id.ll_email_field).setVisibility(LOGIN_BY_EMAIL ? View.VISIBLE : View.GONE);
        findViewById(R.id.ll_phone_number_field).setVisibility(!LOGIN_BY_EMAIL ? View.VISIBLE : View.GONE);
    }

    public void doRegister() {
        User user = new User();
        String name = ((EditText) findViewById(R.id.et_username)).getText().toString();
        String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
        String phone = ((EditText) findViewById(R.id.et_phone)).getText().toString();
        String pass = ((EditText) findViewById(R.id.et_password)).getText().toString();
        String repass = ((EditText) findViewById(R.id.et_repassword)).getText().toString();
        String rc = ((EditText) findViewById(R.id.et_referral_code)).getText().toString();

        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_username, R.id.et_password, R.id.et_repassword);

        if (isValid) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && LOGIN_BY_EMAIL) {
                ((EditText) findViewById(R.id.et_email)).setError(getString(R.string.error_email_invalid));
                isValid = false;
            }

            if (!TextUtils.isDigitsOnly(phone) && !LOGIN_BY_EMAIL) {
                ((EditText) findViewById(R.id.et_phone)).setError(getString(R.string.error_phone_invalid));
                isValid = false;
            }

            if (isValid && !repass.equals(pass)) {
                ((EditText) findViewById(R.id.et_repassword)).setError("Password doesn't matches");
                isValid = false;
            }
        }

        if (isValid) {
            user.setName(name);
            if (LOGIN_BY_EMAIL) {
                user.setEmail(email);
            } else {
                user.setPhoneNumber(countryCodePicker.getFullNumberWithPlus() + phone);
            }
            user.setPassword(pass);

            String referralCode = StaticGroup.checkReferrerData();
            if (!TextUtils.isEmpty(rc)) {
                user.setReferralCode(rc);
            } else if (!TextUtils.isEmpty(referralCode)) {
                user.setReferralCode(referralCode);
            }

            loginUser = user;
            mPresenter.requestRegister(user);
        }
    }


    private void executeMain(boolean isAlreadyLogin) {
        User user = User.getCurrentUser(this);
        if ((User.isLocalSessionEnded() || User.isGoogle2faLocked() || !isAlreadyLogin) && user.isAuthenticatorEnable()) {
            Intent intent = new Intent(getApplicationContext(), GoogleAuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        finish();
    }

    private void requestGoogleLogin() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
        showProgress();
    }

    private void requestFacebookLogin() {
        KLog.v("RegisterActivity", "request Facebook Login");
        showProgress();
        fbLoginButton.performClick();
    }

    private void initialize() {
        KLog.v("RegisterActivity", "Initialize");
        initFacebook();
        initGoogle();
    }

    private void initGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(ConstantGroup.GOOGLE_CLIENT_ID)
                .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                toast("Google Error : " + connectionResult.getErrorMessage());
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
    }

    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(StaticGroup.getFacebookPermission());
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                KLog.v("LoginActivity", "FacebookCallback onSuccess");
                if (loginResult != null) {
                    final AccessToken accessToken = loginResult.getAccessToken();
                    if (loginResult.getRecentlyDeniedPermissions() != null &&
                            loginResult.getRecentlyDeniedPermissions().size() > 0) {

                        KLog.v("LoginActivity", "access denied");
                        LoginManager.getInstance().logOut();
                        hideProgress();

                        requestFacebookLogin();
                    } else {

                        KLog.v("LoginActivity", "access accept");
                        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response != null && response.getJSONObject() != null) {
                                    KLog.v("LoginActivity", "GraphRequest onCompleted : " + response.getRawResponse());

                                    JSONObject userInfo = response.getJSONObject();

                                    User facebookUserInfo = User.createWithFacebook(userInfo);

                                    String referralCode = StaticGroup.checkReferrerData();
                                    String rc = refCode;
                                    if (!TextUtils.isEmpty(rc)) {
                                        facebookUserInfo.setReferralCode(rc);
                                    } else if (!TextUtils.isEmpty(referralCode)) {
                                        facebookUserInfo.setReferralCode(referralCode);
                                    }


                                    hideProgress();
                                    mPresenter.requestLogin(facebookUserInfo);
                                } else {
                                    hideProgress();
                                    if (response != null && response.getError() != null) {
                                        KLog.v("LoginActivity", "GraphRequest getError : " + response.getError().toString());
                                        StaticGroup.showCommonErrorDialog(RegisterActivity.this, response.getError().getErrorCode());
                                    }
                                }
                            }
                        });
                        request.setParameters(StaticGroup.getFacebookFields());
                        request.executeAsync();
                    }
                } else {
                    hideProgress();
                }
            }

            @Override
            public void onCancel() {
                KLog.v("LoginActivity", "FacebookCallback onCancel");
                hideProgress();
            }

            @Override
            public void onError(FacebookException error) {
                hideProgress();

                KLog.v("LoginActivity", "FacebookCallback onError");
                if (error != null) {
                    KLog.v("LoginActivity", "FacebookCallback onError : " + error.getMessage());
                    StaticGroup.showCommonErrorDialog(RegisterActivity.this, error.getMessage());
                }
            }
        });
    }


}
