package com.vexanium.vexgift.module.wallet.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface IWalletView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}

