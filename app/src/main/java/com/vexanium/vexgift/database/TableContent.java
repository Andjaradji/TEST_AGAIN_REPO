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
    private long createdTime;
    private long updatedTime;


    @Generated(hash = 1459931542)
    public TableContent(long id, String vouchers, String tokens, String notifs,
            String myBoxs, long createdTime, long updatedTime) {
        this.id = id;
        this.vouchers = vouchers;
        this.tokens = tokens;
        this.notifs = notifs;
        this.myBoxs = myBoxs;
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
}
