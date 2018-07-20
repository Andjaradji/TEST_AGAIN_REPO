package com.vexanium.vexgift.bean.model;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.socks.library.KLog;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by mac on 11/16/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @JsonProperty("uid")
    private String id;
    @JsonProperty("photo")
    private String photo;
    @JsonProperty("name")
    private String name;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("password")
    private String password;
    @JsonProperty("age")
    private int age;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("name")
    private String fullName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("city")
    private String city;
    @JsonProperty("level")
    private int level;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("register")
    private String registerTime;

    @JsonProperty("ov")
    private String osVersion;
    @JsonProperty("av")
    private String appVersion;
    @JsonProperty("device_name")
    private String deviceName;

    @JsonProperty("sess")
    private String sessionKey;
    @JsonProperty("gid")
    private String googleToken;
    @JsonProperty("last")
    private long lastTimestamp;

    @JsonProperty("liked")
    private ArrayList<String> likedList = new ArrayList<>();
    @JsonProperty("bookmarked")
    private ArrayList<String> bookmarkList = new ArrayList<>();
    @JsonProperty("vote")
    private ArrayList<String> vote = new ArrayList<>();

    @JsonProperty("config")
    private SettingCondition config;
    @JsonProperty("pushtoken")
    private String pushToken;

    @JsonProperty("mutual_friends")
    private List<String> friendList = new ArrayList<>();
    @JsonProperty("profile_images")
    private List<String> profileList = new ArrayList<>();

    @JsonProperty("instagram_user_name")
    private String instagramUserName;
    @JsonProperty("instagram_images")
    private List<String> instagramMedias;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dob")
    private String birthDay;
    @JsonProperty("verified")
    private boolean verified;
    @JsonProperty("timezone")
    private int timezone;
    @JsonProperty("fb_link")
    private String facebookLink;

    @JsonProperty("fb_uid")
    private String facebookUid;

    @JsonProperty("fb_token")
    private String facebookAccessToken;
    private List<String> facebookLocationIdList;
    @JsonProperty("fb_friend_count")
    private int facebookFriendCount;
    private List<Album> albumList = new ArrayList<>();

    private String instagramAccessToken;

    private static User currentUser;

    public User(String photo, String name, String city) {
        this.photo = photo;
        this.name = name;
        this.city = city;
    }

    public User(String photo, String name, String city, int level) {
        this.photo = photo;
        this.name = name;
        this.city = city;
        this.level = level;
    }

    public User(String photo, String name) {
        this.photo = photo;
        this.name = name;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public static User getCurrentUser(Context context) {
        TpUtil tpUtil = new TpUtil(context);
        String userStr = tpUtil.getString(TpUtil.KEY_CURRENT_LOGGED_IN_USER, "");

        currentUser = null;
        if (!TextUtils.isEmpty(userStr)) {
            currentUser = (User) JsonUtil.toObject(userStr, User.class);
        }

        return currentUser;
    }

    public static void updateCurrentUser(Context context, User updateInfo) {
        User existUser = getCurrentUser(context);
        KLog.v("updateCurrentUser1 : " + existUser);
        if (existUser != null && updateInfo != null) {
            KLog.v("updateCurrentUser2 : " + existUser.getFacebookLocationIdList());
            updateInfo.setFacebookLocationIdList(existUser.getFacebookLocationIdList());
            updateInfo.setFacebookAccessToken(existUser.getFacebookAccessToken());
            updateInfo.setFacebookUid(existUser.getFacebookUid());
            updateInfo.setFacebookFriendCount(existUser.getFacebookFriendCount());
            updateInfo.setEmail(existUser.getEmail());

//            if (!TextUtils.isEmpty(existUser.getInstagramUserName())) {
//                updateInfo.setInstagramUserName(existUser.getInstagramUserName());
//            }
//            if (!TextUtils.isEmpty(existUser.getInstagramAccessToken())) {
//                updateInfo.setInstagramAccessToken(existUser.getInstagramAccessToken());
//            }
//            if (existUser.getInstagramMedias().size() != 0) {
//                updateInfo.setInstagramMedias(existUser.getInstagramMedias());
//            }
        }

        TpUtil tpUtil = new TpUtil(context);
        tpUtil.put(TpUtil.KEY_CURRENT_LOGGED_IN_USER, JsonUtil.toString(updateInfo));
    }

    public static User createWithFacebook(JSONObject userInfo) {
        final User user = new User();
        user.facebookLocationIdList = new ArrayList<>();

        try {
            if (!TextUtils.isEmpty(userInfo.getString("id"))) {
                user.setFacebookUid(userInfo.getString("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(userInfo.getString("first_name"))) {
                user.setName(userInfo.getString("first_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(userInfo.getString("last_name"))) {
                user.setFamilyName(userInfo.getString("last_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(userInfo.getString("email"))) {
                user.setEmail(userInfo.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(userInfo.getString("birthday"))) {
                user.setBirthDay(userInfo.getString("birthday"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date birthday = dateFormat.parse(userInfo.getString("birthday"));
                user.setAge(calculateAge(birthday));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(userInfo.getString("gender"))) {
                user.setGender(userInfo.getString("gender"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(userInfo.getString("locale"))) {
                user.setLocale(userInfo.getString("locale"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (userInfo.has("timezone")) {
                user.setTimezone(userInfo.getInt("timezone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (userInfo.has("about")) {
                user.setDescription(userInfo.getString("about"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (userInfo.has("verified")) {
                user.setVerified(userInfo.getBoolean("verified"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> profileList = new ArrayList<>();
        try {
            JSONArray albumsArr = userInfo.getJSONObject("albums").getJSONArray("data");
            for (int i = 0; i < albumsArr.length(); i++) {
                JSONObject albumObj = albumsArr.getJSONObject(i);

                if (albumObj.get("name").equals("Profile Pictures")) {
                    JSONArray photoArray = albumObj.getJSONObject("photos").getJSONArray("data");
                    if (photoArray != null && photoArray.length() > 0) {
                        for (int j = 0; j < photoArray.length(); j++) {
                            JSONArray webpImages = photoArray.getJSONObject(j).getJSONArray("webp_images");
                            if (webpImages != null && webpImages.length() > 0) {
                                String profileStr = webpImages.getJSONObject(0).getString("source");
                                profileList.add(profileStr);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profileList.size() == 0) {
            try {
                if (userInfo.getJSONObject("picture") != null
                        && userInfo.getJSONObject("picture").getJSONObject("data") != null
                        && !TextUtils.isEmpty(userInfo.getJSONObject("picture").getJSONObject("data").getString("url"))) {
                    String profileUrl = userInfo.getJSONObject("picture").getJSONObject("data").getString("url");
                    profileList.add(profileUrl);
                    //Glide.with(App.getContext()).download(profileUrl);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (userInfo.getJSONObject("cover") != null
                        && !TextUtils.isEmpty(userInfo.getJSONObject("cover").getString("source"))) {
                    String coverUrl = userInfo.getJSONObject("cover").getString("source");
                    profileList.add(coverUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (profileList.size() > 0) {
            user.setProfileList(profileList);
            user.setPhoto(profileList.get(0));
        }

        try {
            JSONObject locationInfo = userInfo.getJSONObject("location");
            if (locationInfo != null) {
                String locationId = locationInfo.getString("id");
                if (!TextUtils.isEmpty(locationId) && !user.facebookLocationIdList.contains(locationId)) {
                    user.facebookLocationIdList.add(locationId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject friendTotalInfo = userInfo.getJSONObject("friends");
            if (friendTotalInfo != null) {
                int friendTotalCount = friendTotalInfo.getJSONObject("summary").getInt("total_count");
                user.facebookFriendCount = friendTotalCount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        KLog.v("user.facebookLocationIdList : " + user.facebookLocationIdList);

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !TextUtils.isEmpty(accessToken.getToken())) {
            user.setFacebookAccessToken(accessToken.getToken());
        }

        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {

        if(TextUtils.isEmpty(fullName))
            if (TextUtils.isEmpty(lastName))
                fullName = name;
            else
                fullName = name +" "+ lastName;
        return fullName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public ArrayList<String> getLikedList() {
        return likedList;
    }

    public void setLikedList(ArrayList<String> likedList) {
        this.likedList = likedList;
    }

    public ArrayList<String> getBookmarkList() {
        return bookmarkList;
    }

    public void setBookmarkList(ArrayList<String> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public SettingCondition getConfig() {
        return config;
    }

    public void setConfig(SettingCondition config) {
        this.config = config;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }

    public String getInstagramUserName() {
        return instagramUserName;
    }

    public void setInstagramUserName(String instagramUserName) {
        this.instagramUserName = instagramUserName;
    }

    public List<String> getInstagramMedias() {
        return instagramMedias;
    }

    public void setInstagramMedias(List<String> instagramMedias) {
        this.instagramMedias = instagramMedias;
    }

    public String getFamilyName() {
        return lastName;
    }

    public void setFamilyName(String familyName) {
        this.lastName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getFacebookUid() {
        return facebookUid;
    }

    public void setFacebookUid(String facebookUid) {
        this.facebookUid = facebookUid;
    }

    public String getFacebookAccessToken() {
        return facebookAccessToken;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
    }

    public List<String> getFacebookLocationIdList() {
        return facebookLocationIdList;
    }

    public void setFacebookLocationIdList(List<String> facebookLocationIdList) {
        this.facebookLocationIdList = facebookLocationIdList;
    }

    public int getFacebookFriendCount() {
        return facebookFriendCount;
    }

    public void setFacebookFriendCount(int facebookFriendCount) {
        this.facebookFriendCount = facebookFriendCount;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

    public String getInstagramAccessToken() {
        return instagramAccessToken;
    }

    public void setInstagramAccessToken(String instagramAccessToken) {
        this.instagramAccessToken = instagramAccessToken;
    }

    public List<String> getProfileList() {
        return profileList;
    }

    public void setProfileList(List<String> profileList) {
        this.profileList = profileList;
    }

    private static int calculateAge(Date dateOfBirth) {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) || (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) && (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
