package com.vexanium.vexgift.base;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;
import com.vexanium.vexgift.annotation.ActivityFragmentInject;


/**
 * Created by mac on 11/16/17.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView, View.OnClickListener {
    protected T mPresenter;

    protected View mFragmentRootView;
    protected int mContentViewId;

    private boolean mIsStop;

    public BaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == mFragmentRootView) {
            if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
                ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
                mContentViewId = annotation.contentViewId();
            } else {
                throw new RuntimeException("Class must add annotations of ActivityFragmentInject.class" + getClass().getSimpleName());
            }
            KLog.v("mContentViewId : " + mContentViewId);
            mFragmentRootView = inflater.inflate(mContentViewId, container, false);
            initView(mFragmentRootView);
        }

        return mFragmentRootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsStop = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsStop = false;
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) mFragmentRootView.getParent();
        if (null != parent) {
            parent.removeView(mFragmentRootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    protected abstract void initView(View fragmentRootView);

    public void onChange() {
    }

    protected void showSnackbar(String msg) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            Snackbar.make(mFragmentRootView, msg, Snackbar.LENGTH_SHORT).show();
        }
    }

    protected void showSnackbar(@StringRes int id) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            Snackbar.make(mFragmentRootView, id, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void toast(String msg) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            showSnackbar(msg);
        }
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onClick(View v) {

    }
}
