package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.NotificationResponse;
import com.vexanium.vexgift.bean.response.ResetPasswordCodeResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.bean.response.UserReferralResponse;
import com.vexanium.vexgift.bean.response.VexPointRecordResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
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
    @PATCH("user/password")
    Observable<HttpResponse<EmptyResponse>> requestChangePassword(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/reset-password")
    Observable<HttpResponse<EmptyResponse>> requestResetPassword(
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/reset-password/validate-code")
    Observable<HttpResponse<ResetPasswordCodeResponse>> requestResetPasswordCodeValidation(
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/reset-password/validate-token")
    Observable<HttpResponse<EmptyResponse>> requestResetPasswordTokenValidation(
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

//    @FormUrlEncoded
//    @POST("user/password")
//    Observable<HttpResponse<EmptyResponse>> requestChangePassword(
//            @Header("X-Vexanium-Key") String key,
//            @Header("Cache-Control") String cacheControl,
//            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/2fa")
    Observable<HttpResponse<Google2faResponse>> requestGoogleAuthCode(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/2fa/check")
    Observable<HttpResponse<EmptyResponse>> checkGoogleAuthToken(
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

    @FormUrlEncoded
    @POST("user/vex-point/get-act-address")
    Observable<HttpResponse<UserAddressResponse>> getActAddress(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/vex-point/update-act-address")
    Observable<HttpResponse<UserAddressResponse>> setActAddress(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/email-confirmation")
    Observable<HttpResponse<UserLoginResponse>> requestEmailConfirmation(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/resend-email-confirmation")
    Observable<HttpResponse<EmptyResponse>> requestResendEmailConfirmation(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @PATCH("user/update-notification-id")
    Observable<HttpResponse<EmptyResponse>> requestUpdateNotificationId(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("vex-point-log")
    Observable<HttpResponse<VexPointRecordResponse>> getVexPointLog(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("vex-point-log-type")
    Observable<HttpResponse<VexPointRecordResponse>> getVexPointLogTypes(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("user/referral/get")
    Observable<HttpResponse<UserReferralResponse>> getUserReferrals(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("notification")
    Observable<HttpResponse<NotificationResponse>> getUserNotification(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

}
