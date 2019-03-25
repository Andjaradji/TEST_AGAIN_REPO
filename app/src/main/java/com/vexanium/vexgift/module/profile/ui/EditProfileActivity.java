package com.vexanium.vexgift.module.profile.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenter;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenterImpl;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_edit_profile, toolbarTitle = R.string.edit_profile_toolbar_title)
public class EditProfileActivity extends BaseActivity<IProfilePresenter> implements IProfileView, OnClickListener {

    TextInputEditText etName;
    String currentEtName;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IProfilePresenterImpl(this);

        etName = findViewById(R.id.et_myprofile_name);

        if (user.getName() != null) {
            etName.setText(user.getName());
        }

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentEtName = etName.getText().toString();
            }
        });

        ViewUtil.setOnClickListener(this, this, R.id.btn_save);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (currentEtName.length() > 0 && currentEtName.length() <= 25) {
                    mPresenter.updateUserProfile(user.getId(), currentEtName);
                } else {
                    StaticGroup.showCommonErrorDialog(this, getString(R.string.editprofile_save_error_length));
                }
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("EditProfileActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {

        } else if (errorResponse != null) {
            KLog.v("EditProfileActivity handleResult error " + errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
            if (errorResponse.getMeta() != null && errorResponse.getMeta().isRequestError()) {
                StaticGroup.showCommonErrorDialog(this, errorResponse.getMeta().getMessage());
            }
        } else {
            user.setName(currentEtName);
            User.updateCurrentUser(this, user);
            new VexDialog.Builder(this)
                    .title(getString(R.string.editprofile_success_title))
                    .content(getString(R.string.editprofile_success_body))
                    .optionType(DialogOptionType.OK)
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            Intent intent = new Intent();
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    })
                    .autoDismiss(false)
                    .show();
        }
    }
}
