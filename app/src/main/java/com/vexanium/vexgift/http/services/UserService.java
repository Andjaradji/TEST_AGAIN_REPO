package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.Google2faResponse;
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

    @FormUrlEncoded
    @POST("user/2fa")
    Observable<HttpResponse<Google2faResponse>> requestGoogleAuthCode(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/2fa/enable")
    Observable<HttpResponse<EmptyResponse>> requestGoogleAuthEnable(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/2fa/disable")
    Observable<HttpResponse<EmptyResponse>> requestGoogleAuthDisable(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/kyc")
    Observable<HttpResponse<Kyc>> submitKyc(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/kyc/get-last-kyc")
    Observable<HttpResponse<Kyc>> requestKyc(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);
}
