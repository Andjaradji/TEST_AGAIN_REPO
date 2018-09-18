package com.vexanium.vexgift.module.deposit.ui;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_deposit_list, withLoadingAnim = true, toolbarTitle = R.string.deposit_title)
public class DepositListActivity extends BaseActivity {

    FrameLayout mFlFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

        mFlFragmentContainer = findViewById(R.id.fl_fragment_container);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mFlFragmentContainer.getId(), Deposit1Fragment.newInstance()).commit();

    }
}
