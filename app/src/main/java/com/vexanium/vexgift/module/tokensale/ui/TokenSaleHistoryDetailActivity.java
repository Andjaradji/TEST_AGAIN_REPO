package com.vexanium.vexgift.module.tokensale.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.TokenSaleHistory;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.TokenSaleHistoryResponse;
import com.vexanium.vexgift.module.tokensale.presenter.ITokenSalePresenter;
import com.vexanium.vexgift.module.tokensale.presenter.ITokenSalePresenterImpl;
import com.vexanium.vexgift.module.tokensale.view.ITokenSaleView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_token_sale_history_detail, toolbarTitle = R.string.token_sale_history_title, withLoadingAnim = true)
public class TokenSaleHistoryDetailActivity extends BaseActivity<ITokenSalePresenter> implements ITokenSaleView {

    private TextView mTvStatus;
    private TokenSaleHistory tokenSaleHistory;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;
    private String tempDistributionAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new ITokenSalePresenterImpl(this);


        mTvStatus = findViewById(R.id.tv_status);


        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.requestTokenSaleHistoryList(user.getId());
            }
        });


        if (getIntent().hasExtra("token_sale_history_detail")) {
            String TokenSaleHistoryString = getIntent().getStringExtra("token_sale_history_detail");
            TokenSaleHistoryResponse tokenSaleResponse = (TokenSaleHistoryResponse) JsonUtil.toObject(TokenSaleHistoryString,TokenSaleHistoryResponse.class);
            
            if(getIntent().hasExtra("position")){
                int position = getIntent().getIntExtra("position",0);
                tokenSaleHistory = tokenSaleResponse.getTokenSaleHistories().get(position);

                ViewUtil.setText(this, R.id.tv_title, "TOKEN "+tokenSaleHistory.getId());
                ViewUtil.setText(this, R.id.tv_date, tokenSaleHistory.getCreatedAtDate());

                if (tokenSaleHistory.getStatus() == 0) {
                    mTvStatus.setTextColor(getResources().getColor(R.color.material_black_text_color));
                    mTvStatus.setText(getText(R.string.premium_purchase_pending));
                } else if (tokenSaleHistory.getStatus() == 1) {
                    mTvStatus.setTextColor(getResources().getColor(R.color.vexpoint_plus));
                    mTvStatus.setText(getText(R.string.premium_purchase_success));
                } else {
                    mTvStatus.setTextColor(getResources().getColor(R.color.vexpoint_minus));
                    mTvStatus.setText(getText(R.string.premium_purchase_failed));
                }

                ViewUtil.setText(this, R.id.tv_deadline, tokenSaleHistory.getTimeStampDate(tokenSaleHistory.getPaymentDeadline()));

                float amount = tokenSaleHistory.getAmount();
                ViewUtil.setText(this, R.id.tv_payment_amount, amount+" "+tokenSaleHistory.getTokenSalePaymentOption().getPaymentCoin());
                ViewUtil.setText(this, R.id.tv_purchased_amount, amount/tokenSaleHistory.getTokenSalePaymentOption().getPricePerCoin()+ " "+tokenSaleHistory.getTokenSale().getTokenName());
                if(tokenSaleHistory.getDistributionAddress() != null && tokenSaleHistory.getDistributionAddress().length() > 0) {
                    ViewUtil.setText(this, R.id.tv_distribution_address, tokenSaleHistory.getDistributionAddress());
                }else{
                    if(tokenSaleHistory.getStatus() != 1) {
                        ViewUtil.setText(this, R.id.tv_distribution_address, "-");
                    }else{
                        if(tokenSaleHistory.getDistributionAddress() == null || tokenSaleHistory.getDistributionAddress().length() == 0) {
                            ViewUtil.setText(this, R.id.tv_distribution_address, "Click here to input address");
                            ViewUtil.setOnClickListener(this, new View.OnClickListener() {
                                @Override
                                public void onClick(View clickedView) {
                                    View view = View.inflate(TokenSaleHistoryDetailActivity.this, R.layout.include_distribution_address, null);
                                    final EditText etAddress = view.findViewById(R.id.et_address);

                                    new VexDialog.Builder(TokenSaleHistoryDetailActivity.this)
                                            .title("Input distibution address")
                                            .content("Input your " + tokenSaleHistory.getTokenSale().getTokenType() + " address")
                                            .addCustomView(view)
                                            .optionType(DialogOptionType.YES_NO)
                                            .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                                    if (etAddress.getText().toString().length() == 0) {
                                                        StaticGroup.showCommonErrorDialog(TokenSaleHistoryDetailActivity.this, "address field must not be empty");
                                                    } else if (etAddress.getText().toString().length() < 5) {
                                                        StaticGroup.showCommonErrorDialog(TokenSaleHistoryDetailActivity.this, "please input a valid address");
                                                    } else {
                                                        tempDistributionAddress = etAddress.getText().toString();
                                                        mPresenter.updateDistributionAddress(user.getId(),tokenSaleHistory.getId(),tempDistributionAddress);
                                                        dialog.dismiss();
                                                    }
                                                }
                                            })
                                            .autoDismiss(false)
                                            .cancelable(false)
                                            .canceledOnTouchOutside(false)
                                            .show();
                                }
                            },R.id.tv_distribution_address);
                        }else{
                            ViewUtil.setText(this, R.id.tv_distribution_address, tokenSaleHistory.getDistributionAddress());
                        }
                    }
                }
                ViewUtil.setText(this, R.id.tv_transfer_to, tokenSaleHistory.getPaymentAddress());
                ViewUtil.setText(this, R.id.tv_token_title, tokenSaleHistory.getTokenSale().getTitle());
                ViewUtil.setText(this, R.id.tv_token_type, tokenSaleHistory.getTokenSale().getTokenName() + " ("+tokenSaleHistory.getTokenSale().getTokenType()+")");
                ViewUtil.setText(this, R.id.tv_desc, tokenSaleHistory.getTokenSale().getDescription());
                ViewUtil.setText(this, R.id.tv_token_left, tokenSaleHistory.getTokenSale().getTokenLeft()+"");
                ViewUtil.setText(this, R.id.tv_token_total, tokenSaleHistory.getTokenSale().getTokenAvailable()+"");


            }else{
                finish();
            }
        }else{
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.requestTokenSaleHistoryList(user.getId());

    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof TokenSaleHistoryResponse) {
                TokenSaleHistoryResponse tokenSaleHistoryResponse = (TokenSaleHistoryResponse) data;

            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }else{
            //updated distribution address
            ViewUtil.setText(this, R.id.tv_distribution_address, tempDistributionAddress);
            Toast.makeText(this, "Your distribution address has been updated successfully", Toast.LENGTH_SHORT).show();
        }
    }


}
