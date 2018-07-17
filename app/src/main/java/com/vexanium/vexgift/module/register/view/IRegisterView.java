package com.vexanium.vexgift.module.register.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface IRegisterView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}

