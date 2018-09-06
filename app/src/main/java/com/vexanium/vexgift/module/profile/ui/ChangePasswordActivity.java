package com.vexanium.vexgift.module.profile.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenter;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenterImpl;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_change_password, toolbarTitle = R.string.change_password_toolbar_title)
public class ChangePasswordActivity extends BaseActivity<IProfilePresenter> implements IProfileView {
    User user;
    boolean isPassSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IProfilePresenterImpl(this);
        user = User.getCurrentUser(this);
        isPassSet = User.getIsPasswordSet(this);

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
                doChangePass();
            }
        });
        findViewById(R.id.btn_addpass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                doAddPass();

            }
        });
    }

    private void doAddPass() {
        String newPass = ((TextInputEditText) findViewById(R.id.et_password)).getText().toString();
        String newPassConf = ((TextInputEditText) findViewById(R.id.et_confirm_password)).getText().toString();

        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_password,  R.id.et_confirm_password);
        if (isValid) {
            if (!newPass.equalsIgnoreCase(newPassConf)) {
                isValid = false;
            }
        }

        if (isValid)
            mPresenter.changePass(user.getId(), "", newPass);
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
        if (data != null) {

        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }else{
            if(!isPassSet) {
                User.setIsPasswordSet(this,true);
                new VexDialog.Builder(this)
                        .optionType(DialogOptionType.OK)
                        .okText("OK")
                        .title("Add Password Success")
                        .content("Your new password has been added")
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                if (ClickUtil.isFastDoubleClick()) return;
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .cancelable(false)
                        .show();
            }else{
                new VexDialog.Builder(this)
                        .optionType(DialogOptionType.OK)
                        .okText("OK")
                        .title("Change Password Success")
                        .content("Your password has been changed")
                        .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                if (ClickUtil.isFastDoubleClick()) return;
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        }

    }
}
