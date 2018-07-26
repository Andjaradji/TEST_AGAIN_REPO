package com.vexanium.vexgift.module.login.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface IGoogleAuthView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}

