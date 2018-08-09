package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by hizkia on 12/03/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements Serializable {
    public static final String TYPE_EXPIRED = "exp";
    public static final String TYPE_EXPIRED_SOON = "exp_soon";
    public static final String TYPE_GET_SUCCESS = "get_success";

    @JsonProperty("id")
    private String id;
    @JsonProperty("voucher")
    private Voucher voucher;
    @JsonProperty("type")
    private String type;
    @JsonProperty("time")
    private long time;
    @JsonProperty("new")
    private boolean isNew;
    @JsonProperty("url")
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}