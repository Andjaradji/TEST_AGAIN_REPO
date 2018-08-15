package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SortFilterCondition implements Serializable{

    @JsonProperty("sort")
    String sort;
    @JsonProperty("payment")
    List<String> payment;
    @JsonProperty("member")
    List<String> member;
    @JsonProperty("location")
    List<String> location;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<String> getMemberType() {
        if (member == null)
            return new ArrayList<>();
        else
            return member;
    }

    public void setMemberType(List<String> member) {
        this.member = member;
    }

    public List<String> getPaymentType() {
        if (payment == null)
            return new ArrayList<>();
        else
            return payment;
    }

    public void setPaymentType(List<String> type) {
        this.payment = type;
    }

    public List<String> getLocation() {
        if (location == null)
            return new ArrayList<>();
        else
            return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }
}
