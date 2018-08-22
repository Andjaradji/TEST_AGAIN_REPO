package com.vexanium.vexgift.database;

import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.bean.response.MemberTypeResponse;
import com.vexanium.vexgift.bean.response.SettingResponse;
import com.vexanium.vexgift.util.JsonUtil;

import org.greenrobot.greendao.query.QueryBuilder;

public class TablePrefDaoUtil extends BaseDaoUtil {

    private static TablePrefDaoUtil instance;
    private static TablePrefDao mTablePrefDao;

    public TablePrefDaoUtil() {
    }

    public static TablePrefDaoUtil getInstance() {
        if (instance == null) {
            synchronized (TablePrefDaoUtil.class) {
                if (instance == null) {
                    instance = new TablePrefDaoUtil();
                }
            }

            mDaoSession = App.getApplication().getDaoSession();
            KLog.v("mDaoSession : " + mDaoSession);
            mTablePrefDao = mDaoSession.getTablePrefDao();
            KLog.v("mCardListResultDao : " + mTablePrefDao);
        }

        return instance;

    }

    @Override
    public void clearAllCache() {
        mTablePrefDao.deleteAll();
    }

    public String getInviteCode() {
        QueryBuilder<TablePref> query = mTablePrefDao.queryBuilder().orderAsc(TablePrefDao.Properties.Id);

        return query.list().size() > 0 && query.list().get(0).getInviteCode() != null ?
                query.list().get(0).getInviteCode() : "";
    }

    public SettingResponse getSettings() {
        QueryBuilder<TablePref> query = mTablePrefDao.queryBuilder().orderAsc(TablePrefDao.Properties.Id);

        return query.list().size() > 0 && query.list().get(0).getSetting() != null ?
                (SettingResponse) JsonUtil.toObject(query.list().get(0).getSetting(), new TypeToken<SettingResponse>() {
                }.getType()) : null;
    }

    public void saveInviteCodeToDb(String inviteCode) {
        TablePref tablePref;
        QueryBuilder<TablePref> query = mTablePrefDao.queryBuilder().orderAsc(TablePrefDao.Properties.Id);
        if (query.list().size() > 0) {
            tablePref = query.list().get(0);
            tablePref.setInviteCode(inviteCode);
            mTablePrefDao.update(tablePref);
        } else {
            tablePref = new TablePref();
            tablePref.setInviteCode(inviteCode);
            mTablePrefDao.insert(tablePref);
        }
        KLog.v("============== Pref Database saved ===============");
    }

    public void saveSettingToDb(String settings) {
        TablePref tablePref;
        QueryBuilder<TablePref> query = mTablePrefDao.queryBuilder().orderAsc(TablePrefDao.Properties.Id);
        if (query.list().size() > 0) {
            tablePref = query.list().get(0);
            tablePref.setSetting(settings);
            mTablePrefDao.update(tablePref);
        } else {
            tablePref = new TablePref();
            tablePref.setSetting(settings);
            mTablePrefDao.insert(tablePref);
        }
        KLog.v("============== Pref Database saved ===============");
    }

}

