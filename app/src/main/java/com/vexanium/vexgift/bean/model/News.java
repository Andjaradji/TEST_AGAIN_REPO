package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class News extends BaseType {

    @JsonProperty("site_link")
    private String siteLink;
    @JsonProperty("site_logo")
    private String siteLogo;
    @JsonProperty("javascript")
    private String javascript;
    @JsonProperty("image")
    private String image;
    @JsonProperty("is_published")
    private boolean isPublished;
    @JsonProperty("priority")
    private int priority;

    public String getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(String siteLink) {
        this.siteLink = siteLink;
    }

    public String getSiteLogo() {
        return siteLogo;
    }

    public void setSiteLogo(String siteLogo) {
        this.siteLogo = siteLogo;
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
