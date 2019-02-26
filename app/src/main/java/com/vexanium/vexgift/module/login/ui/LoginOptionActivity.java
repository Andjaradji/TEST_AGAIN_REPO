package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.module.login.presenter.ILoginPresenter;
import com.vexanium.vexgift.module.login.presenter.ILoginPresenterImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.register.ui.RegisterActivity;
import com.vexanium.vexgift.module.register.ui.RegisterConfirmationActivity;
import com.vexanium.vexgift.module.register.ui.RegisterPhoneConfirmationActivity;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import static com.vexanium.vexgift.app.ConstantGroup.SIGN_IN_REQUEST_CODE;

@ActivityFragmentInject(contentViewId = R.layout.activity_login_option, withLoadingAnim = true)
public class LoginOptionActivity extends BaseActivity<ILoginPresenter> implements ILoginView {

    int currentCountdown = 8;
    String url;
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private GoogleApiClient googleApiClient;
    private String refCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
        }

    }

    @Override
    protected void initView() {
        if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");
            KLog.v("LoginActivity", "initView: getDeeplink " + url);
            getIntent().removeExtra("url");
        }

        mPresenter = new ILoginPresenterImpl(this);

        fbLoginButton = findViewById(R.id.login_fb_button);

        findViewById(R.id.login_fake_fb_button).setOnClickListener(this);
        findViewById(R.id.login_google_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.register_button).setOnClickListener(this);

//        ((EditText) findViewById(R.id.et_email)).setText("asd@asd.asd");
//        ((EditText) findViewById(R.id.et_pass)).setText("asdasd");

        String referralCode = StaticGroup.checkReferrerData();
        if (!TextUtils.isEmpty(referralCode)) {
            refCode = referralCode;
        }

//        checkAppVersion();
        initialize();
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("LoginActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {
            if (data instanceof UserLoginResponse) {
                UserLoginResponse response = (UserLoginResponse) data;
                if (response.user != null && (response.user.getEmailConfirmationStatus() || (response.user.getFacebookId() != null || response.user.getGoogleToken() != null))) {
                    StaticGroup.removeReferrerData();

                    StaticGroup.userSession = response.user.getSessionKey();
                    StaticGroup.isPasswordSet = response.isPasswordSet;

                    User.setIsPasswordSet(this.getApplicationContext(), response.isPasswordSet);
                    User.updateCurrentUser(this.getApplicationContext(), response.user);

                    User.google2faLock(response.user);

                    executeMain(false);
                } else if (response.user != null && !response.user.getEmailConfirmationStatus() && !TextUtils.isEmpty(response.user.getEmail())) {
                    StaticGroup.userSession = response.user.getSessionKey();
                    Intent intent = new Intent(LoginOptionActivity.this, RegisterConfirmationActivity.class);
                    intent.putExtra("user", JsonUtil.toString(response.user));
                    startActivity(intent);
                }else if (response.user != null && !response.user.getPhoneConfirmationStatus() && !TextUtils.isEmpty(response.user.getPhoneNumber())) {
                    StaticGroup.userSession = response.user.getSessionKey();
                    Intent intent = new Intent(LoginOptionActivity.this, RegisterPhoneConfirmationActivity.class);
                    intent.putExtra("user", JsonUtil.toString(response.user));
                    startActivity(intent);
                }
            }

        } else if (errorResponse != null) {
            KLog.v("LoginOptionActivity handleResult error : " + errorResponse.getMeta().getMessage());
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_fake_fb_button:
                if (StaticGroup.isReferralActive()) {
                    View view = View.inflate(this, R.layout.include_g2fa_get_voucher, null);
                    final EditText etPin = view.findViewById(R.id.et_pin);
                    etPin.setHint(getString(R.string.referral_code_field_dialog_et));
                    etPin.setInputType(InputType.TYPE_CLASS_TEXT);
                    if (!TextUtils.isEmpty(refCode)) {
                        etPin.setText(refCode);
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
            case R.id.login_button:
                intent = new Intent(this, LoginActivity.class);
                LoginOptionActivity.this.startActivity(intent);
                break;
            case R.id.register_button:
                intent = new Intent(this, RegisterActivity.class);
                LoginOptionActivity.this.startActivity(intent);
                break;
            case R.id.login_forgot_button:
                intent = new Intent(LoginOptionActivity.this, ForgotPasswordActivity.class);
                LoginOptionActivity.this.startActivity(intent);
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
            hideProgress();
            handleGoogleSignInResult(result);
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
                        if (!TextUtils.isEmpty(referralCode)) {
                            user.setReferralCode(referralCode);
                        }

                        mPresenter.requestLogin(user);
                    } else {
                        StaticGroup.showCommonErrorDialog(this, result.getStatus().getStatusCode());
                    }
            }
            KLog.v("Google Signin success : " + result.getStatus().getStatusCode() + " " + result.getStatus().getStatusMessage());
        } else {
            KLog.v("Google Signin error : " + result.getStatus().getStatusCode() + " " + result.getStatus().getStatusMessage());
            StaticGroup.showCommonErrorDialog(this, result.getStatus().getStatusCode());
        }
    }

    public void doLogin() {
        User user = new User();
        String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
        String pass = ((EditText) findViewById(R.id.et_pass)).getText().toString();

        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_email, R.id.et_pass);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !Patterns.PHONE.matcher(email).matches()) {
            ((EditText) findViewById(R.id.et_email)).setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        if (isValid) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                user.setEmail(email);
            } else {
                user.setPhoneNumber(email);
            }
            user.setPassword(pass);
            mPresenter.requestLogin(user);
        }
    }

