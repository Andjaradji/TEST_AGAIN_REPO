package com.vexanium.vexgift.module.vexpoint.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenter;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenterImpl;
import com.vexanium.vexgift.module.vexpoint.view.IVexpointView;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_vex_address, toolbarTitle = R.string.vexpoint_fill_address)
public class VexAddressActivity extends BaseActivity<IVexpointPresenter> implements IVexpointView {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IVexpointPresenterImpl(this);

        user = User.getCurrentUser(this);

        updateView();

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_submit:
                doSubmitAddress();
                break;
        }
    }

    private void doSubmitAddress() {
        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_vex_address, R.id.et_ga_pin);
        if(isValid){
            String address = ((EditText) findViewById(R.id.et_vex_address)).getText().toString();
            String token = ((EditText) findViewById(R.id.et_ga_pin)).getText().toString();
            mPresenter.requestSetActAddress(user.getId(), address, token);
        }
        handleVexAddress();
    }

    private void handleVexAddress() {
        User.setVexAddressSubmitTime();
        updateView();
    }

    private void updateView() {
        if (User.isVexAddVerifTimeEnded()) {
            findViewById(R.id.ll_fill_address).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_wait_address).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ll_fill_address).setVisibility(View.GONE);
            findViewById(R.id.rl_wait_address).setVisibility(View.VISIBLE);
        }
    }
}
