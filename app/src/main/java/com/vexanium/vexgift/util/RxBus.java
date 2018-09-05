package com.vexanium.vexgift.util;

import android.support.annotation.NonNull;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by mac on 11/16/17.
 */

public class RxBus {
    public static final String KEY_APP_FINISH = "key_app_finish";
    public static final String KEY_VP_RECORD_ADDED = "key_vp_record_added";
    public static final String KEY_VEXPOINT_UPDATE = "key_vp_update";
    public static final String KEY_NOTIF_ADDED = "key_notif_added";
    public static final String KEY_BOX_HISTORY_ADDED = "key_box_history_added";
    public static final String KEY_BOX_CHANGED = "key_box_changed";
    public static final String KEY_TOKEN_VOUCHER_GUIDANCE = "key_token_voucher_guidance";
    public static final String KEY_MY_BOX_GUIDANCE = "key_mybox_guidance";
    public static final String KEY_CLEAR_GUIDANCE = "key_clear_guidance";
    public static final String KEY_ENABLE_REFRESH_LAYOUT_OR_SCROLL_RECYCLER_VIEW = "key_enable_refresh_layout_or_scroll_recycler_view";

    public static final String KEY_NETWORK_STATUS_CHANGE = "key_network_status_change";

    private volatile static RxBus sInstance;
    private ConcurrentMap<Object, List<Subject>> mSubjectMapper = new ConcurrentHashMap<>();

    private RxBus() {
    }

    public static RxBus get() {
        if (sInstance == null) {
            synchronized (RxBus.class) {
                if (sInstance == null) {
                    sInstance = new RxBus();
                }
            }
        }
        return sInstance;
    }

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> clazz) {
        List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            mSubjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        KLog.e("{register}subjectMapper: " + mSubjectMapper);
        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        final List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null != subjectList) {
            subjectList.remove(observable);
            if (subjectList.isEmpty()) {
                mSubjectMapper.remove(tag);
            }
        }
        KLog.e("{unregister}subjectMapper: " + mSubjectMapper);
    }

    @SuppressWarnings("unchecked")
    public void post(@NonNull Object tag, @NonNull Object content) {
        final List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null != subjectList && !subjectList.isEmpty()) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }
}
