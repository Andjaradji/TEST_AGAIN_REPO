package com.vexanium.vexgift.module.deposit.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenter;
import com.vexanium.vexgift.module.deposit.presenter.IDepositPresenterImpl;
import com.vexanium.vexgift.module.deposit.view.IDepositView;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_token_freeze, withLoadingAnim = true, toolbarTitle = R.string.token_freeze_title)
public class TokenFreezeActivity extends BaseActivity<IDepositPresenter> implements IDepositView {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IDepositPresenterImpl(this);

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {

    }
}
