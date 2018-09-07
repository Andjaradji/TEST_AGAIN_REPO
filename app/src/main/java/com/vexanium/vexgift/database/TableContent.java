package com.vexanium.vexgift.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(indexes = {
        @Index(value = "id DESC", unique = true)
})
public class TableContent {
    @Id
    private long id;
    private String vouchers;
    private String tokens;
    private String notifs;
    private String myBoxs;
    private String voucherTypes;
    private String paymentTypes;
    private String memberTypes;
    private String categories;
    private String bestVoucher;
    private String featuredVoucher;
    private long createdTime;
    private long updatedTime;


    @Generated(hash = 1568511860)
    public TableContent(long id, String vouchers, String tokens, String notifs,
                        String myBoxs, String voucherTypes, String paymentTypes,
                        String memberTypes, String categories, String bestVoucher,
                        String featuredVoucher, long createdTime, long updatedTime) {
        this.id = id;
        this.vouchers = vouchers;
        this.tokens = tokens;
        this.notifs = notifs;
        this.myBoxs = myBoxs;
        this.voucherTypes = voucherTypes;
        this.paymentTypes = paymentTypes;
        this.memberTypes = memberTypes;
        this.categories = categories;
        this.bestVoucher = bestVoucher;
        this.featuredVoucher = featuredVoucher;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    @Generated(hash = 1207901501)
    public TableContent() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVouchers() {
        return vouchers;
    }

    public void setVouchers(String vouchers) {
        this.vouchers = vouchers;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public String getNotifs() {
        return notifs;
    }

    public void setNotifs(String notifs) {
        this.notifs = notifs;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getMyBoxs() {
        return myBoxs;
    }

    public void setMyBoxs(String myBoxs) {
        this.myBoxs = myBoxs;
    }

    public String getVoucherTypes() {
        return this.voucherTypes;
    }

    public void setVoucherTypes(String voucherTypes) {
        this.voucherTypes = voucherTypes;
    }

    public String getPaymentTypes() {
        return this.paymentTypes;
    }

    public void setPaymentTypes(String paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public String getMemberTypes() {
        return this.memberTypes;
    }

    public void setMemberTypes(String memberTypes) {
        this.memberTypes = memberTypes;
    }

    public String getCategories() {
        return this.categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getBestVoucher() {
        return this.bestVoucher;
    }

    public void setBestVoucher(String bestVoucher) {
        this.bestVoucher = bestVoucher;
    }

    public String getFeaturedVoucher() {
        return this.featuredVoucher;
    }

    public void setFeaturedVoucher(String featuredVoucher) {
        this.featuredVoucher = featuredVoucher;
    }
}
