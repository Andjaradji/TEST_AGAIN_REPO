package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by hizkia on 01/03/18.
 */

public interface UserService {
    @FormUrlEncoded
    @POST("user/auth")
    Observable<HttpResponse<UserLoginResponse>> requestLogin(
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user")
    Observable<HttpResponse<UserLoginResponse>> requestRegister(
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);
}
