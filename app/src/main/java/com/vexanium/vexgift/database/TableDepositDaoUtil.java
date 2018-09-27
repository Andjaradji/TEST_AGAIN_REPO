package com.vexanium.vexgift.database;

import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.bean.response.DepositListResponse;
import com.vexanium.vexgift.bean.response.UserDepositResponse;
import com.vexanium.vexgift.util.JsonUtil;

import org.greenrobot.greendao.query.QueryBuilder;

public class TableDepositDaoUtil extends BaseDaoUtil {
    private static TableDepositDaoUtil instance;
    private static TableDepositDao mTableDepositDao;

    public TableDepositDaoUtil() {
    }

    public static TableDepositDaoUtil getInstance() {
        if (instance == null) {
            synchronized (TableDepositDaoUtil.class) {
                if (instance == null) {
                    instance = new TableDepositDaoUtil();
                }
            }

            mDaoSession = App.getApplication().getDaoSession();
            KLog.v("mDaoSession : " + mDaoSession);
            mTableDepositDao = mDaoSession.getTableDepositDao();
            KLog.v("mCardListResultDao : " + mTableDepositDao);
        }

        return instance;

    }

    @Override
    public void clearAllCache() {
        mTableDepositDao.deleteAll();
    }

    public DepositListResponse getDepositListResponse() {
        QueryBuilder<TableDeposit> query = mTableDepositDao.queryBuilder().orderAsc(TableDepositDao.Properties.Id);

        return query.list().size() > 0 && query.list().get(0).getDeposits() != null ?
                (DepositListResponse) JsonUtil.toObject(query.list().get(0).getDeposits(), new TypeToken<DepositListResponse>() {
                }.getType()) : null;
    }

    public UserDepositResponse getUserDepositListResponse() {
        QueryBuilder<TableDeposit> query = mTableDepositDao.queryBuilder().orderAsc(TableDepositDao.Properties.Id);

        return query.list().size() > 0 && query.list().get(0).getUserDeposits() != null ?
                (UserDepositResponse) JsonUtil.toObject(query.list().get(0).getUserDeposits(), new TypeToken<UserDepositResponse>() {
                }.getType()) : null;
    }

    public void saveDepositsToDb(String deposits) {
        TableDeposit tableDao;
        QueryBuilder<TableDeposit> query = mTableDepositDao.queryBuilder().orderAsc(TableDepositDao.Properties.Id);
        if (query.list().size() > 0) {
            tableDao = query.list().get(0);
            tableDao.setDeposits(deposits);
            mTableDepositDao.update(tableDao);
        } else {
            tableDao = new TableDeposit();
            tableDao.setDeposits(deposits);
            mTableDepositDao.insert(tableDao);
        }
        KLog.v("============== Deposit Database saved ===============");
    }

    public void saveUserDepositsToDb(String userDeposits) {
        TableDeposit tableDao;
        QueryBuilder<TableDeposit> query = mTableDepositDao.queryBuilder().orderAsc(TableDepositDao.Properties.Id);
        if (query.list().size() > 0) {
            tableDao = query.list().get(0);
            tableDao.setUserDeposits(userDeposits);
            mTableDepositDao.update(tableDao);
        } else {
            tableDao = new TableDeposit();
            tableDao.setUserDeposits(userDeposits);
            mTableDepositDao.insert(tableDao);
        }
        KLog.v("============== Deposit Database saved ===============");
    }
}
