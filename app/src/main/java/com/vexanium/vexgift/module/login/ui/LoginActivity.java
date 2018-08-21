package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.ConstantGroup.SIGN_IN_REQUEST_CODE;

@ActivityFragmentInject(contentViewId = R.layout.activity_login, withLoadingAnim = true)
public class LoginActivity extends BaseActivity<ILoginPresenter> implements ILoginView {

    int currentCountdown = 5;
    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private GoogleApiClient googleApiClient;

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
        mPresenter = new ILoginPresenterImpl(this);

        fbLoginButton = findViewById(R.id.login_fb_button);

        findViewById(R.id.login_fake_fb_button).setOnClickListener(this);
        findViewById(R.id.login_google_button).setOnClickListener(this);
        findViewById(R.id.login_signup_button).setOnClickListener(this);
        findViewById(R.id.login_forgot_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

//        ((EditText) findViewById(R.id.et_email)).setText("asd@asd.asd");
//        ((EditText) findViewById(R.id.et_pass)).setText("asdasd");

        checkAppVersion();
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("LoginActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {
            UserLoginResponse response = (UserLoginResponse) data;
// TODO: 17/08/18 remove true 
            if (response.user != null && (response.user.getEmailConfirmationStatus() || (response.user.getFacebookId() != null || response.user.getGoogleToken() != null))) {
                StaticGroup.removeReferrerData();

                StaticGroup.userSession = response.user.getSessionKey();
                StaticGroup.isPasswordSet = response.isPasswordSet;

                User.setIsPasswordSet(this.getApplicationContext(), response.isPasswordSet);
                User.updateCurrentUser(this.getApplicationContext(), response.user);

                User.google2faLock(response.user);

                executeMain(false);
            } else if (response.user != null && !response.user.getEmailConfirmationStatus()) {
                StaticGroup.userSession = response.user.getSessionKey();
                Intent intent = new Intent(LoginActivity.this, RegisterConfirmationActivity.class);
                intent.putExtra("user", JsonUtil.toString(response.user));
                startActivity(intent);
            }

        } else if (errorResponse != null) {
            KLog.v("LoginActivity handleResult error : " + errorResponse.getMeta().getMessage());
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_fake_fb_button:
                requestFacebookLogin();
                break;
            case R.id.login_google_button:
                requestGoogleLogin();
                break;
            case R.id.login_button:
                doLogin();
                break;
            case R.id.login_signup_button:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LoginActivity.this.startActivity(intent);
                break;
            case R.id.login_forgot_button:
                intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                LoginActivity.this.startActivity(intent);
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
            KLog.v("Google Signin error : " + result.getStatus().getStatusCode() + " " + result.getStatus().getStatus().getStatusMessage());
            StaticGroup.showCommonErrorDialog(this, result.getStatus().getStatusCode());
        }
    }

    public void doLogin() {
        User user = new User();
        String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
        String pass = ((EditText) findViewById(R.id.et_pass)).getText().toString();

        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_email, R.id.et_pass);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ((EditText) findViewById(R.id.et_email)).setError("This is not valid email");
            isValid = false;
        }

        if (isValid) {
            user.setEmail(email);
            user.setPassword(pass);
            mPresenter.requestLogin(user);
        }
    }

    private void checkAppVersion() {
        boolean isNeedUpdate = false;
        boolean isNeedForceUpdate = false;

        if (isNeedUpdate) {
            findViewById(R.id.ll_need_update).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_login).setVisibility(View.GONE);
        } else {
            initialize();
        }
    }

    private void startCoundownTimer() {
        currentCountdown = 5;
        Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        String msg = String.format(getString(R.string.appversion_need_update_goto_gp), currentCountdown);
                        ViewUtil.setText(LoginActivity.this, R.id.tv_goto, msg);

                        if (currentCountdown > 0) {
                            currentCountdown--;
                        } else {
                            StaticGroup.openVexgiftGooglePlay(LoginActivity.this);
                            finish();
                            throw new NullPointerException();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                    }
                });
    }

//    private void generateKeyHash() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                KLog.v("LoginActivity", "Key hash : %s", Base64.encodeToString(md.digest(), Base64.DEFAULT));
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
        KLog.v("LoginActivity", "request Facebook Login");
        showProgress();
        fbLoginButton.performClick();
    }

    private void initialize() {
        KLog.v("LoginActivity", "Initialize");
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
                                    if (!TextUtils.isEmpty(referralCode)) {
                                        facebookUserInfo.setReferralCode(referralCode);
                                    }

                                    hideProgress();
                                    mPresenter.requestLogin(facebookUserInfo);

                                } else {
                                    hideProgress();
                                    if (response != null && response.getError() != null) {
                                        KLog.v("LoginActivity", "GraphRequest getError : " + response.getError().toString());
                                        StaticGroup.showCommonErrorDialog(LoginActivity.this, response.getError().getErrorCode());
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
                }
            }
        });
    }
}
