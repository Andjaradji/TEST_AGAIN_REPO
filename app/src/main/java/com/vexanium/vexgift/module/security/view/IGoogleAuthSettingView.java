package com.vexanium.vexgift.module.security.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface IGoogleAuthSettingView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}

