package com.vexanium.vexgift.bean.model;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
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
import java.util.Locale;


/**
 * Created by mac on 11/16/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @JsonProperty("id")
    private int id;
    @JsonProperty("password")
    private String password;

    @JsonProperty("register")
    private String registerTime;

    @JsonProperty("ov")
    private String osVersion;
    @JsonProperty("av")
    private String appVersion;
    @JsonProperty("device_name")
    private String deviceName;

    @JsonProperty("access_token")
    private String sessionKey;
    @JsonProperty("vex_point")
    private int vexPoint;

    @JsonProperty("last_login")
    private String lastTimestamp;

    @JsonProperty("config")
    private SettingCondition config;
    @JsonProperty("pushtoken")
    private String pushToken;

    @JsonProperty("mutual_friends")
    private List<String> friendList = new ArrayList<>();
    @JsonProperty("profile_images")
    private List<String> profileList = new ArrayList<>();

    @JsonProperty("email")
    private String email;

    @JsonProperty("authenticator_enable")
    private boolean authenticatorEnable;

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("fb_access_token")
    private String facebookAccessToken;
    @JsonProperty("fb_link")
    private String facebookLink;
    @JsonProperty("social_media_id")
    private String facebookUid;
    @JsonProperty("photo")
    private String photo;
    @JsonProperty("age")
    private int age;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("timezone")
    private int timezone;
    @JsonProperty("birthday")
    private String birthDay;

    @JsonProperty("google_id_token")
    private String googleToken;

    private List<String> facebookLocationIdList;
    @JsonProperty("fb_friend_count")
    private int facebookFriendCount;
    private List<Album> albumList = new ArrayList<>();

    private static User currentUser;

    public User() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

        TpUtil tpUtil = TpUtil.getInstance(context);
        tpUtil.put(TpUtil.KEY_CURRENT_LOGGED_IN_USER, JsonUtil.toString(updateInfo));
    }

    public static void setIsPasswordSet(Context context, boolean isPasswordSet) {
        TpUtil tpUtil = TpUtil.getInstance(context);
        tpUtil.put(TpUtil.KEY_IS_PASS_SET, isPasswordSet);
    }

    public static boolean getIsPasswordSet(Context context) {
        TpUtil tpUtil = TpUtil.getInstance(context);
        return tpUtil.getBoolean(TpUtil.KEY_IS_PASS_SET, false);
    }

    public static void setIsVexAddressSet(Context context, boolean isPasswordSet) {
        TpUtil tpUtil = TpUtil.getInstance(context);
        tpUtil.put(TpUtil.KEY_IS_VEX_ADD_SET, isPasswordSet);
    }

    public static boolean getIsVexAddressSet(Context context) {
        TpUtil tpUtil = TpUtil.getInstance(context);
        return tpUtil.getBoolean(TpUtil.KEY_IS_VEX_ADD_SET, false);
    }

    public static boolean isVexAddVerifTimeEnded() {
        TpUtil tpUtil = new TpUtil(App.getContext());

        long fillTime = tpUtil.getLong(TpUtil.KEY_VEX_ADDRESS_VERIF_TIME, 0);
        long now = System.currentTimeMillis();

        if (fillTime > 0) {
            KLog.v("User", "isVexAddVerifTimeEnded: now [" + now + "] - last[" + fillTime + "] (" + (now - fillTime) + ")");
            if (now - fillTime > StaticGroup.VEX_ADDRESS_VERIF_TIME) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static boolean isLocalSessionEnded() {
        TpUtil tpUtil = new TpUtil(App.getContext());

        long lastActiveTime = tpUtil.getLong(TpUtil.KEY_LAST_ACTIVE_TIME, 0);

        long now = System.currentTimeMillis();

        KLog.v("User", "isLocalSessionEnded: now [" + now + "] - last[" + lastActiveTime + "] (" + (now - lastActiveTime) + ")");
        if (now - lastActiveTime > StaticGroup.SLEEP_SIGN_TIME) {
            tpUtil.put(TpUtil.KEY_GOOGLE2FA_LOCK, true);
            return true;
        }
        return false;
    }

    public static boolean isGoogle2faLocked() {
        boolean isGoogle2faLocked;
        TpUtil tpUtil = new TpUtil(App.getContext());
        isGoogle2faLocked = tpUtil.getBoolean(TpUtil.KEY_GOOGLE2FA_LOCK, false);
        KLog.v("User", "isGoogle2faLocked: " + isGoogle2faLocked);
        return isGoogle2faLocked;
    }

    public boolean isAuthenticatorEnable() {
        return authenticatorEnable;
    }

    public void setAuthenticatorEnable(boolean authenticatorEnable) {
        this.authenticatorEnable = authenticatorEnable;
    }

    public static void setLastActiveTime() {
        Date now = new Date();
        TpUtil tpUtil = new TpUtil(App.getContext());
        tpUtil.put(TpUtil.KEY_LAST_ACTIVE_TIME,
                now.getTime());
    }

    public static void setVexAddressSubmitTime() {
        Date now = new Date();
        TpUtil tpUtil = new TpUtil(App.getContext());
        tpUtil.put(TpUtil.KEY_VEX_ADDRESS_VERIF_TIME,
                now.getTime());
    }

    public static User createWithGoogle(GoogleSignInAccount account) {
        final User user = new User();

        user.setEmail(account.getEmail());
        user.setFirstName(account.getDisplayName());
        user.setGoogleToken(account.getIdToken());

        return user;
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
                user.setFirstName(userInfo.getString("first_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(userInfo.getString("last_name"))) {
                user.setLastName(userInfo.getString("last_name"));
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

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
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
                user.facebookFriendCount = friendTotalInfo.getJSONObject("summary").getInt("total_count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !TextUtils.isEmpty(accessToken.getToken())) {
            user.setFacebookAccessToken(accessToken.getToken());
        }

        return user;
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

    public String getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(String lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
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

    public void setName(String name) {
        this.name = name;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public int getVexPoint() {
        return vexPoint + 1500;
    }

    public void setVexPoint(int vexPoint) {
        this.vexPoint = vexPoint;
    }

    public boolean isLoginByFacebook() {
        return !TextUtils.isEmpty(getFacebookAccessToken());
    }
}
