package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SortFilterCondition implements Serializable{

    @JsonProperty("sort")
    String sort;
    @JsonProperty("category")
    List<String> category;
    @JsonProperty("type")
    List<String> type;
    @JsonProperty("location")
    List<String> location;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<String> getCategory() {
        if (category == null)
            return new ArrayList<>();
        else
            return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<String> getType() {
        if (type == null)
            return new ArrayList<>();
        else
            return type;
    }

    public void setType(List<String> type) {
        this.type = type;
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
