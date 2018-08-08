package com.vexanium.vexgift.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "id DESC", unique = true)
})
public class TableContent {
    @Id
    private long id;
    private String result;
    private long createdTime;

    @Generated(hash = 1861641962)
    public TableContent(long id, String result, long createdTime) {
        this.id = id;
        this.result = result;
        this.createdTime = createdTime;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
