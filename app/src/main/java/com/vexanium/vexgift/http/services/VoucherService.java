package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserVouchersResponse;
import com.vexanium.vexgift.bean.response.VoucherCodeResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface VoucherService {
    @FormUrlEncoded
    @POST("voucher")
    Observable<HttpResponse<VouchersResponse>> getVoucherList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("voucher/get-user-voucher")
    Observable<HttpResponse<UserVouchersResponse>> getUserVoucherList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("voucher/buy-voucher")
    Observable<HttpResponse<EmptyResponse>> requestBuyVoucher(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("voucher/claim-voucher")
    Observable<HttpResponse<VoucherCodeResponse>> requestRedeemVoucher(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("voucher/deactivate-voucher-code")
    Observable<HttpResponse<VoucherCodeResponse>> requestDeactivateVoucher(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

}
