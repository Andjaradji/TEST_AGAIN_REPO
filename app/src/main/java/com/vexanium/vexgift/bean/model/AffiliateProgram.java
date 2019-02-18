package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AffiliateProgram extends BaseType {
    @JsonProperty("title")
    private String title;
    @JsonProperty("image")
    private String image;
    @JsonProperty("short_description")
    private String shortDesc;
    @JsonProperty("long_description")
    private String longDesc;
    @JsonProperty("privacy_policy")
    private String privacyPolicy;
    @JsonProperty("link")
    private String link;
    @JsonProperty("valid_from")
    private int validFrom;
    @JsonProperty("valid_until")
    private int validUntil;
    @JsonProperty("input_options")
    private String inputOptions;
    @JsonProperty("limit_per_user")
    private int limitPerUser;
    @JsonProperty("is_active")
    private boolean isActive;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(int validFrom) {
        this.validFrom = validFrom;
    }

    public int getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(int validUntil) {
        this.validUntil = validUntil;
    }

    public String getInputOptions() {
        return inputOptions;
    }

    public void setInputOptions(String inputOptions) {
        this.inputOptions = inputOptions;
    }

    public int getLimitPerUser() {
        return limitPerUser;
    }

    public void setLimitPerUser(int limitPerUser) {
        this.limitPerUser = limitPerUser;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStrValidFrom() {
        long l = TimeUnit.SECONDS.toMillis(validFrom);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMMM dd, yyyy") + "  HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public String getStrValidUntil() {
        long l = TimeUnit.SECONDS.toMillis(validUntil);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMMM dd, yyyy") + "  HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
