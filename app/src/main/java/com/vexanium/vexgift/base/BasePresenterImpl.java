package com.vexanium.vexgift.base;

import android.text.TextUtils;

import com.socks.library.KLog;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by mac on 11/16/17.
 */

public class BasePresenterImpl<T extends BaseView, V> implements BasePresenter, RequestCallback<V> {
    protected CompositeSubscription compositeSubscription = new CompositeSubscription();
    protected T mView;

    public BasePresenterImpl(T view) {
        mView = view;
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onDestroy() {
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }

        mView = null;
    }


    @Override
    public void beforeRequest() {
        if (mView != null) {
            mView.showProgress();
        }
    }

    @Override
    public void requestError(HttpResponse response) {
        //KLog.e(msg);
        KLog.v("HPtes requestError " + JsonUtil.toString(response));
        if (response != null && response.getMeta().getError() != null) {
            Map<String, String> notifyInfo = (HashMap) response.getMeta().getError();
            if (notifyInfo != null && !TextUtils.isEmpty(notifyInfo.get("msg")) && mView != null) {
//                mView.toast(notifyInfo.get("msg"));
            }
        }
        if (mView != null) {
            mView.hideProgress();
        }
    }

    @Override
    public void requestComplete() {
        if (mView != null) {
            mView.hideProgress();
        }
    }

    @Override
    public void requestSuccess(V data) {

    }
}
