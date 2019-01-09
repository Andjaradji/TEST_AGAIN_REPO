package com.vexanium.vexgift.module.news.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface INewsPresenter extends BasePresenter {
    void requestNews(User user);
}
