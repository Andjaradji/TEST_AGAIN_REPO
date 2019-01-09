package com.vexanium.vexgift.module.misc.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IDigifinexEmailPresenter extends BasePresenter {

    void requestSubmitDigifinexEmailReferral(int userId, String email);

    void requestUserDifinexEmailReferred(int userId);
}
