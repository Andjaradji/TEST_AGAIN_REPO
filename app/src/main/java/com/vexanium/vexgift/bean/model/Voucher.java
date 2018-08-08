package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Voucher implements Serializable {
    @JsonProperty("id")
    private String id;
    @JsonProperty("category_id")
    private String categoryId;
    @JsonProperty("vendor_id")
    private String vendorId;

    @JsonProperty("name")
    private String title;
    @JsonProperty("short_description")
    private String decription;
    @JsonProperty("long_description")
    private String longDecription;
    @JsonProperty("term_and_condition")
    private String termsAndCond;

    @JsonProperty("payment_type")
    private String paymentType;
    @JsonProperty("voucher_type")
    private String voucherType;

    @JsonProperty("price")
    private int price;

    @JsonProperty("thumbnail")
    private String thumbnail;
    @JsonProperty("images")
    private String photo;

    @JsonProperty("valid_from")
    private long validFrom;
    @JsonProperty("valid_until")
    private long validUntil;

    @JsonProperty("country")
    private String country;
    @JsonProperty("region")
    private String region;

    @JsonProperty("lat")
    private String lat;
    @JsonProperty("lng")
    private String lang;

    @JsonProperty("quantity_available")
    private int qtyAvailable;
    @JsonProperty("quantity_left")
    private int qtyLeft;

    @JsonProperty("limit_per_user")
    private int limitPerUser;

    @JsonProperty("category")
    private Category category;
    @JsonProperty("vendor")
    private Vendor vendor;

    public boolean active = true;

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

    public String getExpiredDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(validUntil);
        String sDate = (StaticGroup.isInIDLocale()?"dd MMM yyyy":"MMM dd yyyy") + "  hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());

        return dateFormat.format(calendar.getTime());
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getLongDecription() {
        return longDecription;
    }

    public void setLongDecription(String longDecription) {
        this.longDecription = longDecription;
    }

    public String getTermsAndCond() {
        return termsAndCond;
    }

    public void setTermsAndCond(String termsAndCond) {
        this.termsAndCond = termsAndCond;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public long getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(long validUntil) {
        this.validUntil = validUntil;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getQtyAvailable() {
        return qtyAvailable;
    }

    public int getQtyTotal() {
        return qtyAvailable + qtyLeft;
    }

    public void setQtyAvailable(int qtyAvailable) {
        this.qtyAvailable = qtyAvailable;
    }

    public int getQtyLeft() {
        return qtyLeft;
    }

    public void setQtyLeft(int qtyLeft) {
        this.qtyLeft = qtyLeft;
    }

    public int getLimitPerUser() {
        return limitPerUser;
    }

    public void setLimitPerUser(int limitPerUser) {
        this.limitPerUser = limitPerUser;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
