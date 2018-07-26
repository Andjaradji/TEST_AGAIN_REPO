package com.vexanium.vexgift.module.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.Kyc;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenter;
import com.vexanium.vexgift.module.profile.presenter.IProfilePresenterImpl;
import com.vexanium.vexgift.module.profile.view.IProfileView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.RxBus;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;

import rx.Observable;
import rx.functions.Action1;

import static com.vexanium.vexgift.app.ConstantGroup.KYC_ACCEPTED;
import static com.vexanium.vexgift.app.ConstantGroup.KYC_NONE;
import static com.vexanium.vexgift.app.ConstantGroup.KYC_PENDING;
import static com.vexanium.vexgift.app.ConstantGroup.KYC_REJECTED;

@ActivityFragmentInject(contentViewId = R.layout.activity_my_profile, toolbarTitle = R.string.myprofile_toolbar_title)
public class MyProfileActivity extends BaseActivity<IProfilePresenter> implements IProfileView {

    private Observable<Boolean> mKycStatusUpdateObservable;
    private int kycStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new IProfilePresenterImpl(this);

        User user = User.getCurrentUser(App.getContext());
        if (user != null) {
            setUserData(user);
            mPresenter.requestKyc(user.getId());
        }

        mKycStatusUpdateObservable = RxBus.get().register("kycStatusUpdate", Boolean.class);
        mKycStatusUpdateObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                User user = User.getCurrentUser(App.getContext());
                if (user != null) {
                    mPresenter.requestKyc(user.getId());
                }
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        KLog.v("MyProfileActivity handleResult : " + JsonUtil.toString(data));
        if (data != null) {
            Kyc kyc = (Kyc) data;
            if (kyc != null) {
                if(kyc.getStatus().equalsIgnoreCase("pending")){
                    setKycInfo(KYC_PENDING);
                }else{
                    setKycInfo(KYC_ACCEPTED);
                }
                setKycContent(kyc);
            }
        } else if (errorResponse != null) {
            KLog.v("MyProfileActivity handleResult error : " + errorResponse.getMeta().getMessage());
            if (errorResponse.getMeta().getStatus() == 404) {
                setKycInfo(KYC_NONE);
            } else {
                toast(errorResponse.getMeta().getStatus() + " : " + errorResponse.getMeta().getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_next:
                Intent intent = new Intent(MyProfileActivity.this, KycActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setUserData(User user) {
        ViewUtil.setText(this, R.id.tv_email, user.getEmail());
    }

    private void setKycContent(Kyc kycContent) {
        ViewUtil.setText(this, R.id.tv_name, kycContent.getIdName());
        ViewUtil.setText(this, R.id.tv_document_type, kycContent.getIdType());
        ViewUtil.setText(this, R.id.tv_id_number, kycContent.getIdNumber());
        ViewUtil.setImageUrl(this, R.id.iv_document_front, kycContent.getIdImageFront(), R.drawable.placeholder);
        ViewUtil.setImageUrl(this, R.id.iv_document_back, kycContent.getIdImageBack(), R.drawable.placeholder);
        ViewUtil.setImageUrl(this, R.id.iv_document_selfie, kycContent.getIdImageSelfie(), R.drawable.placeholder);
    }

    private void setKycInfo(int status) {
        final LinearLayout llInfo = findViewById(R.id.kyc_info);
        final LinearLayout llcontent = findViewById(R.id.kyc_content);
        TextView tvInfo = findViewById(R.id.tv_info);
        TextView tvTitle = findViewById(R.id.tv_info_title);
        TextView tvDesc = findViewById(R.id.tv_info_desc);
        TextView tvNext = findViewById(R.id.tv_next);
        LinearLayout btnNext = findViewById(R.id.btn_next);

        switch (status) {
            case KYC_ACCEPTED:
                llcontent.setVisibility(View.VISIBLE);
                llInfo.setVisibility(View.GONE);
                break;
            case KYC_NONE:
                llcontent.setVisibility(View.GONE);
                llInfo.setVisibility(View.VISIBLE);
                tvInfo.setText(getString(R.string.myprofile_kyc_none));
                tvTitle.setText(getString(R.string.myprofile_kyc_none_title));
                tvDesc.setText(getString(R.string.myprofile_kyc_none_desc));
                tvNext.setText(getString(R.string.myprofile_submit));
                btnNext.setOnClickListener(this);
                break;
            case KYC_PENDING:
                llcontent.setVisibility(View.GONE);
                llInfo.setVisibility(View.VISIBLE);
                tvInfo.setText(getString(R.string.myprofile_kyc_pending));
                tvTitle.setText(getString(R.string.myprofile_kyc_pending_title));
                tvDesc.setText(getString(R.string.myprofile_kyc_pending_desc));
                tvNext.setText(getString(R.string.myprofile_view));
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ClickUtil.isFastDoubleClick())return;
                        llcontent.setVisibility(View.VISIBLE);
                        llInfo.setVisibility(View.GONE);
                    }
                });

                break;
            case KYC_REJECTED:
                llcontent.setVisibility(View.GONE);
                llInfo.setVisibility(View.VISIBLE);
                tvInfo.setText(getString(R.string.myprofile_kyc_rejected));
                tvTitle.setText(getString(R.string.myprofile_kyc_rejected_title));
                tvDesc.setText(getString(R.string.myprofile_kyc_rejected_desc));
                tvNext.setText(getString(R.string.myprofile_submit));
                btnNext.setOnClickListener(this);


                break;
        }
    }
}
