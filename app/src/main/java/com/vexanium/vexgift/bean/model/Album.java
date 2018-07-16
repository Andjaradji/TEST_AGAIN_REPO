package com.vexanium.vexgift.bean.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mac on 11/20/17.
 */

public class Album implements Parcelable {

    public long id;
    public String name;
    public String picture;
    public int count;

    public Album(long id, String name, String picture, int count) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.picture);
        dest.writeInt(this.count);
    }

    public Album(Parcel source) {
        this.id = source.readLong();
        this.name = source.readString();
        this.picture = source.readString();
        this.count = source.readInt();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

}