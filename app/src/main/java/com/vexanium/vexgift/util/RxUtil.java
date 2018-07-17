package com.vexanium.vexgift.util;

import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.http.ApiException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
/**
 * Created by hizkia on 01/03/18.
 */

public class RxUtil {
    public static <T> Observable.Transformer<HttpResponse<T>, T> handleResult() {
        return new Observable.Transformer<HttpResponse<T>, T>() {
            @Override
            public Observable<T> call(Observable<HttpResponse<T>> httpResponseObservable) {
                return httpResponseObservable.flatMap(new Func1<HttpResponse<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(HttpResponse<T> tHttpResponse) {
//                        if(tHttpResponse.getCode() > 0) {
//                            return Observable.error(new ApiException(tHttpResponse.getMsg()));
//                        } else {
//                            return createData(tHttpResponse.getData());
//                        }
                        return null;
                    }
                });
            }
        };
    }

    public static <T> Observable<T> createData(final T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
