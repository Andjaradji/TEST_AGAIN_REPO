package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Buyback implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("member_type_id")
    private int memberTypeId;
    @JsonProperty("image")
    private String image;
    @JsonProperty("description")
    private String description;
    @JsonProperty("name")
    private String name;
    @JsonProperty("start_time")
    private long startTime;
    @JsonProperty("end_time")
    private long endTime;
    @JsonProperty("coin_type")
    private String coinType;
    @JsonProperty("coin_name")
    private String coinName;
    @JsonProperty("coin_bought")
    private float coinBought;
    @JsonProperty("total_coin")
    private float totalCoin;
    @JsonProperty("base_payment_address")
    private String basePaymentAddress;
    @JsonProperty("loyalty_point_required")
    private int loyaltyPointRequired;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("member_type")
    private MemberType memberType;
    @JsonProperty("buy_back_options")
    private ArrayList<BuybackOption> buybackOptions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(int memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public float getCoinBought() {
        return coinBought;
    }

    public void setCoinBought(float coinBought) {
        this.coinBought = coinBought;
    }

    public float getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(float totalCoin) {
        this.totalCoin = totalCoin;
    }

    public String getBasePaymentAddress() {
        return basePaymentAddress;
    }

    public void setBasePaymentAddress(String basePaymentAddress) {
        this.basePaymentAddress = basePaymentAddress;
    }

    public int getLoyaltyPointRequired() {
        return loyaltyPointRequired;
    }

    public void setLoyaltyPointRequired(int loyaltyPointRequired) {
        this.loyaltyPointRequired = loyaltyPointRequired;
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

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public ArrayList<BuybackOption> getBuybackOptions() {
        return buybackOptions;
    }

    public void setBuybackOptions(ArrayList<BuybackOption> buybackOptions) {
        this.buybackOptions = buybackOptions;
    }

    public String getTimeStampDate(long timeStamp) {
        long l = TimeUnit.SECONDS.toMillis(timeStamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

}
