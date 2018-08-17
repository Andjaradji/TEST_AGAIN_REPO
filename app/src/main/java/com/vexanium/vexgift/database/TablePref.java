package com.vexanium.vexgift.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "id DESC", unique = true)
})
public class TablePref {
    @Id
    private long id;
    private String setting;
    private String inviteCode;

    @Generated(hash = 786432263)
    public TablePref(long id, String setting, String inviteCode) {
        this.id = id;
        this.setting = setting;
        this.inviteCode = inviteCode;
    }

    @Generated(hash = 675594995)
    public TablePref() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