//    private void generateKeyHash() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                KLog.v("LoginOptionActivity", "Key hash : %s", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private boolean checkLoginInfo() {
        User user = User.getCurrentUser(this.getApplicationContext());

        if (user != null) {
            if (!TextUtils.isEmpty(user.getFacebookAccessToken())) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    Date now = new Date();
                    if (accessToken.getExpires() != null && accessToken.getExpires().getTime() < now.getTime()) {
                        AccessToken.refreshCurrentAccessTokenAsync();
                    }
                }
            }
            return true;
        } else {
            return false;
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
            if (url != null) {
                intent.putExtra("url", url);
            }
            startActivity(intent);
        }

        finish();
    }

    private void requestGoogleLogin() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        showProgress();
        startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
    }

    private void requestFacebookLogin() {
        KLog.v("LoginOptionActivity", "request Facebook Login");
        showProgress();
        fbLoginButton.performClick();
    }

    private void initialize() {
        KLog.v("LoginOptionActivity", "Initialize");
        if (checkLoginInfo()) {
            executeMain(true);
        } else {
//            generateKeyHash();

            initFacebook();
            initGoogle();
        }
    }

    private void initGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(ConstantGroup.GOOGLE_CLIENT_ID)
                .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//                toast("Google Error : " + connectionResult.getErrorMessage());
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
    }

    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(StaticGroup.getFacebookPermission());
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                KLog.v("LoginOptionActivity", "FacebookCallback onSuccess");
                if (loginResult != null) {
                    final AccessToken accessToken = loginResult.getAccessToken();
                    if (loginResult.getRecentlyDeniedPermissions() != null &&
                            loginResult.getRecentlyDeniedPermissions().size() > 0) {

                        KLog.v("LoginOptionActivity", "access denied");
                        LoginManager.getInstance().logOut();
                        hideProgress();

                        requestFacebookLogin();
                    } else {

                        KLog.v("LoginOptionActivity", "access accept");
                        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response != null && response.getJSONObject() != null) {
                                    KLog.v("LoginOptionActivity", "GraphRequest onCompleted : " + response.getRawResponse());

                                    JSONObject userInfo = response.getJSONObject();

                                    User facebookUserInfo = User.createWithFacebook(userInfo);

                                    String referralCode = StaticGroup.checkReferrerData();
                                    if (!TextUtils.isEmpty(referralCode)) {
                                        facebookUserInfo.setReferralCode(referralCode);
                                    }

                                    hideProgress();
                                    mPresenter.requestLogin(facebookUserInfo);

                                } else {
                                    hideProgress();
                                    if (response != null && response.getError() != null) {
                                        KLog.v("LoginOptionActivity", "GraphRequest getError : " + response.getError().toString());
                                        StaticGroup.showCommonErrorDialog(LoginOptionActivity.this, response.getError().getErrorCode());
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
                KLog.v("LoginOptionActivity", "FacebookCallback onCancel");
                hideProgress();
            }

            @Override
            public void onError(FacebookException error) {
                hideProgress();

                KLog.v("LoginOptionActivity", "FacebookCallback onError");
                if (error != null) {
                    KLog.v("LoginOptionActivity", "FacebookCallback onError : " + error.getMessage());
                }
            }
        });
    }
}
