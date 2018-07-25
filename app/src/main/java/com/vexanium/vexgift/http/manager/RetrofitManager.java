package com.vexanium.vexgift.http.manager;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseSchedulerTransformer;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.http.Api;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.interceptor.RxErrorHandlingCallAdapterFactory;
import com.vexanium.vexgift.http.services.UserService;
import com.vexanium.vexgift.util.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    private UserService mUserService;

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

        mUserService = retrofit.create(UserService.class);
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
    private String getApiKey(){
        return Api.API_KEY;
    }

    public Observable<HttpResponse<UserLoginResponse>> requestLogin(User user) {
        Map<String, Object> params = Api.getBasicParam();

        if (user.getFamilyName() != null) {
            params.put("last_name", user.getFamilyName());
        }
        if (user.getEmail() != null) {
            params.put("email", user.getEmail());
        }
        if (user.getPassword() != null) {
            params.put("password", user.getPassword());
        }
        if (user.getDescription() != null) {
            params.put("description", user.getDescription());
        }
        if (user.getLocale() != null) {
            params.put("locale", user.getLocale());
        }
        if (user.getPhoto() != null) {
            params.put("photo", user.getPhoto());
        }
        if (user.getFacebookUid() != null) {
            params.put("fb_uid", user.getFacebookUid());
        }
        if (user.getFacebookAccessToken() != null) {
            params.put("fb_token", user.getFacebookAccessToken());
        }
        if (user.getLocale() != null) {
            params.put("fb_loc", user.getLocale());
        }

        params.put("fb_friend_count", user.getFacebookFriendCount());
        params.put("age", user.getAge());

        return mUserService.requestLogin(getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserLoginResponse>>());
    }

    public Observable<HttpResponse<UserLoginResponse>> requestRegister(User user) {
        Map<String, Object> params = Api.getBasicParam();

        if (user.getFamilyName() != null) {
            params.put("last_name", user.getFamilyName());
        }
        if (user.getEmail() != null) {
            params.put("email", user.getEmail());
        }
        if (user.getPassword() != null) {
            params.put("password", user.getPassword());
        }
        if (user.getDescription() != null) {
            params.put("description", user.getDescription());
        }
        if (user.getLocale() != null) {
            params.put("locale", user.getLocale());
        }
        if (user.getPhoto() != null) {
            params.put("photo", user.getPhoto());
        }
        if (user.getFacebookUid() != null) {
            params.put("fb_uid", user.getFacebookUid());
        }
        if (user.getFacebookAccessToken() != null) {
            params.put("fb_token", user.getFacebookAccessToken());
        }
        if (user.getLocale() != null) {
            params.put("fb_loc", user.getLocale());
        }

        params.put("fb_friend_count", user.getFacebookFriendCount());
        params.put("age", user.getAge());

        return mUserService.requestRegister(getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<UserLoginResponse>>());
    }

    public Observable<HttpResponse<Google2faResponse>> requestGoogle2faCode(int id){
        Map<String, Object> params = Api.getBasicParam();

        params.put("user_id", id);

        return mUserService.requestGoogleAuthCode(getApiKey(), getCacheControl(), params).compose(new BaseSchedulerTransformer<HttpResponse<Google2faResponse>>());
    }
}
