package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PremiumPlan implements Serializable{
    @JsonProperty("id")
    private String id;

    @JsonProperty("price")
    private int price;

    @JsonProperty("day")
    private int day;

    public boolean active = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }



    public PremiumPlan(int price, int day) {
        this.price = price;
        this.day = day;
    }
}
