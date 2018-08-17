package com.vexanium.vexgift.module.register.ui;

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

@ActivityFragmentInject(contentViewId = R.layout.activity_register_confirmation, withLoadingAnim = true)
public class RegisterConfirmationActivity extends BaseActivity<IRegisterPresenter> implements IRegisterView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

        ViewUtil.setOnClickListener(this, this,
                R.id.resend_button,
                R.id.confirmation_button);

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            
        } else if (errorResponse != null) {

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resend_button:

                break;
            case R.id.confirmation_button:

                break;
            default:
        }
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}
