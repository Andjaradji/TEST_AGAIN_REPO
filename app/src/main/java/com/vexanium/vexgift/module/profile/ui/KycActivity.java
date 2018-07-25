package com.vexanium.vexgift.module.profile.ui;

import android.os.Bundle;
import android.view.View;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenter;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.JsonUtil;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_kyc, toolbarTitle = R.string.myprofile_kyc)
public class KycActivity extends BaseActivity<IProfilePresenter> implements IProfileView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("KycActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {

        } else if (errorResponse != null) {
            KLog.v("MyProfileActivity handleResult error : " + errorResponse.getMeta().getMessage());
            toast(errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_next:
                break;
        }
    }

    private void doSubmitKyc(){
        Kyc kyc = new Kyc();
        User user = User.getCurrentUser(App.getContext());
        if(user == null) return;

        kyc.setId(user.getId());
//        kyc.setIdName();
//        kyc.setIdType();
    }
}
