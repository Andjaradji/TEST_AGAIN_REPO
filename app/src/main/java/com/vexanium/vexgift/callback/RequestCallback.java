package com.vexanium.vexgift.callback;


import com.vexanium.vexgift.bean.response.HttpResponse;

/**
 * Created by mac on 10/5/17.
 */

public interface RequestCallback<T> {
    void beforeRequest();
    void requestError(HttpResponse response);
    void requestComplete();
    void requestSuccess(T data);
}
