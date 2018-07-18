package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by hizkia on 12/03/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Brand implements Serializable {
    @JsonProperty("id")
    private String id;
    @JsonProperty("photo")
    private String photo;
    @JsonProperty("title")
    private String title;
    @JsonProperty("desc")
    private String decription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public Brand(String photo, String title) {
        this.photo = photo;
        this.title = title;
    }
}
