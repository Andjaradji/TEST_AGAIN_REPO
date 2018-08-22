package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.response.BestVoucherResponse;
import com.vexanium.vexgift.bean.response.CategoryResponse;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.FeaturedVoucherResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.MemberTypeResponse;
import com.vexanium.vexgift.bean.response.PaymentTypeResponse;
import com.vexanium.vexgift.bean.response.UserVouchersResponse;
import com.vexanium.vexgift.bean.response.VoucherCodeResponse;
import com.vexanium.vexgift.bean.response.VoucherGiftCodeResponse;
import com.vexanium.vexgift.bean.response.VoucherTypeResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface VoucherService {
    @FormUrlEncoded
    @POST("member-type")
    Observable<HttpResponse<MemberTypeResponse>> getMemberTypes(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("payment-type")
    Observable<HttpResponse<PaymentTypeResponse>> getPaymentTypes(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("voucher-type")
    Observable<HttpResponse<VoucherTypeResponse>> getVoucherTypes(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("category")
    Observable<HttpResponse<CategoryResponse>> getCategories(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("voucher")
    Observable<HttpResponse<VouchersResponse>> getVoucherList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("best-voucher")
    Observable<HttpResponse<BestVoucherResponse>> getBestVoucherList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("featured-voucher")
    Observable<HttpResponse<FeaturedVoucherResponse>> getFeaturedVoucherList(
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


    @FormUrlEncoded
    @POST("voucher-gift-code/get-gift-code")
    Observable<HttpResponse<VoucherGiftCodeResponse>> requestGetGiftCode(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);


    @FormUrlEncoded
    @POST("voucher-gift-code/claim-gift-code")
    Observable<HttpResponse<VoucherCodeResponse>> requestClaimGiftCode(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

}
