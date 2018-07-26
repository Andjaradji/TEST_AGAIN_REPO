package com.vexanium.vexgift.module.security.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IGoogleAuthStatePresenter extends BasePresenter {
    void updateGoogle2faState(int id, String key, String token, boolean isSetToEnable);
}
