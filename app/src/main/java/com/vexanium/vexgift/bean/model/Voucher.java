package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Voucher implements Serializable {
    public boolean isRedeemed = false;
    public boolean isOnline = false;
    public long redeemedTime;

    @JsonProperty("id")
    private int id;
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
    private PaymentType paymentType;

    /*
     * Free
     * Pay with Vex Point
     *
     */

    @JsonProperty("member_type")
    private MemberType memberType;

    /*
     * All
     * Premium
     * Non Premium*/

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

    @JsonProperty("is_multiple_allowed")
    private boolean isMultipleAllowed;

    @JsonProperty("category")
    private Category category;
    @JsonProperty("vendor")
    private Vendor vendor;

    public boolean active = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMMM dd, yyyy") + "  hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());

        return dateFormat.format(calendar.getTime());
    }

    public String getCreatedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(validFrom);
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMM dd yyyy") + "  hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());

        return dateFormat.format(calendar.getTime());
    }


    public String getRedeemedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(redeemedTime);
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMMM dd, yyyy") + "  hh:mm";
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

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public boolean isPremium() {
        if (memberType == null) return false;
        else
            return memberType.getId() == 2;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
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
        Random random = new Random();
        if (System.currentTimeMillis() > validUntil) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, random.nextInt(30));
            validUntil = calendar.getTimeInMillis();
        }
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

    public boolean isMultipleAllowed() {
        return isMultipleAllowed;
    }

    public void setMultipleAllowed(boolean multipleAllowed) {
        isMultipleAllowed = multipleAllowed;
    }
}
