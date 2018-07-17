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

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.socks.library.KLog;
import com.vexanium.vexgift.BuildConfig;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.module.login.presenter.ILoginPresenter;
import com.vexanium.vexgift.module.login.presenter.ILoginPresenterImpl;
import com.vexanium.vexgift.module.login.view.ILoginView;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;

import java.io.Serializable;
import java.security.MessageDigest;

@ActivityFragmentInject(contentViewId = R.layout.activity_login)
public class LoginActivity extends BaseActivity<ILoginPresenter> implements ILoginView {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new ILoginPresenterImpl(this);

        fbLoginButton = (LoginButton) findViewById(R.id.login_fb_button);

        findViewById(R.id.login_fake_fb_button).setOnClickListener(this);

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
}
