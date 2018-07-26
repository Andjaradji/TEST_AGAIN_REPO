package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Google2faResponse implements Serializable{
    @JsonProperty("authenticator_code")
    private String authenticationCode;

    @JsonProperty("authenticator_url")
    private String authenticationUrl;

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public String getAuthenticationUrl() {
        return authenticationUrl;
    }

    public void setAuthenticationUrl(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }
}

