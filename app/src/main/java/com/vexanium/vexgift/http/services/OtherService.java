package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface OtherService {
    @FormUrlEncoded
    @POST("voucher")
    Observable<HttpResponse<VouchersResponse>> getNotifList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("premium-member/get")
    Observable<HttpResponse<PremiumListResponse>> getPremiumList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);


}
