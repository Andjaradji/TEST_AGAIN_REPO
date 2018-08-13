package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.UserAddress;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAddressResponse implements Serializable {
    @JsonProperty("user_address")
    private UserAddress userAddress;

    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
    }
}