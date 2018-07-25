package com.vexanium.vexgift.module.register.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.module.login.ui.LoginActivity;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.register.presenter.IRegisterPresenter;
import com.vexanium.vexgift.module.register.presenter.IRegisterPresenterImpl;
import com.vexanium.vexgift.module.register.view.IRegisterView;
import com.vexanium.vexgift.util.JsonUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

@ActivityFragmentInject(contentViewId = R.layout.activity_register)
public class RegisterActivity extends BaseActivity<IRegisterPresenter> implements IRegisterView {

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
        mPresenter = new IRegisterPresenterImpl(this);

        fbLoginButton = (LoginButton)findViewById(R.id.login_fb_button);

        findViewById(R.id.login_fake_fb_button).setOnClickListener(this);
        findViewById(R.id.register_login_button).setOnClickListener(this);

        initialize();

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("RegisterActivity handleResult : "+ JsonUtil.toString(data));
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
            KLog.v("LoginActivity handleResult error : "+errorResponse.getMeta().getMessage());
            toast(errorResponse.getMeta().getStatus()+" : "+ errorResponse.getMeta().getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_fake_fb_button:
                requestFacebookLogin();
                break;
            case R.id.register_login_button:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                RegisterActivity.this.startActivity(intent);
//                LoginActivity.this.finish();
                break;
            default:
        }
        super.onClick(v);
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
        return false;
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
