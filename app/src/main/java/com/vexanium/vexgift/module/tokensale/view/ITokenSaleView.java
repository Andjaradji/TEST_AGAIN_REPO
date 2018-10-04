package com.vexanium.vexgift.module.tokensale.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface ITokenSaleView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}
