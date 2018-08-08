package com.vexanium.vexgift.module.voucher.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface IVoucherView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}

