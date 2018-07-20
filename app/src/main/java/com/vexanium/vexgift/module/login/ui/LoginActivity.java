package com.vexanium.vexgift.module.login.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
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
import com.socks.library.KLog;
import com.vexanium.vexgift.BuildConfig;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.fixture.FixtureData;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.module.login.presenter.ILoginPresenter;
import com.vexanium.vexgift.module.login.presenter.ILoginPresenterImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.register.ui.RegisterActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

@ActivityFragmentInject(contentViewId = R.layout.activity_login)
public class LoginActivity extends BaseActivity<ILoginPresenter> implements ILoginView {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new ILoginPresenterImpl(this);

        fbLoginButton = (LoginButton)findViewById(R.id.login_fb_button);

        findViewById(R.id.login_fake_fb_button).setOnClickListener(this);
        findViewById(R.id.login_signup_button).setOnClickListener(this);

        initialize();

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("LoginActivity handleResult : "+ JsonUtil.toString(data));
        if (data != null) {
            UserLoginResponse response = (UserLoginResponse) data;
            String session = "";
            if(!TextUtils.isEmpty(response.session)){
                session = response.session;
            }

            String uid = "";
            if (response.user != null ) {
                if (!TextUtils.isEmpty(response.user.getId()) ){
                    uid = response.user.getId();
                }

                User.updateCurrentUser(this.getApplicationContext(), response.user);
            }

//            StaticGroup.currentUser = FixtureData.getRandomUser();
//            StaticGroup.currentUser.setUser(response.user);

            hideProgress();
            executeMain(false);
        }else if(errorResponse != null){
            hideProgress();
            KLog.v("LoginActivity handleResult error : "+errorResponse.getMsg());
            toast(errorResponse.getCode()+" : "+ errorResponse.getMsg());
        }
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) return;
        switch (v.getId()) {
            case R.id.login_fake_fb_button:
                requestFacebookLogin();
                break;
            case R.id.login_button:
                doLogin();
                break;
            case R.id.login_signup_button:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LoginActivity.this.startActivity(intent);
//                LoginActivity.this.finish();
                break;
            default:
        }
        super.onClick(v);
    }

    public void doLogin(){
        User user = new User();
        String email = ((EditText)findViewById(R.id.et_email)).getText().toString();
        String pass = ((EditText)findViewById(R.id.et_pass)).getText().toString();
        user.setEmail(email);
        user.setPassword(pass);

        mPresenter.requestLogin(user);
    }

    private void generateKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                KLog.v("LoginActivity","Key hash : %s", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean checkLoginInfo() {
        User user  = User.getCurrentUser(this.getApplicationContext());

        // TODO: 19/07/18 check facebook access token

//        return user!=null;
        return true;
    }

    private void executeMain(boolean isAlreadyLogin) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (!isAlreadyLogin) {
            intent.putExtra("is_create_user", true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        finish();
    }

    private void requestGoogleLogin(){
//        StaticGroup.currentUser = FixtureData.getRandomUser();

        hideProgress();
        executeMain(false);
    }

    private void requestFacebookLogin() {
        KLog.v("LoginActivity","request Facebook Login");
        showProgress();
        fbLoginButton.performClick();
    }

    private void initialize() {
        KLog.v("LoginActivity","Initialize");
        if (checkLoginInfo()) {
            executeMain(true);
        } else {
            generateKeyHash();

            callbackManager = CallbackManager.Factory.create();
            fbLoginButton.setReadPermissions(getFacebookPermission());
            fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    KLog.v("LoginActivity","FacebookCallback onSuccess");
                    if (loginResult != null) {
                        final AccessToken accessToken = loginResult.getAccessToken();
                        if (loginResult.getRecentlyDeniedPermissions() != null &&
                                loginResult.getRecentlyDeniedPermissions().size() > 0) {

                            KLog.v("LoginActivity","access denied");
                            LoginManager.getInstance().logOut();
                            hideProgress();

                            requestFacebookLogin();
                        } else {

                            KLog.v("LoginActivity","access accept");
                            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    if (response != null && response.getJSONObject() != null) {
                                        KLog.v("LoginActivity","GraphRequest onCompleted : " + response.getRawResponse());

                                        JSONObject userInfo = response.getJSONObject();

                                        User facebookUserInfo = User.createWithFacebook(userInfo);

                                        KLog.json("HPtes",JsonUtil.toString(facebookUserInfo));

//                                        StaticGroup.currentUser = FixtureData.getRandomUser();
//                                        StaticGroup.currentUser.setUser(facebookUserInfo);

                                        hideProgress();
                                        executeMain(false);
//                                        mPresenter.requestSignUp(facebookUserInfo);

                                    } else {
                                        hideProgress();
                                        if (response.getError() != null) {
                                            KLog.v("LoginActivity","GraphRequest getError : " + response.getError().toString());
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
                    KLog.v("LoginActivity","FacebookCallback onCancel");
                    hideProgress();
                }

                @Override
                public void onError(FacebookException error) {
                    hideProgress();

                    KLog.v("LoginActivity","FacebookCallback onError");
                    if (error != null) {
                        KLog.v("LoginActivity","FacebookCallback onError : " + error.getMessage());
                    }
                }
            });
        }
    }

    private Bundle getFacebookFields() {
        Bundle parameters = new Bundle();
        StringBuilder parameterStr = new StringBuilder();
        parameterStr.append("about,");
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
        //parameterStr.append("photos.limit(6){webp_images},");
        parameterStr.append("timezone,");
        parameterStr.append("updated_time,");
        // Add Permission
        parameterStr.append("birthday,");
        parameterStr.append("education,");
        parameterStr.append("albums{photos.limit(6){webp_images},name},");
        parameterStr.append("friends,");
        parameterStr.append("religion,");
        parameterStr.append("location,");
        parameterStr.append("relationship_status,");
        parameterStr.append("verified");

        parameters.putString("fields", parameterStr.toString());

        return parameters;
    }

    private List<String> getFacebookPermission() {
        return Arrays.asList(
                "email",
                "public_profile",
                "user_about_me",
                "user_birthday",
                "user_education_history",
                "user_friends",
                "user_location",
                "user_photos",
                "user_relationships",
                "user_religion_politics"
        );
    }
}
