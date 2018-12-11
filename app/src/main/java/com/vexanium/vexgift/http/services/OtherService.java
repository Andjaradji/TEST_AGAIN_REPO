package com.vexanium.vexgift.http.services;

import com.vexanium.vexgift.bean.model.BuybackHistory;
import com.vexanium.vexgift.bean.response.BuybackHistoryResponse;
import com.vexanium.vexgift.bean.response.BuybackPaymentResponse;
import com.vexanium.vexgift.bean.response.BuybackResponse;
import com.vexanium.vexgift.bean.response.CountriesResponse;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.ExchangeResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.LuckyDrawListResponse;
import com.vexanium.vexgift.bean.response.LuckyDrawResponse;
import com.vexanium.vexgift.bean.response.PremiumDueDateResponse;
import com.vexanium.vexgift.bean.response.PremiumHistoryResponse;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.bean.response.PremiumPurchaseResponse;
import com.vexanium.vexgift.bean.response.SettingResponse;
import com.vexanium.vexgift.bean.response.TokenSaleHistoryDetailResponse;
import com.vexanium.vexgift.bean.response.TokenSaleHistoryResponse;
import com.vexanium.vexgift.bean.response.TokenSalePaymentResponse;
import com.vexanium.vexgift.bean.response.TokenSaleResponse;
import com.vexanium.vexgift.bean.response.UserDepositResponse;
import com.vexanium.vexgift.bean.response.UserDepositSingleResponse;
import com.vexanium.vexgift.bean.response.UserLuckyDrawListResponse;
import com.vexanium.vexgift.bean.response.UserLuckyDrawResponse;
import com.vexanium.vexgift.bean.response.VexVaultResponse;
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
    @POST("exchange/get")
    Observable<HttpResponse<ExchangeResponse>> getExchangeList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

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

    @GET("country")
    Observable<HttpResponse<CountriesResponse>> getCountries(
            @Header("Cache-Control") String cacheControl,
            @QueryMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("deposit/get")
    Observable<HttpResponse<DepositListResponse>> getDeposits(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("deposit/get-deposit-history")
    Observable<HttpResponse<UserDepositResponse>> getDepositHistory(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("deposit/deposit")
    Observable<HttpResponse<UserDepositSingleResponse>> requestDeposit(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("vex-vault/get")
    Observable<HttpResponse<VexVaultResponse>> getTokenFreeze(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("token-sale/get")
    Observable<HttpResponse<TokenSaleResponse>> getTokenSales(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("token-sale/get-token-sale-payment-history")
    Observable<HttpResponse<TokenSaleHistoryResponse>> getTokenSaleHistories(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("token-sale/buy")
    Observable<HttpResponse<TokenSalePaymentResponse>> buyTokenSales(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("token-sale/update-distribution-address")
    Observable<HttpResponse<EmptyResponse>> updateDistributionAddress(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("token-sale/get-token-sale-payment")
    Observable<HttpResponse<TokenSaleHistoryDetailResponse>> getTokenSalePayment(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("lucky-draw")
    Observable<HttpResponse<LuckyDrawListResponse>> getLuckyDrawList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("lucky-draw/get")
    Observable<HttpResponse<LuckyDrawResponse>> getLuckyDraw(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("lucky-draw/get-user-lucky-draw")
    Observable<HttpResponse<UserLuckyDrawListResponse>> getUserLuckyDrawList(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("lucky-draw/buy-lucky-draw")
    Observable<HttpResponse<EmptyResponse>> buyLuckyDraw(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("lucky-draw/set-address")
    Observable<HttpResponse<UserLuckyDrawResponse>> setUserLuckyDrawAddress(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("buy-back/get-buy-back-history")
    Observable<HttpResponse<BuybackHistoryResponse>> getBuybackHistories(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("buy-back")
    Observable<HttpResponse<BuybackPaymentResponse>> doBuyback(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("buy-back/get")
    Observable<HttpResponse<BuybackResponse>> getBuyback(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("buy-back/update-distribution-address")
    Observable<HttpResponse<EmptyResponse>> updateBuybackDistributionAddress(
            @Header("X-Vexanium-Key") String key,
            @Header("Cache-Control") String cacheControl,
            @FieldMap Map<String, Object> params);

}
