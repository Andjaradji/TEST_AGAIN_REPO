package com.vexanium.vexgift.base;

import android.support.annotation.CallSuper;

import com.socks.library.KLog;
import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscriber;

/**
 * Created by mac on 11/16/17.
 */

public class BaseSubscriber<T> extends Subscriber<T> {
    private RequestCallback<T> mRequestCallback;

    public BaseSubscriber(RequestCallback<T> requestCallback) {
        mRequestCallback = requestCallback;
    }

    @CallSuper
    @Override
    public void onStart() {
        KLog.v("BaseSubscriber onStart");
        super.onStart();
        if (mRequestCallback != null) {
            mRequestCallback.beforeRequest();
        }
    }

    @CallSuper
    @Override
    public void onCompleted() {
        KLog.v("BaseSubscriber onCompleted");
        if (mRequestCallback != null) {
            mRequestCallback.requestComplete();
        }
    }

    @CallSuper
    @Override
    public void onError(Throwable e) {
        KLog.v("BaseSubscriber onError");
        KLog.v("BaseSubscriber onError"+ " "+e.getMessage());
        e.printStackTrace();

//        try {
//            if (mRequestCallback != null) {
//                try {
//                    if (e instanceof ApiException) {
//                        ApiException error = (ApiException) e;
//                        HttpResponse httpResponse = new HttpResponse();
//                        httpResponse.setMsg(error.getMessage());
//
//                        mRequestCallback.requestError(httpResponse);
//                    } else {
//                        RetrofitException error = (RetrofitException) e;
//                        HttpResponse response;
//                        if (!TextUtils.isEmpty(error.getMessage()) && error.getMessage().equalsIgnoreCase("timeout")) {
//                            response = new HttpResponse();
//                            response.setMsg(App.getContext().getString(R.string.net_error_title));
//                            Map<String, String> notifyInfo = new HashMap<>();
//                            notifyInfo.put("msg", App.getContext().getString(R.string.net_error_title));
//                            response.setNotify(notifyInfo);
//                        } else {
//                            response = error.getErrorBodyAs(HttpResponse.class);
//                            if (response != null) {
//                                KLog.v("onError response : [" + response.getCode() + "] " + response.getMsg() + " / " + response.getData());
//                                switch (response.getCode()) {
//
//                                }
//                            }
//                        }
//
//                        mRequestCallback.requestError(response);
//                    }
//                } catch (Exception ex) {
//                    HttpResponse httpResponse = new HttpResponse();
//                    httpResponse.setMsg(App.getContext().getString(R.string.net_error_title));
//                    mRequestCallback.requestError(httpResponse);
//                    e.printStackTrace();
//                    ex.printStackTrace();
//                }
//
//            }
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
    }

    @CallSuper
    @Override
    public void onNext(T t) {
        KLog.v("BaseSubscriber onNext");
        if (mRequestCallback != null) {
            mRequestCallback.requestSuccess(t);
        }
    }
}
