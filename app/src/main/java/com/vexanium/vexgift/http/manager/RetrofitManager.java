package com.vexanium.vexgift.http.manager;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseSchedulerTransformer;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.PremiumListResponse;
import com.vexanium.vexgift.bean.response.UserAddressResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.bean.response.UserVouchersResponse;
import com.vexanium.vexgift.bean.response.VoucherCodeResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.http.Api;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.interceptor.RxErrorHandlingCallAdapterFactory;
import com.vexanium.vexgift.http.services.OtherService;
import com.vexanium.vexgift.http.services.UserService;
import com.vexanium.vexgift.http.services.VoucherService;
import com.vexanium.vexgift.util.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
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

        params.put("fb_friend_count", user.getFacebookFriendCount());
        params.put("age", user.getAge());

        KLog.v("--------------------------------------------Request Login Param Start----------------------------------------------------");
        KLog.json(params.toString());
        KLog.v("--------------------------------------------Response Login Param End----------------------------------------------------");

        return mUserService.requestLogin(getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserLoginResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestRegister(User user) {
        Map<String, Object> params = Api.getBasicParam();

        if (user.getName() != null) {
            params.put("name", user.getName());
        }else{
            params.put("name","undefined");
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

        return mUserService.requestRegister(getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<Google2faResponse>> requestGoogle2faCode(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mUserService.requestGoogleAuthCode(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<Google2faResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestChangePass(int id, String oldpass, String pass) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("password", pass);
        params.put("old_password", oldpass);

        return mUserService.requestChangePassword(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestGoogle2faUpdateState(int id, String authCode, String token, boolean isSetToEnable) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("authenticator_code", authCode);
        params.put("token", token);

        if (isSetToEnable)
            return mUserService.requestGoogleAuthEnable(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
        else
            return mUserService.requestGoogleAuthDisable(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<Kyc>> requestKyc(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mUserService.requestKyc(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<Kyc>>());
    }

    public Observable<HttpResponse<EmptyResponse>> checkToken(int id, String token) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("token", token);

        KLog.v("--------------------------------------------Request checkToken Param Start----------------------------------------------------");
        KLog.json(params.toString());
        KLog.v("--------------------------------------------Response checkToken Param End----------------------------------------------------");

        return mUserService.checkGoogleAuthToken(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
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

        return mUserService.submitKyc(getApiKey(), getCacheControl(), params, frontBody, backBody, selfieBody).compose(new BaseSchedulerTransformer<HttpResponse<Kyc>>());
    }

    public Observable<HttpResponse<UserAddressResponse>> requestGetActAddress(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mUserService.getActAddress(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserAddressResponse>>());
    }

    public Observable<HttpResponse<UserAddressResponse>> requestSetActAddress(int id, String token, String actAddress) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("token", token);
        params.put("act_address", actAddress);

        return mUserService.setActAddress(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserAddressResponse>>());
    }

    public Observable<HttpResponse<VouchersResponse>> requestVoucherList(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);
        params.put("limit", 50);

        return mVoucherService.getVoucherList(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<VouchersResponse>>());
    }

    public Observable<HttpResponse<UserVouchersResponse>> requestUserVoucherList(int id) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mVoucherService.getUserVoucherList(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserVouchersResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestBuyVoucher(int userId, int voucherId, String token) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_id", voucherId);
        params.put("token", token);

        return mVoucherService.requestBuyVoucher(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

    public Observable<HttpResponse<VoucherCodeResponse>> requestRedeemVoucher(int userId, int voucherCodeId, String vendorCode, String voucherCode, int voucherId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);
        params.put("vendorCode", vendorCode);
        params.put("voucherCode", "");
        params.put("voucher_id", voucherId);

        return mVoucherService.requestRedeemVoucher(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherCodeResponse>>());
    }

    public Observable<HttpResponse<VoucherCodeResponse>> requestDeactivateVoucher(int userId, int voucherCodeId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);

        return mVoucherService.requestDeactivateVoucher(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherCodeResponse>>());
    }

    public Observable<HttpResponse<VoucherCodeResponse>> requestGetGiftCode(int userId, int voucherCodeId, String token) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);
        params.put("token", token);

        return mVoucherService.requestGetGiftCode(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherCodeResponse>>());
    }

    public Observable<HttpResponse<VoucherCodeResponse>> requestClaimGiftCode(int userId, int voucherCodeId, String token) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("voucher_code_id", voucherCodeId);
        params.put("token", token);

        return mVoucherService.requestClaimGiftCode(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<VoucherCodeResponse>>());
    }

    public Observable<HttpResponse<PremiumListResponse>> requestPremiumList(int userId) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        Log.e("idkey", getApiKey());
        return mOtherService.getPremiumList(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<PremiumListResponse>>());
    }

    public Observable<HttpResponse<EmptyResponse>> requestEmailConfirmation(int userId, String code) {
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", userId);
        params.put("email_confirmation_code", userId);

        return mUserService.requestEmailConfirmation(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<EmptyResponse>>());
    }

}
