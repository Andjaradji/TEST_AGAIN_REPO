package com.vexanium.vexgift.bean.response;

public class HttpResponse<T> {
    private MetaResponse<T> meta;

    public MetaResponse<T> getMeta() {
        return meta;
    }

    public void setMeta(MetaResponse<T> meta) {
        this.meta = meta;
    }
}

