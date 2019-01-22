package com.vexanium.vexgift.module.main.ui;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.base.BaseRecyclerAdapter;
import com.vexanium.vexgift.base.BaseRecyclerViewHolder;
import com.vexanium.vexgift.base.BaseSpacesItemDecoration;
import com.vexanium.vexgift.bean.model.AffiliateEntry;
import com.vexanium.vexgift.bean.model.AffiliateProgram;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.AffiliateProgramEntryResponse;
import com.vexanium.vexgift.bean.response.AffiliateProgramResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.module.main.presenter.IMainPresenter;
import com.vexanium.vexgift.module.main.presenter.IMainPresenterImpl;
import com.vexanium.vexgift.module.main.view.IMainView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.MeasureUtil;
import com.vexanium.vexgift.util.ViewUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ActivityFragmentInject(contentViewId = R.layout.activity_user_input_2, toolbarTitle = R.string.app_title, withLoadingAnim = true)
public class UserInputActivity extends BaseActivity<IMainPresenter> implements IMainView {

    User user;
    int id = -1;
    AffiliateProgram affiliateProgram;
    BaseRecyclerAdapter<AffiliateEntry> mAdapter;
    GridLayoutManager layoutListManager;
    ArrayList<String> options;
    ArrayList<AffiliateEntry> affiliateEntries;
    RecyclerView mRecyclerview;
    LinearLayout userInputBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        user = User.getCurrentUser(this);
        mPresenter = new IMainPresenterImpl(this);

        layoutListManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        userInputBox = findViewById(R.id.ll_user_input_box);

        mPresenter.getAffiliatePrograms(user.getId());
        if (getIntent().hasExtra("id")) {
            id = getIntent().getIntExtra("id", -1);
        }

        ViewUtil.setOnClickListener(this, this, R.id.btn_submit);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_submit:
                doSubmitUserInput();
                break;
            default:
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof AffiliateProgramResponse) {
                AffiliateProgramResponse affiliateProgramResponse = (AffiliateProgramResponse) data;
                if (id != -1) {
                    affiliateProgram = affiliateProgramResponse.getAffiliateProgramById(id);
                }
                if (affiliateProgram != null) {
                    updateView(affiliateProgram);
                    mPresenter.getAffiliateProgramEntries(user.getId(), affiliateProgram.getId());
                }
            } else if (data instanceof AffiliateProgramEntryResponse) {
                AffiliateProgramEntryResponse affiliateProgramEntryResponse = (AffiliateProgramEntryResponse) data;
                affiliateEntries = affiliateProgramEntryResponse.getAffiliateEntries();
                if (affiliateEntries != null) {
                    setAffiliateProgramEntry(affiliateEntries);
                }
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    private void updateView(AffiliateProgram affiliateProgram) {
        findViewById(R.id.send_button).setVisibility(View.GONE);
        ViewUtil.setText(this, R.id.tv_title, affiliateProgram.getTitle());
        ViewUtil.setText(this, R.id.tv_time, String.format("%s - %s", affiliateProgram.getStrValidFrom(), affiliateProgram.getStrValidUntil()));
        ViewUtil.setImageUrl(this, R.id.iv_image, affiliateProgram.getImage());
        ViewUtil.setText(this, R.id.tv_desc, affiliateProgram.getLongDesc());
        ViewUtil.setText(this, R.id.tv_terms, affiliateProgram.getPrivacyPolicy());

        String strOption = affiliateProgram.getInputOptions();
        options = (ArrayList<String>) JsonUtil.toObject(strOption, ArrayList.class);

        setOptionsField(options);
    }

    public void doSubmitUserInput() {

        final int childCount = userInputBox.getChildCount();
        Map<String, String> vals = new HashMap<>();

        for (int i = 0; i < childCount; i++) {
            if (userInputBox.getChildAt(i).getTag() != null) {
                String key = userInputBox.getChildAt(i).getTag().toString();
                String val = ((TextInputEditText) userInputBox.getChildAt(i)).getText().toString();

                vals.put(key, val);
            }
        }

        mPresenter.submitAffiliateProgramEntry(user.getId(), affiliateProgram.getId(), JsonUtil.toString(vals), JsonUtil.toString(options));
    }

    public void setOptionsField(ArrayList<String> opts) {
        userInputBox.removeAllViews();

        for (String opt : opts) {
            View view = View.inflate(this, R.layout.item_affiliate_input_field, null);
            view.setTag(opt);
            ((TextInputEditText) view.findViewById(R.id.et_option)).setHint(opt);
            userInputBox.addView(view);
        }
    }

    public void setAffiliateProgramEntry(ArrayList<AffiliateEntry> affiliateEntries) {

        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<AffiliateEntry>(this, affiliateEntries, layoutListManager) {

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_coupon_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final AffiliateEntry item) {

                    JsonObject objectVal = (JsonObject) JsonUtil.toObject(item.getJson(), JsonObject.class);
                    String text = "";
                    for (String set : options) {
                        text += String.format("%s : %s\n", set, objectVal.get(set));
                    }

                    holder.setText(R.id.tv_list, text);
                }
            };
            mAdapter.setHasStableIds(true);
            mRecyclerview.setLayoutManager(layoutListManager);
            mRecyclerview.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this, 16)));
            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
            if (mRecyclerview.getItemAnimator() != null)
                mRecyclerview.getItemAnimator().setAddDuration(250);
            mRecyclerview.getItemAnimator().setMoveDuration(250);
            mRecyclerview.getItemAnimator().setChangeDuration(250);
            mRecyclerview.getItemAnimator().setRemoveDuration(250);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRecyclerview.setItemViewCacheSize(30);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    App.setTextViewStyle(mRecyclerview);
                }
            });
        } else {
            mAdapter.setData(affiliateEntries);
        }
    }
}
