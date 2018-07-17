package com.vexanium.vexgift.module.home.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface IHomeView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}
