package com.vexanium.vexgift.module.home.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.response.BannerResponse;
import com.vexanium.vexgift.bean.response.BestVoucherResponse;
import com.vexanium.vexgift.bean.response.FeaturedVoucherResponse;
import com.vexanium.vexgift.bean.response.PremiumDueDateResponse;
import com.vexanium.vexgift.bean.response.VexPointResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import java.util.Collections;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IHomeInteractorImpl implements IHomeInteractor {
    @Override
    public Subscription requestVoucherList(RequestCallback callback, int id) {

        return RetrofitManager.getInstance(HostType.COMMON_API).requestVoucherList(id, 0).compose(RxUtil.<VouchersResponse>handleResult())
                .flatMap(new Func1<VouchersResponse, Observable<VouchersResponse>>() {
                    @Override
                    public Observable<VouchersResponse> call(VouchersResponse vouchersResponse) {
                        Collections.shuffle(vouchersResponse.getVouchers());
                        return Observable.just(vouchersResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestKyc(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestKyc(id).compose(RxUtil.<Kyc>handleResult())
                .flatMap(new Func1<Kyc, Observable<Kyc>>() {
                    @Override
                    public Observable<Kyc> call(Kyc kyc) {

                        KLog.json("HPtes", JsonUtil.toString(kyc));
                        return Observable.just(kyc);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestBestVoucherList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestBestVoucherList(id).compose(RxUtil.<BestVoucherResponse>handleResult())
                .flatMap(new Func1<BestVoucherResponse, Observable<?>>() {
                    @Override
                    public Observable<BestVoucherResponse> call(BestVoucherResponse bestVoucherResponse) {
                        return Observable.just(bestVoucherResponse);
                    }
                }).subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestFeaturedVoucherList(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestFeaturedVoucherList(id).compose(RxUtil.<FeaturedVoucherResponse>handleResult())
                .flatMap(new Func1<FeaturedVoucherResponse, Observable<?>>() {
                    @Override
                    public Observable<FeaturedVoucherResponse> call(FeaturedVoucherResponse bestVoucherResponse) {
                        return Observable.just(bestVoucherResponse);
                    }
                }).subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestPremiumDueDate(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestUserPremiumDueDate(id).compose(RxUtil.<PremiumDueDateResponse>handleResult())
                .flatMap(new Func1<PremiumDueDateResponse, Observable<PremiumDueDateResponse>>() {
                    @Override
                    public Observable<PremiumDueDateResponse> call(PremiumDueDateResponse premiumDueDateResponse) {

                        KLog.json("HPtes", JsonUtil.toString(premiumDueDateResponse));
                        return Observable.just(premiumDueDateResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestUserVexPoint(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestVexPoint(id).compose(RxUtil.<VexPointResponse>handleResult())
                .flatMap(new Func1<VexPointResponse, Observable<VexPointResponse>>() {
                    @Override
                    public Observable<VexPointResponse> call(VexPointResponse vexPointResponse) {

                        KLog.json("HPtes", JsonUtil.toString(vexPointResponse));
                        return Observable.just(vexPointResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription requestBanner(RequestCallback callback, int id) {
        return RetrofitManager.getInstance(HostType.COMMON_API).requestBanner(id).compose(RxUtil.<BannerResponse>handleResult())
                .flatMap(new Func1<BannerResponse, Observable<BannerResponse>>() {
                    @Override
                    public Observable<BannerResponse> call(BannerResponse bannerResponse) {

                        KLog.json("HPtes", JsonUtil.toString(bannerResponse));
                        return Observable.just(bannerResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
