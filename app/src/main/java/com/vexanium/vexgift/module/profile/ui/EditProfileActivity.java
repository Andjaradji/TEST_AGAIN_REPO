package com.vexanium.vexgift.module.profile.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.base.BaseActivity;

@ActivityFragmentInject(contentViewId = R.layout.activity_edit_profile, toolbarTitle = R.string.edit_profile_toolbar_title)
public class EditProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {

    }
}
