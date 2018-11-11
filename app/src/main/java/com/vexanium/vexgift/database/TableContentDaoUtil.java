package com.vexanium.vexgift.database;

import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;
import com.vexanium.vexgift.app.App;
import com.vexanium.vexgift.bean.model.Notification;
import com.vexanium.vexgift.bean.model.NotificationModel;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.BestVoucherResponse;
import com.vexanium.vexgift.bean.response.CategoryResponse;
import com.vexanium.vexgift.bean.response.FeaturedVoucherResponse;
import com.vexanium.vexgift.bean.response.MemberTypeResponse;
import com.vexanium.vexgift.bean.response.PaymentTypeResponse;
import com.vexanium.vexgift.bean.response.UserReferralResponse;
import com.vexanium.vexgift.bean.response.UserVouchersResponse;
import com.vexanium.vexgift.bean.response.VoucherTypeResponse;
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

    public VouchersResponse getVouchersContent() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getVouchers() != null ?
                (VouchersResponse) JsonUtil.toObject(query.list().get(0).getVouchers(), new TypeToken<VouchersResponse>() {
                }.getType()) : null;
    }

    public UserVouchersResponse getMyBoxContent() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getMyBoxs() != null ?
                (UserVouchersResponse) JsonUtil.toObject(query.list().get(0).getMyBoxs(), new TypeToken<UserVouchersResponse>() {
                }.getType()) : new UserVouchersResponse();
    }

    public ArrayList<Notification> getNotifs() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getNotifs() != null ?
                (ArrayList<Notification>) JsonUtil.toObject(query.list().get(0).getNotifs(), new TypeToken<ArrayList<Notification>>() {
                }.getType()) : null;
    }

    public UserReferralResponse getReferrals() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getReferrals() != null ?
                (UserReferralResponse) JsonUtil.toObject(query.list().get(0).getReferrals(), new TypeToken<UserReferralResponse>() {
                }.getType()) : null;
    }

    public ArrayList<NotificationModel> getNotifications() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getNotifs() != null ?
                (ArrayList<NotificationModel>) JsonUtil.toObject(query.list().get(0).getNotifs(), new TypeToken<ArrayList<NotificationModel>>() {
                }.getType()) : null;
    }

    public PaymentTypeResponse getPaymentTypes() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getPaymentTypes() != null ?
                (PaymentTypeResponse) JsonUtil.toObject(query.list().get(0).getPaymentTypes(), new TypeToken<PaymentTypeResponse>() {
                }.getType()) : null;
    }

    public MemberTypeResponse getMemberTypes() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getMemberTypes() != null ?
                (MemberTypeResponse) JsonUtil.toObject(query.list().get(0).getMemberTypes(), new TypeToken<MemberTypeResponse>() {
                }.getType()) : null;
    }

    public VoucherTypeResponse getVoucherTypes() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getMemberTypes() != null ?
                (VoucherTypeResponse) JsonUtil.toObject(query.list().get(0).getVoucherTypes(), new TypeToken<VoucherTypeResponse>() {
                }.getType()) : null;
    }

    public CategoryResponse getCategories() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getCategories() != null ?
                (CategoryResponse) JsonUtil.toObject(query.list().get(0).getCategories(), new TypeToken<CategoryResponse>() {
                }.getType()) : null;
    }

    public ArrayList<Voucher> getVouchers() {
        VouchersResponse vouchersResponse = getVouchersContent();
        if (vouchersResponse != null && vouchersResponse.getVouchers() != null) {
            return vouchersResponse.getVouchers();
        } else {
            return null;
        }
    }

    public BestVoucherResponse getBestVouchers() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getBestVoucher() != null ?
                (BestVoucherResponse) JsonUtil.toObject(query.list().get(0).getBestVoucher(), new TypeToken<BestVoucherResponse>() {
                }.getType()) : null;
    }

    public FeaturedVoucherResponse getFeaturedVouchers() {
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);

        return query.list().size() > 0 && query.list().get(0).getFeaturedVoucher() != null ?
                (FeaturedVoucherResponse) JsonUtil.toObject(query.list().get(0).getFeaturedVoucher(), new TypeToken<FeaturedVoucherResponse>() {
                }.getType()) : null;
    }

    public ArrayList<Voucher> getTokens() {
        VouchersResponse vouchersResponse = getVouchersContent();
        if (vouchersResponse != null && vouchersResponse.getTokens() != null) {
            return vouchersResponse.getTokens();
        } else {
            return null;
        }
    }


    public void saveVouchersToDb(String vouchers) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setVouchers(vouchers);
            mTableContentDao.update(tableContent);
        } else {
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
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setVouchers(vouchers);
            mTableContentDao.update(tableContent);
        } else {
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
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setNotifs(notifs);
            mTableContentDao.update(tableContent);
        } else {
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
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setMyBoxs(boxs);
            mTableContentDao.update(tableContent);
        } else {
            tableContent = new TableContent();
            tableContent.setMyBoxs(boxs);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void saveVoucherTypesToDb(String voucherType) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setVoucherTypes(voucherType);
            mTableContentDao.update(tableContent);
        } else {
            tableContent = new TableContent();
            tableContent.setVoucherTypes(voucherType);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void saveMemberTypesToDb(String memberType) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setMemberTypes(memberType);
            mTableContentDao.update(tableContent);
        } else {
            tableContent = new TableContent();
            tableContent.setMemberTypes(memberType);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void savePaymentTypeToDb(String paymentType) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setPaymentTypes(paymentType);
            mTableContentDao.update(tableContent);
        } else {
            tableContent = new TableContent();
            tableContent.setPaymentTypes(paymentType);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void saveCategoryToDb(String category) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setCategories(category);
            mTableContentDao.update(tableContent);
        } else {
            tableContent = new TableContent();
            tableContent.setCategories(category);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void saveBestVoucherToDb(String bestVoucher) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setBestVoucher(bestVoucher);
            mTableContentDao.update(tableContent);
        } else {
            tableContent = new TableContent();
            tableContent.setBestVoucher(bestVoucher);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }

    public void saveFeaturedVoucherToDb(String featuredVoucher) {
        TableContent tableContent;
        QueryBuilder<TableContent> query = mTableContentDao.queryBuilder().orderAsc(TableContentDao.Properties.CreatedTime);
        if (query.list().size() > 0) {
            tableContent = query.list().get(0);
            tableContent.setUpdatedTime(System.currentTimeMillis());
            tableContent.setFeaturedVoucher(featuredVoucher);
            mTableContentDao.update(tableContent);
        } else {
            tableContent = new TableContent();
            tableContent.setFeaturedVoucher(featuredVoucher);
            tableContent.setCreatedTime(System.currentTimeMillis());
            mTableContentDao.insert(tableContent);
        }
        KLog.v("============== Content Database saved ===============");
    }
}

