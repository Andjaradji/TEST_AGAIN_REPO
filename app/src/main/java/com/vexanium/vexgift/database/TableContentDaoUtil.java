package com.vexanium.vexgift.database;

import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.bean.model.Notification;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.UserVouchersResponse;
import com.vexanium.vexgift.bean.response.VouchersResponse;
import com.vexanium.vexgift.util.JsonUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;

public class TableContentDaoUtil extends BaseDaoUtil {

    private static TableContentDaoUtil instance;
    private static TableContentDao mTableContentDao;

    public TableContentDaoUtil() {
    }

    public static TableContentDaoUtil getInstance() {
        if (instance == null) {
            synchronized (TableContentDaoUtil.class) {
                if (instance == null) {
                    instance = new TableContentDaoUtil();
                }
            }

            mDaoSession = App.getApplication().getDaoSession();
            KLog.v("mDaoSession : " + mDaoSession);
            mTableContentDao = mDaoSession.getTableContentDao();
            KLog.v("mCardListResultDao : " + mTableContentDao);
        }

        return instance;

    }

    @Override
    public void clearAllCache() {
        mTableContentDao.deleteAll();
    }

    public VouchersResponse getVouchersContent(){
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getVouchers() != null?
                (VouchersResponse) JsonUtil.toObject(query.list().get(0).getVouchers(), new TypeToken<VouchersResponse>() {}.getType()) : null;
    }

    public UserVouchersResponse getMyBoxContent(){
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getMyBoxs() != null?
                (UserVouchersResponse) JsonUtil.toObject(query.list().get(0).getMyBoxs(), new TypeToken<UserVouchersResponse>() {}.getType()) : new UserVouchersResponse();
    }

    public ArrayList<Notification> getNotifs(){
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getNotifs() != null?
                (ArrayList<Notification>) JsonUtil.toObject(query.list().get(0).getNotifs(), new TypeToken<ArrayList<Notification>>() {}.getType()) : null;
    }

    public ArrayList<Voucher> getVouchers(){
       VouchersResponse vouchersResponse = getVouchersContent();
       if(vouchersResponse!=null && vouchersResponse.getVouchers()!= null){
           return vouchersResponse.getVouchers();
       }else{
           return null;
       }
    }

    public ArrayList<Voucher> getTokens(){
        VouchersResponse vouchersResponse = getVouchersContent();
        if(vouchersResponse!=null && vouchersResponse.getTokens()!= null){
            return vouchersResponse.getTokens();
        }else{
            return null;
        }
    }


    public void saveVouchersToDb(String vouchers) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if(query.list().size() > 0)
        {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setVouchers(vouchers);
            mTableContentDao.update(tableContent);
        }
        else {
            tableContent = new TableContent();
            tableContent.setVouchers(vouchers);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void saveTokensToDb(String vouchers) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if(query.list().size() > 0)
        {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setVouchers(vouchers);
            mTableContentDao.update(tableContent);
        }
        else {
            tableContent = new TableContent();
            tableContent.setVouchers(vouchers);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void saveNotifsToDb(String notifs) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if(query.list().size() > 0)
        {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setNotifs(notifs);
            mTableContentDao.update(tableContent);
        }
        else {
            tableContent = new TableContent();
            tableContent.setNotifs(notifs);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void saveBoxsToDb(String boxs) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if(query.list().size() > 0)
        {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setMyBoxs(boxs);
            mTableContentDao.update(tableContent);
        }
        else {
            tableContent = new TableContent();
            tableContent.setMyBoxs(boxs);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }
}

