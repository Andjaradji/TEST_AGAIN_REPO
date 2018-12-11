package com.vexanium.vexgift.module.buyback.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.BuybackHistory;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.BuybackHistoryResponse;
import com.vexanium.vexgift.module.buyback.presenter.IBuybackPresenter;
import com.vexanium.vexgift.module.buyback.presenter.IBuybackPresenterImpl;
import com.vexanium.vexgift.module.buyback.view.IBuybackView;
import com.vexanium.vexgift.util.ClickUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_token_sale_history_detail, toolbarTitle = R.string.buyback_history_title, withLoadingAnim = true)
public class BuybackHistoryDetailActivity extends BaseActivity<IBuybackPresenter> implements IBuybackView {

    private TextView mTvStatus;
    private BuybackHistory buybackHistory;
    private SwipeRefreshLayout mRefreshLayout;
    private User user;
    private String tempDistributionAddress = "";
    int paymentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IBuybackPresenterImpl(this);

        mTvStatus = findViewById(R.id.tv_status);

        mRefreshLayout = findViewById(R.id.srl_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (buybackHistory != null) {
                    mPresenter.requestBuybackHistoryList(user.getId());
                } else {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });


        if (getIntent().hasExtra("buyback_history_detail")) {
            String BuybackHistoryString = getIntent().getStringExtra("buyback_history_detail");
            BuybackHistoryResponse buybackResponse = (BuybackHistoryResponse) JsonUtil.toObject(BuybackHistoryString, BuybackHistoryResponse.class);

            if (getIntent().hasExtra("position")) {
                int position = getIntent().getIntExtra("position", 0);
                buybackHistory = buybackResponse.getBuybackHistories().get(position);

                updateView();
            } else {

            }
        } else if (getIntent().hasExtra("token_payment_id")) {
            paymentId = getIntent().getIntExtra("token_payment_id", -1);
            KLog.v("BuybackHistoryDetailActivity","initView: HPtes token_payment_id : "+paymentId);
            if (paymentId != -1) {
                mPresenter.requestBuybackHistoryList(user.getId());
            } else {
                Toast.makeText(this, "Payment not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "nothing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (buybackHistory != null) {
            mPresenter.getBuybackPayment(user.getId(), buybackHistory.getId());
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof BuybackHistoryResponse) {
                BuybackHistoryResponse buybackHistoryResponse = (BuybackHistoryResponse) data;
                buybackHistory = buybackHistoryResponse.getBuybackHistoryById(paymentId);

                updateView();

            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        } else {
            //updated distribution address
            ViewUtil.setText(this, R.id.tv_distribution_address, tempDistributionAddress);
            ViewUtil.findViewById(BuybackHistoryDetailActivity.this, R.id.ll_distribution_address).setVisibility(View.VISIBLE);
            ViewUtil.findViewById(BuybackHistoryDetailActivity.this, R.id.btn_input_distribution_address).setVisibility(View.GONE);
            Toast.makeText(this, "Your distribution address has been updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    void updateView() {
        ViewUtil.setText(this, R.id.tv_title, "BUYBACK " + buybackHistory.getId());
        ViewUtil.setText(this, R.id.tv_date, buybackHistory.getCreatedAtDate());

        if (buybackHistory.getStatus() == 0) {
            mTvStatus.setTextColor(getResources().getColor(R.color.material_black_text_color));
            mTvStatus.setText(getText(R.string.premium_purchase_pending));
        } else if (buybackHistory.getStatus() == 1) {
            mTvStatus.setTextColor(getResources().getColor(R.color.vexpoint_plus));
            mTvStatus.setText(getText(R.string.premium_purchase_success));
        } else {
            mTvStatus.setTextColor(getResources().getColor(R.color.vexpoint_minus));
            mTvStatus.setText(getText(R.string.premium_purchase_failed));
        }

        ViewUtil.setText(this, R.id.tv_deadline, buybackHistory.getTimeStampDate(buybackHistory.getPaymentDeadline()));

        float amount = buybackHistory.getAmount();
        String paymentAmount = String.format("%.010f", amount);
        String purchasedAmount = String.format("%.010f", amount * buybackHistory.getBuybackOption().getPrice());

        ViewUtil.setText(this, R.id.tv_payment_amount, paymentAmount + " VEX" );
        ViewUtil.setText(this, R.id.tv_purchased_amount, purchasedAmount + " " + buybackHistory.getBuybackOption().getCoinName());
        if (buybackHistory.getDistributionAddress() != null && buybackHistory.getDistributionAddress().length() > 0) {
            ViewUtil.setText(this, R.id.tv_distribution_address, buybackHistory.getDistributionAddress());
        } else {
            ViewUtil.findViewById(BuybackHistoryDetailActivity.this, R.id.ll_distribution_address).setVisibility(View.VISIBLE);
            if (buybackHistory.getStatus() != 1) {
                ViewUtil.setText(this, R.id.tv_distribution_address, "-");
            } else {
                if (buybackHistory.getDistributionAddress() == null || buybackHistory.getDistributionAddress().length() == 0) {
                    ViewUtil.findViewById(BuybackHistoryDetailActivity.this, R.id.ll_distribution_address).setVisibility(View.GONE);
                    ViewUtil.findViewById(BuybackHistoryDetailActivity.this, R.id.btn_input_distribution_address).setVisibility(View.VISIBLE);
                    ViewUtil.setOnClickListener(this, new View.OnClickListener() {
                        @Override
                        public void onClick(View clickedView) {
                            View view = View.inflate(BuybackHistoryDetailActivity.this, R.layout.include_distribution_address, null);
                            final EditText etAddress = view.findViewById(R.id.et_address);

                            new VexDialog.Builder(BuybackHistoryDetailActivity.this)
                                    .title("Input distribution address")
                                    .content(buybackHistory.getBuybackOption().getDistributionAddressType())
                                    .addCustomView(view)
                                    .optionType(DialogOptionType.YES_NO)
                                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                            if (etAddress.getText().toString().length() == 0) {
                                                StaticGroup.showCommonErrorDialog(BuybackHistoryDetailActivity.this, "address field must not be empty");
                                            } else if (etAddress.getText().toString().length() < 5) {
                                                StaticGroup.showCommonErrorDialog(BuybackHistoryDetailActivity.this, "please input a valid address");
                                            } else {
                                                tempDistributionAddress = etAddress.getText().toString();
                                                mPresenter.updateDistributionAddress(user.getId(), buybackHistory.getId(), tempDistributionAddress);
                                                dialog.dismiss();
                                            }
                                        }
                                    })
                                    .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                                            if (ClickUtil.isFastDoubleClick()) return;
                                            dialog.dismiss();
                                        }
                                    })
                                    .autoDismiss(false)
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .show();
                        }
                    }, R.id.btn_input_distribution_address);
                } else {
                    ViewUtil.setText(this, R.id.tv_distribution_address, buybackHistory.getDistributionAddress());
                }
            }
        }
        ViewUtil.setText(this, R.id.tv_transfer_to, buybackHistory.getPaymentAddress());
        ViewUtil.findViewById(this, R.id.ll_transfer_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticGroup.copyToClipboard(BuybackHistoryDetailActivity.this, buybackHistory.getPaymentAddress());
            }
        });
        ViewUtil.setText(this, R.id.tv_transfer_deadline, buybackHistory.getTimeStampDate(buybackHistory.getPaymentDeadline()));
        ViewUtil.setText(this, R.id.tv_token_title, buybackHistory.getBuyback().getName());
        ViewUtil.setText(this, R.id.tv_token_type, buybackHistory.getBuyback().getCoinName() + " (" + buybackHistory.getBuyback().getCoinType() + ")");
        ViewUtil.setText(this, R.id.tv_desc, buybackHistory.getBuyback().getDescription());
        String left = String.format("%.010f", buybackHistory.getBuyback().getCoinBought());
        String available = String.format("%.010f", buybackHistory.getBuyback().getTotalCoin());
        ViewUtil.setText(this, R.id.tv_token_left, left);
        ViewUtil.setText(this, R.id.tv_token_total, available);
    }


}
