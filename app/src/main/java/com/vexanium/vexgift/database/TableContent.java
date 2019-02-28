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
    private String referrals;
    private String walletReferrals;
    private String luckydraws;
    private String banners;
    private String bigBanners;
    private String news;
    private String wallet;
    private long createdTime;
    private long updatedTime;


    @Generated(hash = 1398052712)
    public TableContent(long id, String vouchers, String tokens, String notifs, String myBoxs,
            String voucherTypes, String paymentTypes, String memberTypes, String categories, String bestVoucher,
            String featuredVoucher, String referrals, String walletReferrals, String luckydraws, String banners,
            String bigBanners, String news, String wallet, long createdTime, long updatedTime) {
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
        this.referrals = referrals;
        this.walletReferrals = walletReferrals;
        this.luckydraws = luckydraws;
        this.banners = banners;
        this.bigBanners = bigBanners;
        this.news = news;
        this.wallet = wallet;
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

    public String getReferrals() {
        return this.referrals;
    }

    public void setReferrals(String referrals) {
        this.referrals = referrals;
    }

    public String getBanners() {
        return this.banners;
    }

    public void setBanners(String banners) {
        this.banners = banners;
    }

    public String getLuckydraws() {
        return this.luckydraws;
    }

    public void setLuckydraws(String luckydraws) {
        this.luckydraws = luckydraws;
    }

    public String getNews() {
        return this.news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getWallet() {
        return this.wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getBigBanners() {
        return this.bigBanners;
    }

    public void setBigBanners(String bigBanners) {
        this.bigBanners = bigBanners;
    }

    public String getWalletReferrals() {
        return this.walletReferrals;
    }

    public void setWalletReferrals(String walletReferrals) {
        this.walletReferrals = walletReferrals;
    }
}
