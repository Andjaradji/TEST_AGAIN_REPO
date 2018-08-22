package com.vexanium.vexgift.module.referral.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IReferralPresenter extends BasePresenter {

    void requestUserReferral(int userId);
}
