package com.vexanium.vexgift.module.news.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.base.BaseActivity;
import com.vexanium.vexgift.bean.model.News;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.NewsResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.module.news.presenter.INewsPresenter;
import com.vexanium.vexgift.module.news.presenter.INewsPresenterImpl;
import com.vexanium.vexgift.module.news.view.INewsView;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.widget.CustomViewPager;

import java.io.Serializable;
import java.util.ArrayList;

@ActivityFragmentInject(contentViewId = R.layout.activity_news, withLoadingAnim = true)
public class NewsActivity extends BaseActivity<INewsPresenter> implements INewsView {

    ArrayList<News> news;
    ArrayList<NewsFragment> fragments;
    CustomViewPager viewPager;
    NewsPagerAdapter newsPagerAdapter;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mPresenter = new INewsPresenterImpl(this);
        user = User.getCurrentUser(this);

        setViewPager();
        setToolbar();

        NewsResponse newsResponse = TableContentDaoUtil.getInstance().getNews();
        if (newsResponse != null && newsResponse.getNews() != null) {
            news = newsResponse.getNews();
            setViewPagerData(news);
        }
        mPresenter.requestNews(user);

        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
        }
    }

    @Override
    public void handleResult(Serializable data, HttpResponse errorResponse) {
        if (data != null) {
            if (data instanceof NewsResponse) {
                NewsResponse newsResponse = (NewsResponse) data;
                TableContentDaoUtil.getInstance().saveNewsToDb(JsonUtil.toString(newsResponse));

                news = newsResponse.getNews();
                setViewPagerData(news);
            }
        } else if (errorResponse != null) {
            StaticGroup.showCommonErrorDialog(this, errorResponse);
        }
    }

    private void setViewPagerData(ArrayList<News> news) {
        fragments = new ArrayList<>();
        for (News n : news) {
            fragments.add(NewsFragment.newInstance(n.getSiteLink(), n.getJavascript()));
        }
        if (newsPagerAdapter == null) {
            setViewPager();
        }
        newsPagerAdapter.setDataSource(fragments);
    }

    private void setViewPager() {
        newsPagerAdapter = new NewsPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(true);
        viewPager.setAdapter(newsPagerAdapter);
    }

    private void setToolbar() {
        TabLayout tabLayout = findViewById(R.id.tab_layouts);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class NewsPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<NewsFragment> mFragments;

        public NewsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<>();
        }

        public void setDataSource(ArrayList<NewsFragment> mFragments) {
            this.mFragments = mFragments;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = news.get(position).getName();

//
//            SpannableStringBuilder sb = new SpannableStringBuilder("   " + title); // space added before text for convenience
//            try {
//                myDrawable.setBounds(5, 5, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
//                ImageSpan span = new ImageSpan(myDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
//                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            } catch (Exception e) {
//                // TODO: handle exception
//            }
            return title;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
