package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by mac on 11/20/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingCondition implements Serializable {

    @JsonProperty("is_authenticator_enable")
    private Boolean isGoogle2faEnable;
}
