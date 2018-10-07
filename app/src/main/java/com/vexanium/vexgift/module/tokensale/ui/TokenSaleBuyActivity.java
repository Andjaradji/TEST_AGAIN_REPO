package com.vexanium.vexgift.module.tokensale.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.TokenSale;
import com.vexanium.vexgift.bean.model.TokenSaleOption;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.TokenSalePaymentResponse;
import com.vexanium.vexgift.bean.response.TokenSaleResponse;
import com.vexanium.vexgift.module.tokensale.presenter.ITokenSalePresenter;
import com.vexanium.vexgift.module.tokensale.presenter.ITokenSalePresenterImpl;
import com.vexanium.vexgift.module.tokensale.view.ITokenSaleView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;

@ActivityFragmentInject(contentViewId = R.layout.activity_buy_token_sale, withLoadingAnim = true)
public class TokenSaleBuyActivity extends BaseActivity<ITokenSalePresenter> implements ITokenSaleView {

    User user;
    TokenSale tokenSale;
    TokenSaleOption tokenSaleOption;
    TokenSaleResponse tokenSaleResponse;

    EditText etAddress, etAmount;
    TextView tvPurchasedTotal;

    float amountTotal = 0, amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new ITokenSalePresenterImpl(this);
        user = User.getCurrentUser(this);

        mPresenter.requestTokenSaleList(user.getId());

        etAmount = findViewById(R.id.et_amount);
        etAddress = findViewById(R.id.et_address);
        tvPurchasedTotal = findViewById(R.id.tv_purchased_total_body);

