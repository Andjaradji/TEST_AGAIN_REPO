package com.vexanium.vexgift.module.profile.ui;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenter;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenterImpl;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_change_password, toolbarTitle = R.string.change_password_toolbar_title)
public class ChangePasswordActivity extends BaseActivity<IProfilePresenter> implements IProfileView {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IProfilePresenterImpl(this);
        user = User.getCurrentUser(this);
        final boolean isPassSet = User.getIsPasswordSet(this);

        if (isPassSet) {
            ViewUtil.setText(this, R.id.tv_toolbar_title, getString(R.string.change_password_title));
            findViewById(R.id.ll_addpass).setVisibility(View.GONE);
            findViewById(R.id.ll_changepass).setVisibility(View.VISIBLE);
        } else {
            ViewUtil.setText(this, R.id.tv_toolbar_title, getString(R.string.add_password_toolbar_title));
            findViewById(R.id.ll_addpass).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_changepass).setVisibility(View.GONE);
        }

        findViewById(R.id.btn_changepass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                if (isPassSet) {
                    doChangePass();
                } else {
                    doAddPass();
                }
            }
        });
    }

    private void doAddPass() {
        String newPass = ((TextInputEditText) findViewById(R.id.et_new_password)).getText().toString();
        String newPassConf = ((TextInputEditText) findViewById(R.id.et_confirm_new_password)).getText().toString();

        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_new_password, R.id.et_last_password, R.id.et_confirm_new_password);
        if (isValid) {
            if (!newPass.equalsIgnoreCase(newPassConf)) {
                isValid = false;
            }
        }

        if (isValid)
            mPresenter.changePass(user.getId(),"", newPass);
    }

    private void doChangePass() {
        String oldPass = ((TextInputEditText) findViewById(R.id.et_last_password)).getText().toString();
        String newPass = ((TextInputEditText) findViewById(R.id.et_new_password)).getText().toString();
        String newPassConf = ((TextInputEditText) findViewById(R.id.et_confirm_new_password)).getText().toString();

        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_new_password, R.id.et_last_password, R.id.et_confirm_new_password);

        if (isValid) {
            if (!newPass.equalsIgnoreCase(newPassConf)) {
                isValid = false;
            }
        }

        if (isValid)
            mPresenter.changePass(user.getId(), oldPass, newPass);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

    }
}
