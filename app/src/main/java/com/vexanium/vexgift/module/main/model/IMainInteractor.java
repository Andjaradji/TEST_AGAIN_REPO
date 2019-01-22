package com.vexanium.vexgift.module.main.model;

import com.vexanium.vexgift.callback.RequestCallback;

import rx.Subscription;

public interface IMainInteractor<T> {
    Subscription requestInput(RequestCallback<T> callback, int id, String input);

    Subscription getAffilateProgram(RequestCallback<T> callback, int userId, int affiliateProgramId);

    Subscription getAffilatePrograms(RequestCallback<T> callback, int userId);

    Subscription getAffilateProgramEntries(RequestCallback<T> callback, int id, int affiliateProgramId);

    Subscription submitAffiliateProgramEntry(RequestCallback<T> callback, int user_id, int affiliate_program_id, String vals, String keys);

    Subscription getInput(RequestCallback<T> callback, int id);
}
