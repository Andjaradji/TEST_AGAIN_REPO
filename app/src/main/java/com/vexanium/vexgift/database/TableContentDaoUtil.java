package com.vexanium.vexgift.database;

import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.bean.model.Voucher;
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

    public VouchersResponse getContent(){
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 ?
                (VouchersResponse) JsonUtil.toObject(query.list().get(0).getResult(), new TypeToken<VouchersResponse>() {}.getType()) : null;
    }

    public ArrayList<Voucher> getVouchers(){
       VouchersResponse vouchersResponse = getContent();
       if(vouchersResponse!=null && vouchersResponse.getVouchers()!= null){
           return vouchersResponse.getVouchers();
       }else{
           return null;
       }
    }

    public void saveToDb(String result) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if(query.list().size() > 0)
        {
            tableContent = query.list().get(0);
            tableContent.setCreatedTime(System.currentTimeMillis());
            tableContent.setResult(result);
            mTableContentDao.update(tableContent);
        }
        else {
            tableContent = new TableContent();
            tableContent.setResult(result);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }
}

