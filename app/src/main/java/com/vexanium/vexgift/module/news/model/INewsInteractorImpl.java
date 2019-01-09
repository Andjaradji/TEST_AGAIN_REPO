package com.vexanium.vexgift.module.news.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.response.Google2faResponse;
import com.vexanium.vexgift.bean.response.NewsResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class INewsInteractorImpl implements INewsInteractor {
    @Override
    public Subscription requestNews(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestNews(id).compose(RxUtil.<NewsResponse>handleResult())
                .flatMap(new Func1<NewsResponse, Observable<NewsResponse>>() {
                    @Override
                    public Observable<NewsResponse> call(NewsResponse response) {

                        KLog.json("HPtes", JsonUtil.toString(response));
                        return Observable.just(response);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
