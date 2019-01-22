package com.vexanium.vexgift.module.main.presenter;

import com.vexanium.vexgift.base.BasePresenter;
import com.vexanium.vexgift.bean.model.User;

public interface IMainPresenter extends BasePresenter {
    void requestUserInputData(int id, String input);
    void getUserInputData(int id);

    void submitAffiliateProgramEntry(int userId, int affiliateProgramId, String vals, String keys );
    void getAffiliateProgramEntries(int userId, int affiliateProgramId);
    void getAffiliateProgram(int userId, int affiliateProgramId);
    void getAffiliatePrograms(int userId);
}
