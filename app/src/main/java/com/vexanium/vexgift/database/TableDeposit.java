package com.vexanium.vexgift.database;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "id DESC", unique = true)
})
public class TableDeposit {
    @Id
    private long id;
    private String deposits;
    private String userDeposits;
    private long createdTime;
    private long updatedTime;
@Generated(hash = 2048385194)
public TableDeposit(long id, String deposits, String userDeposits,
        long createdTime, long updatedTime) {
    this.id = id;
    this.deposits = deposits;
    this.userDeposits = userDeposits;
    this.createdTime = createdTime;
    this.updatedTime = updatedTime;
}
@Generated(hash = 1100934374)
public TableDeposit() {
}
public long getId() {
    return this.id;
}
public void setId(long id) {
    this.id = id;
}
public String getDeposits() {
    return this.deposits;
}
public void setDeposits(String deposits) {
    this.deposits = deposits;
}
public long getCreatedTime() {
    return this.createdTime;
}
public void setCreatedTime(long createdTime) {
    this.createdTime = createdTime;
}
public long getUpdatedTime() {
    return this.updatedTime;
}
public void setUpdatedTime(long updatedTime) {
    this.updatedTime = updatedTime;
}
public String getUserDeposits() {
    return this.userDeposits;
}
public void setUserDeposits(String userDeposits) {
    this.userDeposits = userDeposits;
}
}
