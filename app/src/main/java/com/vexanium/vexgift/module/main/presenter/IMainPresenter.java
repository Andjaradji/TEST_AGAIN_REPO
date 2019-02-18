package com.vexanium.vexgift.module.main.presenter;

import com.vexanium.vexgift.base.BasePresenter;

public interface IMainPresenter extends BasePresenter {

    void submitAffiliateProgramEntry(int userId, int affiliateProgramId, String vals);

    void getAffiliateProgramEntries(int userId, int affiliateProgramId);

    void getAffiliateProgram(int userId, int affiliateProgramId);

    void getAffiliatePrograms(int userId);
}
