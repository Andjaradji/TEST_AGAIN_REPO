package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.User;

import java.io.Serializable;


/**
 * Created by hizkia on 11/29/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginResponse implements Serializable {
    @JsonProperty("user")
    public User user;
    @JsonProperty("is_password_set")
    public Boolean isPasswordSet = true;

}
