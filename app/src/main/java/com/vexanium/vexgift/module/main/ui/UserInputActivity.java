package com.vexanium.vexgift.module.main.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

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
        mRecyclerview = findViewById(R.id.recylerview);

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        ((AppBarLayout) toolbarLayout.getParent()).addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, UserInputActivity.State state) {
                if (state == UserInputActivity.State.COLLAPSED) {
                    findViewById(R.id.voucher_title).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(UserInputActivity.this, R.color.material_black));
                } else {
                    findViewById(R.id.voucher_title).setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.back_button)).setColorFilter(ContextCompat.getColor(UserInputActivity.this, R.color.material_white));
                }
            }
        });

        if (getIntent().hasExtra("id")) {
            id = getIntent().getIntExtra("id", -1);
        }

        mPresenter.getAffiliatePrograms(user.getId());

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

        ((TextView) findViewById(R.id.tv_toolbar_title)).setText(affiliateProgram.getTitle());

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
                String val = ((TextInputLayout) userInputBox.getChildAt(i)).getEditText().getText().toString();

                vals.put(key, val);
                ((TextInputLayout) userInputBox.getChildAt(i)).getEditText().setText("");
            }
        }

        mPresenter.submitAffiliateProgramEntry(user.getId(), affiliateProgram.getId(), JsonUtil.toString(vals));
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
                    return R.layout.affiliate_program_entry_list;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final AffiliateEntry item) {

                    JsonObject objectVal = (JsonObject) JsonUtil.toObject(item.getJson(), JsonObject.class);
                    String text = "";
                    for (String set : options) {
                        text += String.format("%s : %s", set, objectVal.get(set));
                        if (!options.get(options.size() - 1).equalsIgnoreCase(set)) text += "\n";
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

    public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
    }
}
