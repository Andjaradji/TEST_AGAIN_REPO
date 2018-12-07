package com.vexanium.vexgift.bean.model;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.app.StaticGroup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LuckyDraw implements Serializable {

    @JsonProperty("id")
    private int id;
//    @JsonProperty("vendor_id")
//    private String vendorId;
    @JsonProperty("name")
    private String title;
    @JsonProperty("short_description")
    private String decription;
    @JsonProperty("long_description")
    private String longDecription;
    @JsonProperty("term_and_condition")
    private String termsAndCond;
    @JsonProperty("lucky_draw_category_id")
    private int categoryId;
    @JsonProperty("payment_type_id")
    private int paymentTypeId;
    @JsonProperty("member_type_id")
    private int memberTypeId;
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
    @JsonProperty("total_purchased")
    private int totalPurchased;
    @JsonProperty("min_ticket")
    private int minTicket;
    @JsonProperty("max_ticket")
    private int maxTicket;
    @JsonProperty("limit_per_user")
    private int limitPerUser;
    @JsonProperty("loyalty_point_required")
    private int loyaltyPointRequired;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("lucky_draw_category")
    private LuckyDrawCategory luckyDrawCategory;
    /*
     * Free
     * Pay with Vex Point
     *
     */
    @JsonProperty("payment_type")
    private PaymentType paymentType;

    /*
     * All
     * Premium
     * Non Premium*/
    @JsonProperty("member_type")
    private MemberType memberType;

    @JsonProperty("vendor")
    private Vendor vendor;

    @JsonProperty("lucky_draw_winners")
    private ArrayList<LuckyDrawWinner> luckyDrawWinners;

    private int userPurchasedTotal = -1;

    public int getUserPurchasedTotal() {
        return userPurchasedTotal;
    }

    public void setUserPurchasedTotal(int userPurchasedTotal) {
        this.userPurchasedTotal = userPurchasedTotal;
    }


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
        long l = TimeUnit.SECONDS.toMillis(validUntil);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMMM dd, yyyy") + "  HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public String getCreatedDate() {
        long l = TimeUnit.SECONDS.toMillis(validFrom);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        String sDate = (StaticGroup.isInIDLocale() ? "dd MMM yyyy" : "MMM dd yyyy") + "  HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(sDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }


    public long getValidUntil() {
//        Random random = new Random();
//        if (System.currentTimeMillis() > validUntil) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, random.nextInt(30));
//            validUntil = calendar.getTimeInMillis();
//        }
        return TimeUnit.SECONDS.toMillis(validUntil);
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

    public int getLimitPerUser() {
        return limitPerUser;
    }

    public void setLimitPerUser(int limitPerUser) {
        this.limitPerUser = limitPerUser;
    }

    public boolean isForAllMember() {
        if (memberType == null || TextUtils.isEmpty(memberType.getName())) {
            return false;
        } else {
            return memberType.getName().equalsIgnoreCase("All");
        }
    }

    public boolean isForPremium() {
        if (memberType == null || TextUtils.isEmpty(memberType.getName())) {
            return false;
        } else {
            return memberType.getName().equalsIgnoreCase("Premium");
        }
    }

    public int getLoyaltyPointRequired() {
        return loyaltyPointRequired;
    }

    public void setLoyaltyPointRequired(int loyaltyPointRequired) {
        this.loyaltyPointRequired = loyaltyPointRequired;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public int getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(int memberTypeId) {
        this.memberTypeId = memberTypeId;
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

    public int getTotalPurchased() {
        return totalPurchased;
    }

    public void setTotalPurchased(int totalPurchased) {
        this.totalPurchased = totalPurchased;
    }

    public int getMinTicket() {
        return minTicket;
    }

    public void setMinTicket(int minTicket) {
        this.minTicket = minTicket;
    }

    public int getMaxTicket() {
        return maxTicket;
    }

    public void setMaxTicket(int maxTicket) {
        this.maxTicket = maxTicket;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LuckyDrawCategory getLuckyDrawCategory() {
        return luckyDrawCategory;
    }

    public void setLuckyDrawCategory(LuckyDrawCategory luckyDrawCategory) {
        this.luckyDrawCategory = luckyDrawCategory;
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

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public ArrayList<LuckyDrawWinner> getLuckyDrawWinners() {
        return luckyDrawWinners;
    }

    public void setLuckyDrawWinners(ArrayList<LuckyDrawWinner> luckyDrawWinners) {
        this.luckyDrawWinners = luckyDrawWinners;
    }
}
