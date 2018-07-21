package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class VexPointRecord implements Serializable{
    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("desc")
    private String description;

    @JsonProperty("type")

    private int type;
    @JsonProperty("amount")
    private int amount;

    public boolean active = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public VexPointRecord(String title, String description, int type, int amount) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.amount = amount;
    }
}
