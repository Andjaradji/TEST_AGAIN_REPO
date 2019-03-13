package com.vexanium.vexgift.http.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseSchedulerTransformer;
import com.vexanium.vexgift.bean.model.AffiliateProgram;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.AffiliateProgramEntryResponse;
import com.vexanium.vexgift.bean.response.AffiliateProgramResponse;
import com.vexanium.vexgift.bean.response.BannerResponse;
import com.vexanium.vexgift.bean.response.BestVoucherResponse;
import com.vexanium.vexgift.bean.response.BigBannerResponse;
import com.vexanium.vexgift.bean.response.BuybackHistoryResponse;
import com.vexanium.vexgift.bean.response.BuybackPaymentResponse;
import com.vexanium.vexgift.bean.response.BuybackResponse;
import com.vexanium.vexgift.bean.response.CategoryResponse;
import com.vexanium.vexgift.bean.response.CountriesResponse;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.ExchangeResponse;
import com.vexanium.vexgift.bean.response.FeaturedVoucherResponse;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.LuckyDrawListResponse;
import com.vexanium.vexgift.bean.response.LuckyDrawResponse;
import com.vexanium.vexgift.bean.response.MemberTypeResponse;
import com.vexanium.vexgift.bean.response.NewsResponse;
import com.vexanium.vexgift.bean.response.NotificationResponse;
import com.vexanium.vexgift.bean.response.PaymentTypeResponse;
import com.vexanium.vexgift.bean.response.PremiumDueDateResponse;
import com.vexanium.vexgift.bean.response.PremiumHistoryResponse;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.bean.response.PremiumPurchaseResponse;
import com.vexanium.vexgift.bean.response.ResetPasswordCodeResponse;
import com.vexanium.vexgift.bean.response.SettingResponse;
import com.vexanium.vexgift.bean.response.TokenSaleHistoryDetailResponse;
import com.vexanium.vexgift.bean.response.TokenSaleHistoryResponse;
import com.vexanium.vexgift.bean.response.TokenSalePaymentResponse;
import com.vexanium.vexgift.bean.response.TokenSaleResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.bean.response.UserDepositResponse;
import com.vexanium.vexgift.bean.response.UserDepositSingleResponse;
import com.vexanium.vexgift.bean.response.UserInputDataResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.bean.response.UserLuckyDrawListResponse;
import com.vexanium.vexgift.bean.response.UserLuckyDrawResponse;
import com.vexanium.vexgift.bean.response.UserReferralResponse;
import com.vexanium.vexgift.bean.response.UserVouchersResponse;
import com.vexanium.vexgift.bean.response.VexPointRecordResponse;
import com.vexanium.vexgift.bean.response.VexPointResponse;
import com.vexanium.vexgift.bean.response.VexVaultResponse;
import com.vexanium.vexgift.bean.response.VoucherCodeResponse;
import com.vexanium.vexgift.bean.response.VoucherGiftCodeResponse;
import com.vexanium.vexgift.bean.response.VoucherTypeResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.bean.response.WalletReferralResponse;
import com.vexanium.vexgift.bean.response.WalletResponse;
import com.vexanium.vexgift.bean.response.WithdrawResponse;
import com.vexanium.vexgift.http.Api;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.interceptor.RxErrorHandlingCallAdapterFactory;
import com.vexanium.vexgift.http.services.OtherService;
import com.vexanium.vexgift.http.services.UserService;
import com.vexanium.vexgift.http.services.VoucherService;
import com.vexanium.vexgift.http.services.WalletService;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.LocaleUtil;
import com.vexanium.vexgift.util.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.echodev.resizer.Resizer;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;

/**
 * Created by mac on 11/21/17.
 */

public class RetrofitManager {
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    private static final String CACHE_CONTROL_NETWORK = "max-age=0";

    private static volatile OkHttpClient sOkHttpClient;

    private static SparseArray<RetrofitManager> sInstanceManager = new SparseArray<>(HostType.TYPE_COUNT);

    private OtherService mOtherService;
    private UserService mUserService;
    private VoucherService mVoucherService;
    private WalletService mWalletService;

    private Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            KLog.e("Request URL: " + request.url());

            if (!NetworkUtil.isOnline(App.getContext())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                KLog.e("Cannot connect internet");
            }
            Response originalResponse = chain.proceed(request);

