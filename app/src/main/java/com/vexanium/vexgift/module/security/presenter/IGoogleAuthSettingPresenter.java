package com.vexanium.vexgift.module.security.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IGoogleAuthSettingPresenter extends BasePresenter {
    void requestCode(int id);
}
