package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.socks.library.KLog;
import com.vexanium.vexgift.BuildConfig;
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
import com.vexanium.vexgift.util.JsonUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.vexanium.vexgift.app.ConstantGroup.SIGN_IN_REQUEST_CODE;

@ActivityFragmentInject(contentViewId = R.layout.activity_login)
public class LoginActivity extends BaseActivity<ILoginPresenter> implements ILoginView {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new ILoginPresenterImpl(this);

        fbLoginButton = (LoginButton) findViewById(R.id.login_fb_button);

        findViewById(R.id.login_fake_fb_button).setOnClickListener(this);
        findViewById(R.id.login_google_button).setOnClickListener(this);
        findViewById(R.id.login_signup_button).setOnClickListener(this);
        findViewById(R.id.login_forgot_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        ((EditText) findViewById(R.id.et_email)).setText("asd@asd.com");
        ((EditText) findViewById(R.id.et_pass)).setText("asdasd");

        initialize();

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("LoginActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {
            UserLoginResponse response = (UserLoginResponse) data;

            if (response.user != null) {
                String session = response.user.getSessionKey();
                StaticGroup.userSession = session;

                User.updateCurrentUser(this.getApplicationContext(), response.user);
            }

            executeMain(false);
        } else if (errorResponse != null) {
            hideProgress();
            KLog.v("LoginActivity handleResult error : " + errorResponse.getMeta().getMessage());
            toast(errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
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
            handleGoogleSignInResult(result);
        } else if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            KLog.json("HPtes google", JsonUtil.toString(account));
            User user = User.createWithGoogle(account);
            if (user != null)
                if (user.getGoogleToken() != null && !TextUtils.isEmpty(user.getGoogleToken())) {

                    mPresenter.requestLogin(user);
                } else {
                    toast("Error, GID null");
                }
            KLog.v("Google Signin success : " + result.getStatus().getStatusCode() + " " + result.getStatus().getStatusMessage());
        } else {
            KLog.v("Google Signin error : " + result.getStatus().getStatusCode() + " " + result.getStatus().getStatus().getStatusMessage());
        }
    }

    public void doLogin() {
        User user = new User();
        String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
        String pass = ((EditText) findViewById(R.id.et_pass)).getText().toString();

        boolean isValid = true;
        if (TextUtils.isEmpty(email)) {
            ((EditText) findViewById(R.id.et_email)).setError("This Field can't be empty");
            isValid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ((EditText) findViewById(R.id.et_email)).setError("This is not valid email");
            isValid = false;
        }
        if (TextUtils.isEmpty(pass)) {
            ((EditText) findViewById(R.id.et_pass)).setError("This Field can't be empty");
            isValid = false;
        }

        if (isValid) {
            user.setEmail(email);
            user.setPassword(pass);

            mPresenter.requestLogin(user);
        }
    }

    private void generateKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                KLog.v("LoginActivity", "Key hash : %s", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        finish();
    }

    private void requestGoogleLogin() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
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
            generateKeyHash();

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
        fbLoginButton.setReadPermissions(getFacebookPermission());
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

                                    hideProgress();
                                    mPresenter.requestLogin(facebookUserInfo);

                                } else {
                                    hideProgress();
                                    if (response.getError() != null) {
                                        KLog.v("LoginActivity", "GraphRequest getError : " + response.getError().toString());
                                    }
                                }
                            }
                        });
                        request.setParameters(getFacebookFields());
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

    private Bundle getFacebookFields() {
        Bundle parameters = new Bundle();
        StringBuilder parameterStr = new StringBuilder();
        parameterStr.append("email,");
        parameterStr.append("id,");
        parameterStr.append("cover,");
        parameterStr.append("name,");
        parameterStr.append("first_name,");
        parameterStr.append("last_name,");
        parameterStr.append("age_range,");
        parameterStr.append("link,");
        parameterStr.append("gender,");
        parameterStr.append("locale,");
        parameterStr.append("picture.type(large),");
        parameterStr.append("timezone,");
        parameterStr.append("updated_time,");
        parameterStr.append("birthday,");
        parameterStr.append("albums{photos.limit(6){webp_images},name},");
        parameterStr.append("friends,");
        parameterStr.append("location");
        parameters.putString("fields", parameterStr.toString());

        return parameters;
    }

    private List<String> getFacebookPermission() {
        return Arrays.asList(
                "email",
                "public_profile",
                "user_birthday",
                "user_friends",
                "user_location",
                "user_photos"
        );
    }
}
