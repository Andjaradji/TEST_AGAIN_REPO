package com.vexanium.vexgift.module.luckydraw.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.response.BestVoucherResponse;
import com.vexanium.vexgift.bean.response.FeaturedVoucherResponse;
import com.vexanium.vexgift.bean.response.LuckyDrawResponse;
import com.vexanium.vexgift.bean.response.PremiumDueDateResponse;
import com.vexanium.vexgift.bean.response.VexPointResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.module.home.model.IHomeInteractor;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import java.util.Collections;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class ILuckyDrawInteractorImpl implements ILuckyDrawInteractor {

    @Override
    public Subscription requestLuckyDraw(RequestCallback callback, int id, int limit, int offset, int luckyDrawCategoryId, int memberTypeId, int paymentTypeId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestLuckyDrawList(id, limit,offset,luckyDrawCategoryId,memberTypeId,paymentTypeId).compose(RxUtil.<LuckyDrawResponse>handleResult())
                .flatMap(new Func1<LuckyDrawResponse, Observable<LuckyDrawResponse>>() {
                    @Override
                    public Observable<LuckyDrawResponse> call(LuckyDrawResponse luckyDrawResponse) {
                        return Observable.just(luckyDrawResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
