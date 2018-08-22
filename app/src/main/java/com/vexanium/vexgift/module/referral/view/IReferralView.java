package com.vexanium.vexgift.module.referral.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface IReferralView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}

