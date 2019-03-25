package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.WalletReferralResponse;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.bean.response.WithdrawResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface WalletService {
    @FormUrlEncoded
    @POST("wallet/get-wallet")
    Observable<HttpResponse<WalletResponse>> getWallet(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @Header("locale") String locale,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("wallet/create-wallet")
    Observable<HttpResponse<WalletResponse>> createWallet(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @Header("locale") String locale,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("wallet/withdraw")
    Observable<HttpResponse<WithdrawResponse>> doWithdraw(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @Header("locale") String locale,
            @FieldMap Map<String, Object> params);


    @FormUrlEncoded
    @POST("wallet/referral/get-referral")
    Observable<HttpResponse<WalletReferralResponse>> getWalletReferral(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @Header("locale") String locale,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("wallet/cancel-withdraw")
    Observable<HttpResponse<WithdrawResponse>> cancelWithdraw(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @Header("locale") String locale,
            @FieldMap Map<String, Object> params);
}
