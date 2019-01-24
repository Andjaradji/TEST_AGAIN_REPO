package com.vexanium.vexgift.module.main.model;

import com.socks.library.KLog;
import com.vexanium.vexgift.base.BaseSubscriber;
import com.vexanium.vexgift.bean.model.AffiliateEntry;
import com.vexanium.vexgift.bean.model.AffiliateProgram;
import com.vexanium.vexgift.bean.response.AffiliateProgramEntryResponse;
import com.vexanium.vexgift.bean.response.AffiliateProgramResponse;
import com.vexanium.vexgift.bean.response.UserInputDataResponse;
import com.vexanium.vexgift.callback.RequestCallback;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class IMainInteractorImpl implements IMainInteractor {

    @Override
    public Subscription getAffilateProgram(RequestCallback callback, int userId, int affiliateProgramId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).getAffiliateProgram(userId, affiliateProgramId).compose(RxUtil.<AffiliateProgram>handleResult())
                .flatMap(new Func1<AffiliateProgram, Observable<?>>() {
                    @Override
                    public Observable<?> call(AffiliateProgram affiliateProgram) {
                        return Observable.just(affiliateProgram);
                    }
                }).subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription getAffilatePrograms(RequestCallback callback, int userId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).getAffiliatePrograms(userId).compose(RxUtil.<AffiliateProgramResponse>handleResult())
                .flatMap(new Func1<AffiliateProgramResponse, Observable<?>>() {
                    @Override
                    public Observable<?> call(AffiliateProgramResponse affiliateProgramResponse) {
                        return Observable.just(affiliateProgramResponse);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription submitAffiliateProgramEntry(RequestCallback callback, int user_id, int affiliate_program_id, String vals) {
        return RetrofitManager.getInstance(HostType.COMMON_API).submitAffiliateProgramEntry(user_id, affiliate_program_id, vals).compose(RxUtil.<AffiliateProgramEntryResponse>handleResult())
                .flatMap(new Func1<AffiliateProgramEntryResponse, Observable<AffiliateProgramEntryResponse>>() {
                    @Override
                    public Observable<AffiliateProgramEntryResponse> call(AffiliateProgramEntryResponse affiliateProgramEntry) {

                        return Observable.just(affiliateProgramEntry);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }

    @Override
    public Subscription getAffilateProgramEntries(RequestCallback callback, int id, int affiliateProgramId) {
        return RetrofitManager.getInstance(HostType.COMMON_API).getAffiliateProgramEntries(id, affiliateProgramId).compose(RxUtil.<AffiliateProgramEntryResponse>handleResult())
                .flatMap(new Func1<AffiliateProgramEntryResponse, Observable<AffiliateProgramEntryResponse>>() {
                    @Override
                    public Observable<AffiliateProgramEntryResponse> call(AffiliateProgramEntryResponse response) {

                        KLog.json("HPtes", JsonUtil.toString(response));
                        return Observable.just(response);
                    }
                })
                .subscribe(new BaseSubscriber<>(callback));
    }
}
