package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VexPointRecord implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("vex_point_log_type_id")
    private int vpLogTypeId;

    @JsonProperty("amount")
    private double amount;

    @JsonProperty("vex_counted_1")
    private double vexCounted1;

    @JsonProperty("vex_counted_2")
    private double vexCounted2;

    @JsonProperty("vex_counted_3")
    private double vexCounted3;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("vex_point_log_type")
    private VexPointLogType vexPointLogType;

    @JsonProperty("voucher")
    private Voucher voucher;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVpLogTypeId() {
        return vpLogTypeId;
    }

    public void setVpLogTypeId(int vpLogTypeId) {
        this.vpLogTypeId = vpLogTypeId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getVexCounted1() {
        return vexCounted1;
    }

    public void setVexCounted1(double vexCounted1) {
        this.vexCounted1 = vexCounted1;
    }

    public double getVexCounted2() {
        return vexCounted2;
    }

    public void setVexCounted2(double vexCounted2) {
        this.vexCounted2 = vexCounted2;
    }

    public double getVexCounted3() {
        return vexCounted3;
    }

    public void setVexCounted3(double vexCounted3) {
        this.vexCounted3 = vexCounted3;
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

    public VexPointLogType getVexPointLogType() {
        return vexPointLogType;
    }

    public void setVexPointLogType(VexPointLogType vexPointLogType) {
        this.vexPointLogType = vexPointLogType;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public String getCreatedAtDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat dateOutput = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(createdAt);
            return dateOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return createdAt;
        }
    }
}
