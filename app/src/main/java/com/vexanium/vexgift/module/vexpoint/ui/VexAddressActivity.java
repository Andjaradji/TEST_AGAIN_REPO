package com.vexanium.vexgift.module.vexpoint.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenter;
import com.vexanium.vexgift.module.vexpoint.presenter.IVexpointPresenterImpl;
import com.vexanium.vexgift.module.vexpoint.view.IVexpointView;
import com.vexanium.vexgift.module.voucher.ui.VoucherRedeemActivity;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_vex_address, toolbarTitle = R.string.vexpoint_fill_address)
public class VexAddressActivity extends BaseActivity<IVexpointPresenter> implements IVexpointView {

    User user;
    private Subscription timeSubsription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IVexpointPresenterImpl(this);

        user = User.getCurrentUser(this);

        updateView();

        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.btn_copy).setOnClickListener(this);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if(data != null){
//            handleVexAddress();
            mPresenter.requestGetActAddress(user.getId());
        }else if (errorResponse != null){

        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_submit:
                doSubmitAddress();
                break;
            case R.id.btn_copy:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onPause() {
        KLog.v("VoucherRedeemActivity", "onPause: ");
        super.onPause();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
            timeSubsription = null;
        }
    }

    @Override
    protected void onStart() {
        KLog.v("VoucherRedeemActivity", "onStart: ");
        super.onStart();
        if (!User.isVexAddVerifTimeEnded())
            startDateTimer();
    }

    @Override
    protected void onDestroy() {
        KLog.v("VoucherRedeemActivity", "onDestroy: ");
        super.onDestroy();
        if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
            timeSubsription.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        KLog.v("VoucherRedeemActivity", "onResume: ");
        super.onResume();
        if (!User.isVexAddVerifTimeEnded())
            startDateTimer();
    }

    private void doSubmitAddress() {
        boolean isValid = ViewUtil.validateEmpty(this, getString(R.string.validate_empty_field), R.id.et_vex_address, R.id.et_ga_pin);
        if (isValid) {
            String address = ((EditText) findViewById(R.id.et_vex_address)).getText().toString();
            String token = ((EditText) findViewById(R.id.et_ga_pin)).getText().toString();
            mPresenter.requestSetActAddress(user.getId(), address, token);
        }
    }

    private void handleVexAddress() {
        User.setVexAddressSubmitTime();
        updateView();
    }

    private void updateView() {
        if (User.isVexAddVerifTimeEnded()) {
            KLog.v("VexAddressActivity","updateView: HPtes vex address verif");
            findViewById(R.id.ll_fill_address).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_wait_address).setVisibility(View.GONE);

            if(user.isAuthenticatorEnable());

        } else {
            KLog.v("VexAddressActivity","updateView: HPtes vex address verif ended");
            findViewById(R.id.ll_fill_address).setVisibility(View.GONE);
            findViewById(R.id.ll_wait_address).setVisibility(View.VISIBLE);
        }
    }

    private void startDateTimer() {
        KLog.v("VoucherRedeemActivity", "startDateTimer: HPtes timer");
        if (timeSubsription == null && StaticGroup.isScreenOn(this, true)) {
            timeSubsription = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            if (!StaticGroup.isScreenOn(VexAddressActivity.this, true)) {
                                if (timeSubsription != null && !timeSubsription.isUnsubscribed()) {
                                    timeSubsription.unsubscribe();
                                }
                            } else {
                                setWatchText();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                        }
                    });
        }
    }


    private void setWatchText() {
        TextView tvHour = findViewById(R.id.tv_time_left);

        Calendar now = Calendar.getInstance();
        Calendar finish = Calendar.getInstance();
        finish.setTimeInMillis(12220);

        long remainTime = finish.getTimeInMillis() - now.getTimeInMillis();

        String day = String.format(getString(R.string.days), TimeUnit.MILLISECONDS.toDays(remainTime));

        long dayInMilis = TimeUnit.DAYS.toMillis(TimeUnit.MILLISECONDS.toDays(remainTime));
        remainTime = remainTime - dayInMilis;

        String time = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(remainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainTime)));
        tvHour.setText(time);
    }
}
