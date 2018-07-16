package com.vexanium.vexgift.bean.response;

public class HttpResponse<T> {
    private int code;
    private String msg;
    private T data;
    private T notify;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getNotify() {
        return notify;
    }

    public void setNotify(T notify) {
        this.notify = notify;
    }
}

