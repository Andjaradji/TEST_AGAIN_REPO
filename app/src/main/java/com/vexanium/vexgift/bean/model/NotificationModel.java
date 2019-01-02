package com.vexanium.vexgift.bean.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hizkia on 12/03/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationModel extends BaseType {

    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("title")
    private String title;
    @JsonProperty("body")
    private String body;
    @JsonProperty("url")
    private String url;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getCreatedAtDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat dateOutput = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = dateFormat.parse(getCreatedAt());
            return dateOutput.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return getCreatedAt();
        }
    }

    public long getCreatedAtMillis() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = dateFormat.parse(getCreatedAt());
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
}