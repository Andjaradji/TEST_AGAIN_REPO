package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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
    Observable<HttpResponse<EmptyResponse>> requestRegister(
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

    @Multipart
    @POST("user/kyc")
    Observable<HttpResponse<Kyc>> submitKyc(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @PartMap Map<String, Object> params,
            @Part MultipartBody.Part frontImage,
            @Part MultipartBody.Part backImage,
            @Part MultipartBody.Part selfieImage);

    @FormUrlEncoded
    @POST("user/kyc/get-last-kyc")
    Observable<HttpResponse<Kyc>> requestKyc(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);
}
