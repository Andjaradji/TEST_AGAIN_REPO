package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.PremiumDueDateResponse;
import com.vexanium.vexgift.bean.response.PremiumHistoryResponse;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.bean.response.PremiumPurchaseResponse;
import com.vexanium.vexgift.bean.response.SettingResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface OtherService {
    @FormUrlEncoded
    @POST("setting")
    Observable<HttpResponse<SettingResponse>> getSettings(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @GET("setting/app-status")
    Observable<HttpResponse<SettingResponse>> getAppStatus(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("voucher")
    Observable<HttpResponse<VouchersResponse>> getNotifList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("premium-member-duration")
    Observable<HttpResponse<PremiumListResponse>> getPremiumList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("premium-member/buy")
    Observable<HttpResponse<PremiumPurchaseResponse>> purcasePremiumMember(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("premium-member/get")
    Observable<HttpResponse<PremiumHistoryResponse>> getPremiumHistoryList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("premium-member/user-premium-due-date")
    Observable<HttpResponse<PremiumDueDateResponse>> getPremiumDueDate(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);


}