            if (NetworkUtil.isOnline(App.getContext())) {
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder().header("Cache-Control", cacheControl).removeHeader("Pragma").build();
            } else {
                return originalResponse.newBuilder().header("Cache-Control", "public, only-if-cached," + CACHE_STALE_SEC).removeHeader("Pragma").build();
            }
        }
    };

    private Interceptor mLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            final Request request = chain.request();
            final Response response = chain.proceed(request);

            final ResponseBody responseBody = response.body();
            final long contentLength = responseBody.contentLength();

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(charset);
                } catch (UnsupportedCharsetException e) {
                    KLog.e("Couldn't decode the response body; charset is likely malformed.");
                    return response;
                }
            }

            if (contentLength != 0) {
                KLog.v("--------------------------------------------Response Data Start----------------------------------------------------");
                KLog.v("URL : " + request.url().toString());
                KLog.json(buffer.clone().readString(charset));
                KLog.v("--------------------------------------------Response Data End----------------------------------------------------");
            }

            return response;
        }
    };

    private RetrofitManager() {
    }

    private RetrofitManager(@HostType.HostTypeChecker int hostType) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.getHost(hostType)).client(getOkHttpClient())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        mOtherService = retrofit.create(OtherService.class);
        mUserService = retrofit.create(UserService.class);
        mVoucherService = retrofit.create(VoucherService.class);
        mWalletService = retrofit.create(WalletService.class);
    }


    public static RetrofitManager getInstance(int hostType) {
        RetrofitManager instance = sInstanceManager.get(hostType);
        if (instance == null) {
            instance = new RetrofitManager(hostType);
            sInstanceManager.put(hostType, instance);
            return instance;
        } else {
            return instance;
        }
    }

    private OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (sOkHttpClient == null) {
                    Cache cache = new Cache(new File(App.getContext().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);

                    sOkHttpClient = new OkHttpClient.Builder().cache(cache)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(mLoggingInterceptor)
                            //.addInterceptor(new AddCookiesInterceptor())
                            //.addInterceptor(new ReceivedCookiesInterceptor())
                            .retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS).build();

                }
            }
        }
        return sOkHttpClient;
    }

    @NonNull
    private String getCacheControl() {
        return NetworkUtil.isOnline(App.getContext()) ? CACHE_CONTROL_NETWORK : CACHE_CONTROL_CACHE;
    }

    @NonNull
    private String getLocale() {
        return LocaleUtil.getLanguage(App.getContext());
    }

    @NonNull
    private String getApiKey() {
        return StaticGroup.getUserSession();
    }

    public Observable<HttpResponse<UserLoginResponse>> requestLogin(User user) {
        Map<String, Object> params = Api.getBasicParam();

        if (user.getLastName() != null) {
            params.put("last_name", user.getLastName());
        }

        if (user.getLastName() != null) {
            params.put("first_name", user.getFirstName());
        }
        if (user.getEmail() != null) {
            params.put("email", user.getEmail());
        }
        if (user.getPhoneNumber() != null) {
            params.put("phone_number", user.getPhoneNumber());
        }
        if (user.getPassword() != null) {
            params.put("password", user.getPassword());
        }
        if (user.getLocale() != null) {
            params.put("locale", user.getLocale());
        }
        if (user.getPhoto() != null) {
            params.put("cover", user.getPhoto());
        }
        if (user.getFacebookUid() != null) {
            params.put("social_media_id", user.getFacebookUid());
        }
        if (user.getFacebookAccessToken() != null) {
            params.put("fb_access_token", user.getFacebookAccessToken());
        }
        if (user.getLocale() != null) {
            params.put("locale", user.getLocale());
        }
        if (user.getLocale() != null) {
            params.put("timezone", user.getTimezone());
        }
        if (user.getLocale() != null) {
            params.put("gender", user.getGender());
        }
        if (user.getGoogleToken() != null) {
            params.put("google_id_token", user.getGoogleToken());
        }
        if (!TextUtils.isEmpty(user.getReferralCode())) {
            params.put("user_referral_code", user.getReferralCode());
        }

        params.put("fb_friend_count", user.getFacebookFriendCount());
        params.put("age", user.getAge());

        KLog.v("--------------------------------------------Request Login Param Start----------------------------------------------------");
        KLog.json(params.toString());
        KLog.v("--------------------------------------------Response Login Param End----------------------------------------------------");

        return mUserService.requestLogin(getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserLoginResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestRegister(User user) {
        Map<String, Object> params = Api.getBasicParam();

        if (user.getName() != null) {
            params.put("name", user.getName());
        } else {
            params.put("name", "undefined");
        }
        if (!TextUtils.isEmpty(user.getReferralCode())) {
            params.put("user_referral_code", user.getReferralCode());
        }
        if (user.getLastName() != null) {
            params.put("last_name", user.getLastName());
        }
        if (user.getLastName() != null) {
            params.put("first_name", user.getFirstName());
        }
        if (user.getEmail() != null) {
            params.put("email", user.getEmail());
        }
        if (user.getPhoneNumber() != null) {
            params.put("phone_number", user.getPhoneNumber());
        }
        if (user.getPassword() != null) {
            params.put("password", user.getPassword());
        }
        if (user.getBirthDay() != null) {
            params.put("birthday", user.getEmail());
        }
        if (user.getLocale() != null) {
            params.put("locale", user.getLocale());
        }
        if (user.getPhoto() != null) {
            params.put("cover", user.getPhoto());
        }
        if (user.getFacebookUid() != null) {
            params.put("social_media_id", user.getFacebookUid());
        }
        if (user.getFacebookAccessToken() != null) {
            params.put("fb_access_token", user.getFacebookAccessToken());
        }
        if (user.getLocale() != null) {
            params.put("locale", user.getLocale());
        }
        if (user.getLocale() != null) {
            params.put("timezone", user.getTimezone());
        }
        if (user.getLocale() != null) {
            params.put("gender", user.getGender());
        }

        if (user.getGoogleToken() != null) {
            params.put("google_id_token", user.getGoogleToken());
        }

        params.put("fb_friend_count", user.getFacebookFriendCount());
        params.put("age", user.getAge());

        KLog.v("--------------------------------------------Request Register Param Start----------------------------------------------------");
        KLog.json(params.toString());
        KLog.v("--------------------------------------------Response Register Param End----------------------------------------------------");

        return mUserService.requestRegister(getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<Google2faResponse>> requestGoogle2faCode(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mUserService.requestGoogleAuthCode(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<Google2faResponse>>());
    }

    public Observable<HttpResponse<ExchangeResponse>> requestExchangeList(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mOtherService.getExchangeList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<ExchangeResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestResetPass(String email) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("email", email);

        return mUserService.requestResetPassword(getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<ResetPasswordCodeResponse>> requestResetPassCodeValidation(String email, String code) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("email", email);
        params.put("reset_password_code", code);

        return mUserService.requestResetPasswordCodeValidation(getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<ResetPasswordCodeResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestResetPassTokenValidation(String email, String token, String password, String confirmPassword) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("email", email);
        params.put("reset_password_token", token);
        params.put("new_password", password);
        params.put("new_password_confirmation", confirmPassword);

        return mUserService.requestResetPasswordTokenValidation(getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestChangePass(int id, String oldpass, String pass) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("password", pass);
        if (!TextUtils.isEmpty(oldpass))
            params.put("old_password", oldpass);

        return mUserService.requestChangePassword(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestGoogle2faUpdateState(int id, String authCode, String token, boolean isSetToEnable) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("authenticator_code", authCode);
        params.put("token", token);

        if (isSetToEnable)
            return mUserService.requestGoogleAuthEnable(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
        else
            return mUserService.requestGoogleAuthDisable(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<Kyc>> requestKyc(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mUserService.requestKyc(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<Kyc>>());
    }

    public Observable<HttpResponse<VexPointResponse>> requestVexPoint(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mUserService.getUserVexPoint(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VexPointResponse>>());
    }

    public Observable<HttpResponse<BannerResponse>> requestBanner(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mOtherService.getBanners(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<BannerResponse>>());
    }

    public Observable<HttpResponse<BigBannerResponse>> requestBigBanner(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mOtherService.getBigBanners(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<BigBannerResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> checkToken(int id, String token) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("token", token);

        KLog.v("--------------------------------------------Request checkToken Param Start----------------------------------------------------");
        KLog.json(params.toString());
        KLog.v("--------------------------------------------Response checkToken Param End----------------------------------------------------");

        return mUserService.checkGoogleAuthToken(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<Kyc>> submitKyc(Kyc kyc) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", kyc.getId());
        params.put("identity_country", kyc.getIdCountry());
        params.put("identity_type", kyc.getIdType());
        params.put("identity_name", kyc.getIdName());
        params.put("identity_id", kyc.getIdNumber());
        params.put("identity_image_front", kyc.getIdImageFront());
        params.put("identity_image_back", kyc.getIdImageBack());
        params.put("identity_image_selfie", kyc.getIdImageSelfie());

        File frontImageFile;
        try {
            frontImageFile = new Resizer(App.getContext()).setTargetLength(1080).setQuality(80).setSourceImage(new File(kyc.getIdImageFront())).getResizedFile();
        } catch (Exception e) {
            frontImageFile = new File(kyc.getIdImageFront());
        }
        RequestBody frontReqFile = RequestBody.create(MediaType.parse("image/*"), frontImageFile);
        MultipartBody.Part frontBody = MultipartBody.Part.createFormData("identity_image_front", "identity_image_front", frontReqFile);

        File backImageFile;
        try {
            backImageFile = new Resizer(App.getContext()).setTargetLength(1080).setQuality(80).setSourceImage(new File(kyc.getIdImageBack())).getResizedFile();
        } catch (Exception e) {
            backImageFile = new File(kyc.getIdImageFront());
        }
        RequestBody backReqFile = RequestBody.create(MediaType.parse("image/*"), backImageFile);
        MultipartBody.Part backBody = MultipartBody.Part.createFormData("identity_image_back", "identity_image_back", backReqFile);

        File selfieImageFile;
        try {
            selfieImageFile = new Resizer(App.getContext()).setTargetLength(1080).setQuality(80).setSourceImage(new File(kyc.getIdImageSelfie())).getResizedFile();
        } catch (Exception e) {
            selfieImageFile = new File(kyc.getIdImageFront());
        }
        RequestBody selfieReqFile = RequestBody.create(MediaType.parse("image/*"), selfieImageFile);
        MultipartBody.Part selfieBody = MultipartBody.Part.createFormData("identity_image_selfie", "identity_image_selfie", selfieReqFile);

        KLog.v("--------------------------------------------Request submitKyc Param Start----------------------------------------------------");
        KLog.json(params.toString());
        KLog.v("--------------------------------------------Response submitKyc Param End----------------------------------------------------");

        return mUserService.submitKyc(getApiKey(), getCacheControl(), getLocale(), params, frontBody, backBody, selfieBody).compose(new BaseSchedulerTransformer<HttpResponse<Kyc>>());
    }

    public Observable<HttpResponse<UserAddressResponse>> requestGetActAddress(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mUserService.getActAddress(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserAddressResponse>>());
    }

    public Observable<HttpResponse<SettingResponse>> requestSettings(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mOtherService.getSettings(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<SettingResponse>>());
    }

    public Observable<HttpResponse<SettingResponse>> requestAppStatus() {
        Map<String, Object> params = Api.getBasicParam();

        return mOtherService.getAppStatus(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<SettingResponse>>());
    }

    public Observable<HttpResponse<UserAddressResponse>> requestSetActAddress(int id, String token, String actAddress) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("token", token);
        params.put("act_address", actAddress);

        return mUserService.setActAddress(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserAddressResponse>>());
    }

    public Observable<HttpResponse<PaymentTypeResponse>> requestPaymentTypes(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("limit", 50);

        return mVoucherService.getPaymentTypes(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<PaymentTypeResponse>>());
    }

    public Observable<HttpResponse<MemberTypeResponse>> requestMemberTypes(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("limit", 50);

        return mVoucherService.getMemberTypes(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<MemberTypeResponse>>());
    }

    public Observable<HttpResponse<VoucherTypeResponse>> requestVoucherTypes(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("limit", 50);

        return mVoucherService.getVoucherTypes(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherTypeResponse>>());
    }

    public Observable<HttpResponse<CategoryResponse>> requestCategories(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("limit", 50);

        return mVoucherService.getCategories(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<CategoryResponse>>());
    }

    public Observable<HttpResponse<VouchersResponse>> requestVoucherList(int id, int voucherType) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("limit", 50);
        if (voucherType != 0) {
            params.put("voucher_type_id", voucherType);
        }

        return mVoucherService.getVoucherList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VouchersResponse>>());
    }

    public Observable<HttpResponse<BestVoucherResponse>> requestBestVoucherList(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("limit", 50);

        return mVoucherService.getBestVoucherList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<BestVoucherResponse>>());
    }

    public Observable<HttpResponse<FeaturedVoucherResponse>> requestFeaturedVoucherList(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("limit", 50);

        return mVoucherService.getFeaturedVoucherList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<FeaturedVoucherResponse>>());
    }

    public Observable<HttpResponse<UserVouchersResponse>> requestUserVoucherList(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mVoucherService.getUserVoucherList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserVouchersResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestBuyVoucher(int userId, int voucherId, String token) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_id", voucherId);
        params.put("token", token);

        return mVoucherService.requestBuyVoucher(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<VoucherCodeResponse>> requestRedeemVoucher(int userId, int voucherCodeId, String vendorCode, String voucherCode, int voucherId, String address) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);

        if (!TextUtils.isEmpty(vendorCode))
            params.put("vendor_code", vendorCode);

        params.put("voucher_code", "");
        params.put("voucher_id", voucherId);

        if (!TextUtils.isEmpty(address))
            params.put("address", address);


        return mVoucherService.requestRedeemVoucher(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherCodeResponse>>());
    }

    public Observable<HttpResponse<VoucherCodeResponse>> requestDeactivateVoucher(int userId, int voucherCodeId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);

        return mVoucherService.requestDeactivateVoucher(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherCodeResponse>>());
    }

    public Observable<HttpResponse<VoucherCodeResponse>> requestArchiveVoucher(int userId, int voucherCodeId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);

        return mVoucherService.requestArchiveVoucher(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherCodeResponse>>());
    }

    public Observable<HttpResponse<VoucherGiftCodeResponse>> requestGetGiftCode(int userId, int voucherCodeId, String token) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);
        params.put("token", token);

        return mVoucherService.requestGetGiftCode(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherGiftCodeResponse>>());
    }

    public Observable<HttpResponse<VoucherCodeResponse>> requestClaimGiftCode(int userId, int voucherCodeId, String voucherGiftCode) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);
        params.put("voucher_gift_code", voucherGiftCode);

        return mVoucherService.requestClaimGiftCode(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherCodeResponse>>());
    }

    public Observable<HttpResponse<PremiumListResponse>> requestPremiumList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getPremiumList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<PremiumListResponse>>());
    }

    public Observable<HttpResponse<PremiumPurchaseResponse>> purchasePremium(int userId, int duration, int price, String currency) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("duration", duration);
        params.put("price", price);
        params.put("currency", currency);

        return mOtherService.purcasePremiumMember(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<PremiumPurchaseResponse>>());
    }

    public Observable<HttpResponse<PremiumHistoryResponse>> requestUserPremiumHistory(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getPremiumHistoryList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<PremiumHistoryResponse>>());
    }

    public Observable<HttpResponse<PremiumDueDateResponse>> requestUserPremiumDueDate(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getPremiumDueDate(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<PremiumDueDateResponse>>());
    }

    public Observable<HttpResponse<UserLoginResponse>> requestEmailConfirmation(int userId, String code) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("email_confirmation_code", code);

        return mUserService.requestEmailConfirmation(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserLoginResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestResendEmail(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mUserService.requestResendEmailConfirmation(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<UserLoginResponse>> requestSmsConfirmation(int userId, String code) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("phone_number_confirmation_code", code);

        return mUserService.requestSmsConfirmation(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserLoginResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestResendSms(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mUserService.requestResendSmsConfirmation(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestUpdateNotificationId(String sess, int userId, String notification_id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("notification_id", notification_id);

        return mUserService.requestUpdateNotificationId(sess, getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<VexPointRecordResponse>> requesVpLog(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mUserService.getVexPointLog(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VexPointRecordResponse>>());
    }

    public Observable<HttpResponse<UserReferralResponse>> requestUserReferrals(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mUserService.getUserReferrals(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserReferralResponse>>());
    }

    public Observable<HttpResponse<NotificationResponse>> requestUserNotification(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mUserService.getUserNotification(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<NotificationResponse>>());
    }

    public Observable<HttpResponse<CountriesResponse>> requestCountryList() {
        Map<String, Object> params = Api.getBasicParam();

        return mOtherService.getCountries(getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<CountriesResponse>>());
    }

    public Observable<HttpResponse<DepositListResponse>> requestDepositList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getDeposits(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<DepositListResponse>>());
    }

    public Observable<HttpResponse<UserDepositResponse>> requestUserDepositList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getDepositHistory(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserDepositResponse>>());
    }

    public Observable<HttpResponse<VexVaultResponse>> requestTokenFreeze(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("limit", 100);

        return mOtherService.getTokenFreeze(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<VexVaultResponse>>());
    }

    public Observable<HttpResponse<UserDepositSingleResponse>> requestDeposit(int userId, int depositId, int depositOptionId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("deposit_id", depositId);
        params.put("deposit_option_id", depositOptionId);

        return mOtherService.requestDeposit(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserDepositSingleResponse>>());
    }

    public Observable<HttpResponse<BuybackPaymentResponse>> requestDoBuyback(int userId, int buyBackId, int buyBackOptionId, float amount) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("buy_back_id", buyBackId);
        params.put("buy_back_option_id", buyBackOptionId);
        params.put("amount", amount);

        return mOtherService.doBuyback(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<BuybackPaymentResponse>>());
    }

    public Observable<HttpResponse<BuybackResponse>> requestBuybackList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getBuyback(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<BuybackResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestUpdateBuybackDistributionAddress(int userId, int buybackPaymentId, String address) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("user_buy_back_id", buybackPaymentId);
        params.put("distribution_address", address);

        return mOtherService.updateBuybackDistributionAddress(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<BuybackHistoryResponse>> requestBuybackHistoryList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getBuybackHistories(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<BuybackHistoryResponse>>());
    }

    public Observable<HttpResponse<TokenSaleResponse>> requestTokenSaleList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getTokenSales(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<TokenSaleResponse>>());
    }

    public Observable<HttpResponse<TokenSaleHistoryResponse>> requestTokenSaleHistoryList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getTokenSaleHistories(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<TokenSaleHistoryResponse>>());
    }


    public Observable<HttpResponse<TokenSalePaymentResponse>> requestBuyTokenSale(int userId, int tokenSaleId, int tokenSalePaymentOptionId, float amount) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("token_sale_id", tokenSaleId);
        params.put("token_sale_payment_option_id", tokenSalePaymentOptionId);
        params.put("amount", amount);

        return mOtherService.buyTokenSales(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<TokenSalePaymentResponse>>());
    }

    public Observable<HttpResponse<TokenSaleHistoryDetailResponse>> requestTokenSalePayment(int userId, int tokenSalePaymentId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("token_sale_payment_id", tokenSalePaymentId);

        return mOtherService.getTokenSalePayment(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<TokenSaleHistoryDetailResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestUpdateDistributionAddress(int userId, int tokenSalePaymentId, String address) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("token_sale_payment_id", tokenSalePaymentId);
        params.put("distribution_address", address);

        return mOtherService.updateDistributionAddress(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<LuckyDrawListResponse>> requestLuckyDrawList(int userId, int limit, int offset, int luckyDrawCategoryId, int memberTypeId, int paymentTypeId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        if (limit > -1) {
            params.put("limit", limit);
        }

        if (offset > -1) {
            params.put("offset", offset);
        }

        if (luckyDrawCategoryId > -1) {
            params.put("luck_draw_category_id", luckyDrawCategoryId);
        }

        if (memberTypeId > -1) {
            params.put("member_type_id", memberTypeId);
        }

        if (paymentTypeId > -1) {
            params.put("payment_type_id", paymentTypeId);
        }

        return mOtherService.getLuckyDrawList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<LuckyDrawListResponse>>());
    }

    public Observable<HttpResponse<LuckyDrawResponse>> requestLuckyDraw(int userId, int luckyDrawId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("luck_draw_id", luckyDrawId);

        return mOtherService.getLuckyDraw(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<LuckyDrawResponse>>());
    }

    public Observable<HttpResponse<NewsResponse>> requestNews(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getNews(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<NewsResponse>>());
    }

    public Observable<HttpResponse<UserInputDataResponse>> requestInput(int userId, String input) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("digifinex_email", input);

        return mOtherService.requestUserInput(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserInputDataResponse>>());
    }

    public Observable<HttpResponse<UserInputDataResponse>> getUserInputData(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getUserInputData(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserInputDataResponse>>());
    }

    public Observable<HttpResponse<UserLuckyDrawListResponse>> requestUserLuckyDrawList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getUserLuckyDrawList(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserLuckyDrawListResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> buyLuckyDraw(int userId, int luckyDrawId, int amount, String token) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("lucky_draw_id", luckyDrawId);
        params.put("amount", amount);
        params.put("token", token);

        return mOtherService.buyLuckyDraw(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<UserLuckyDrawResponse>> setUserLuckyDrawAddress(int userId, int userLuckyDrawId, String address) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("user_luck_draw_id", userLuckyDrawId);
        params.put("address", address);

        return mOtherService.setUserLuckyDrawAddress(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserLuckyDrawResponse>>());
    }

    public Observable<HttpResponse<AffiliateProgramEntryResponse>> submitAffiliateProgramEntry(int userId, int affliateProgramId, String vals) {
        Map<String, Object> params = Api.getBasicParam();

        Map<String, Object> pVal = (Map<String, Object>) JsonUtil.toObject(vals, HashMap.class);
        params.putAll(pVal);

        params.put("user_id", userId);
        params.put("affiliate_program_id", affliateProgramId);

        return mOtherService.submitAffiliateProgramEntry(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<AffiliateProgramEntryResponse>>());
    }

    public Observable<HttpResponse<AffiliateProgramEntryResponse>> getAffiliateProgramEntries(int userId, int affliateProgramId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("affiliate_program_id", affliateProgramId);

        return mOtherService.getAffiliateProgramEntries(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<AffiliateProgramEntryResponse>>());
    }

    public Observable<HttpResponse<AffiliateProgram>> getAffiliateProgram(int userId, int affliateProgramId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("affiliate_program_id", affliateProgramId);

        return mOtherService.getAffiliateProgram(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<AffiliateProgram>>());
    }

    public Observable<HttpResponse<AffiliateProgramResponse>> getAffiliatePrograms(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mOtherService.getAffiliatePrograms(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<AffiliateProgramResponse>>());
    }

    public Observable<HttpResponse<WalletResponse>> getWallet(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mWalletService.getWallet(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<WalletResponse>>());
    }

    public Observable<HttpResponse<WalletReferralResponse>> getWalletReferral(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mWalletService.getWalletReferral(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<WalletReferralResponse>>());
    }

    public Observable<HttpResponse<WalletResponse>> createWallet(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);

        return mWalletService.createWallet(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<WalletResponse>>());
    }

    public Observable<HttpResponse<WithdrawResponse>> doWithdraw(int userId, int walletId, float amount, String destinationAddress) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("wallet_id", walletId);
        params.put("amount", amount);
        params.put("destination_address", destinationAddress);

        return mWalletService.doWithdraw(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<WithdrawResponse>>());
    }

    public Observable<HttpResponse<WithdrawResponse>> cancelWithdraw(int userId, int withdrawId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("wallet_withdrawal_id", withdrawId);

        return mWalletService.cancelWithdraw(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<WithdrawResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> updateUserProfile(int userId, String userName) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("name", userName);

        return mUserService.updateUserProfile(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }


//    public Observable<HttpResponse<DigifinexReferralResponse>> setDigifinexEmailReferral(int userId, String address) {
//        Map<String, Object> params = Api.getBasicParam();
//
//        params.put("user_id", userId);
//        params.put("digifinex_email",address);
//
//        return mOtherService.submitDigifinexEmailReferred(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<DigifinexReferralResponse>>());
//    }
//
//    public Observable<HttpResponse<DigifinexReferralListResponse>> getDigifinexEmailReferralList(int userId) {
//        Map<String, Object> params = Api.getBasicParam();
//
//        params.put("user_id", userId);
//
//        return mOtherService.getUserDigifinexEmailReferred(getApiKey(), getCacheControl(), getLocale(), params).compose(new BaseSchedulerTransformer<HttpResponse<DigifinexReferralListResponse>>());
//    }

}