        if(getIntent().hasExtra("token_sale")){
            String tokenSaleString = getIntent().getStringExtra("token_sale");
            tokenSaleResponse = (TokenSaleResponse) JsonUtil.toObject(tokenSaleString,TokenSaleResponse.class);

            int tokenPosition = -1;
            if(getIntent().hasExtra("token_position")){
                tokenPosition = getIntent().getIntExtra("token_position",-1);
            }

            if(tokenPosition != -1) {
                tokenSale = tokenSaleResponse.getTokenSales().get(tokenPosition);
            }else{
                finish();
            }

            int optionPosition = -1;
            if(getIntent().hasExtra("option_position")){
                optionPosition = getIntent().getIntExtra("option_position",-1);
            }

            if(optionPosition != -1) {
                tokenSaleOption = tokenSaleResponse.getTokenSales().get(tokenPosition).getTokenSalePaymentOptions().get(optionPosition);
            }else{
                finish();
            }

            String amountHeader = String.format("Min = %s, Max = %s",tokenSaleOption.getMinPurchase()+ " "+tokenSaleOption.getPaymentCoin(), tokenSaleOption.getMaxPurchase()+ " "+tokenSaleOption.getPaymentCoin());
            etAmount.setHint(amountHeader);
            ViewUtil.setText(this, R.id.tv_token_title, tokenSale.getTitle());
            String time = String.format("%s - %s", tokenSale.getTimeStampDate(tokenSale.getStartTime()), tokenSale.getTimeStampDate(tokenSale.getEndTime()));
            ViewUtil.setText(this, R.id.tv_sale_time, time);

            ViewUtil.setText(this, R.id.tv_payment_title, "Payment By " + tokenSaleOption.getPaymentCoin());
            String paymentBody = String.format("1 %s = %s", tokenSale.getTokenType(), tokenSaleOption.getPricePerCoin() + " " + tokenSaleOption.getPaymentCoin());
            ViewUtil.setText(this, R.id.tv_payment_body, paymentBody);

            ViewUtil.setText(this, R.id.tv_purchased_total_body, "-");

            etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(etAmount.getText().toString().length() > 0) {
                        try{
                            amount = Float.valueOf(etAmount.getText().toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            amountTotal = amount / tokenSaleOption.getPricePerCoin();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        String purchasedTotal = amountTotal + " " + tokenSale.getTokenType();
                        tvPurchasedTotal.setText(purchasedTotal);
                    }else{
                        tvPurchasedTotal.setText("-");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }

        ViewUtil.setText(this, R.id.tv_toolbar_title, getString(R.string.token_sale_title));
        ViewUtil.setOnClickListener(this, this, R.id.back_button, R.id.btn_buy);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;

            case R.id.btn_buy:
                if(etAddress.getText().toString().length() == 0 || etAmount.getText().toString().length() == 0) {
                    StaticGroup.showCommonErrorDialog(this, "address and amount field must not be empty");
                }else if(etAddress.getText().toString().length() < 5){
                    StaticGroup.showCommonErrorDialog(this, "please input a valid address");
                }else if(amount < tokenSaleOption.getMinPurchase() || amount > tokenSaleOption.getMaxPurchase()){
                    StaticGroup.showCommonErrorDialog(this, "amount must be or higher than "+ tokenSaleOption.getMinPurchase() + " and must be or lower than "+tokenSaleOption.getMaxPurchase());
                }else {
                    mPresenter.buyTokenSale(user.getId(), tokenSale.getId(), tokenSaleOption.getId(), amount, etAddress.getText().toString());
                }
            default:
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
//        mRefreshLayout.setRefreshing(false);
        if (data != null) {
            if (data instanceof TokenSalePaymentResponse) {
                TokenSalePaymentResponse tokenSalePaymentResponse = (TokenSalePaymentResponse) data;

                finish();

            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

//    public void setTokenSaleList() {
//        if (mAdapter == null) {
//            mAdapter = new BaseRecyclerAdapter<TokenSale>(this, tokenSales, layoutListManager) {
//
//                @Override
//                public int getItemLayoutId(int viewType) {
//                    return R.layout.item_token_sale_program;
//                }
//
//                @Override
//                public void bindData(BaseRecyclerViewHolder holder, int position, TokenSale item) {
//                    holder.setText(R.id.tv_title, item.getTitle());
//                    holder.setText(R.id.tv_desc, item.getDescription());
//                    String time = String.format("%s - %s", item.getTimeStampDate(item.getStartTime()), item.getTimeStampDate(item.getEndTime()));
//                    holder.setText(R.id.tv_sale_time,time);
//                    holder.setText(R.id.tv_token_type,item.getTokenName()+ " ("+ item.getTokenType()+")");
//                    holder.setText(R.id.tv_token_left,item.getTokenLeft()+"");
//                    holder.setText(R.id.tv_token_total,item.getTokenAvailable()+"");
//
//                    setTokenSalePaymentOptionList(holder,position,item);
//
//                    App.setTextViewStyle((ViewGroup) holder.getView(R.id.root_view));
//
//                }
//
//            };
//            mAdapter.setHasStableIds(true);
//            mRecyclerview.setLayoutManager(layoutListManager);
//            mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this, 16)));
//            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
//            if (mRecyclerview.getItemAnimator() != null)
//                mRecyclerview.getItemAnimator().setAddDuration(250);
//            mRecyclerview.getItemAnimator().setMoveDuration(250);
//            mRecyclerview.getItemAnimator().setChangeDuration(250);
//            mRecyclerview.getItemAnimator().setRemoveDuration(250);
//            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
//            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
//            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
//            mRecyclerview.setItemViewCacheSize(30);
//            mRecyclerview.setAdapter(mAdapter);
//
//        } else {
//            mAdapter.setData(tokenSales);
//        }
//
//        if (tokenSales.size() <= 0) {
//            mErrorView.setVisibility(View.VISIBLE);
//            mIvError.setImageResource(R.drawable.voucher_empty);
//            mTvErrorHead.setText(getString(R.string.error_token_sale_empty_header));
//            mTvErrorBody.setText(getString(R.string.error_my_token_sale_empty_body));
//
//            mRecyclerview.setVisibility(View.GONE);
//        } else {
//            mErrorView.setVisibility(View.GONE);
//            mRecyclerview.setVisibility(View.VISIBLE);
//
//        }
//    }
//
//    public void setTokenSalePaymentOptionList(BaseRecyclerViewHolder holder, int position, final TokenSale token){
//        final LinearLayout btnPaymentOptions =  (LinearLayout)holder.getView(R.id.btn_payment_options);
//        final RecyclerView recyclerView = holder.getRecyclerView(R.id.rv_payment_options);
//        if(recyclerView!=null) {
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
//            recyclerView.setLayoutManager(gridLayoutManager);
//
//            holder.getRecyclerView(R.id.rv_payment_options).setAdapter(new BaseRecyclerAdapter<TokenSaleOption>(this, token.getTokenSalePaymentOptions(), gridLayoutManager) {
//                @Override
//                public int getItemLayoutId(int viewType) {
//                    return R.layout.item_token_sale_payment_options;
//                }
//
//                @Override
//                public void bindData(BaseRecyclerViewHolder holder, int position, TokenSaleOption item) {
//                    if(item.getPaymentCoin()!=null) {
//                        String title = String.format("Payment by %s", item.getPaymentCoin());
//                        holder.setText(R.id.tv_payment_title, title);
//                    }else{
//                        holder.setText(R.id.tv_payment_title, "error");
//                        holder.getButton(R.id.btn_buy).setEnabled(false);
//                    }
//
//                    if(item.getPricePerCoin() > 0){
//                        String body = String.format("1 %s = %s", token.getTokenType(), item.getPricePerCoin() + " " + item.getPaymentCoin());
//                        holder.setText(R.id.tv_payment_body, body);
//                    }else{
//                        holder.setText(R.id.tv_payment_body, "error");
//                        holder.getButton(R.id.btn_buy).setEnabled(false);
//                    }
//                }
//
//
//            });
//
//            btnPaymentOptions.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(recyclerView.getVisibility() == View.GONE){
//                        btnPaymentOptions.setVisibility(View.GONE);
//                        recyclerView.setVisibility(View.VISIBLE);
//                        findViewById(R.id.tv_payment_options).setVisibility(View.VISIBLE);
//                    }
//                }
//            });
//        }else{
//            btnPaymentOptions.setVisibility(View.GONE);
//        }
//    }
}
