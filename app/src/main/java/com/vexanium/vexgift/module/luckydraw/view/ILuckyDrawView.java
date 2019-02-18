package com.vexanium.vexgift.module.luckydraw.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface ILuckyDrawView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}

