package com.vexanium.vexgift.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.socks.library.KLog;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.bean.model.User;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.EmptyResponse;
import com.vexanium.vexgift.bean.response.HttpResponse;
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.database.TableContentDaoUtil;
import com.vexanium.vexgift.http.HostType;
import com.vexanium.vexgift.http.RetrofitException;
import com.vexanium.vexgift.http.manager.RetrofitManager;
import com.vexanium.vexgift.module.detail.ui.VoucherDetailActivity;
import com.vexanium.vexgift.module.login.ui.LoginActivity;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.module.premium.ui.PremiumMemberActivity;
import com.vexanium.vexgift.util.AlarmUtil;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogDefaultConfig;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;

import static com.vexanium.vexgift.app.ConstantGroup.KYC_NONE;


/**
 * Created by hizkia on 12/03/18.
 */

public class StaticGroup {
    public static final int SHORTCUT_BAR = 1;
    public static final int HOT_LIST = 2;
    public static final int EXPLORE_BAR = 3;
    public static final int CATEGORY_BAR = 4;
    public static final int NORMAL_COUPON = 5;
    public static final int COMPLETE_FORM = 6;
    public static final int CONNECT_FB = 7;

    public static final int SLEEP_SIGN_TIME = 30 * 60000;
    public static final int VEX_ADDRESS_VERIF_TIME = 60 * 60000;

    public static UserLoginResponse currentUser;
    public static String userSession;

    public static NotificationManager notificationManager;

    public static int kycStatus = KYC_NONE;
    public static Boolean isPasswordSet;

    public static String os = Build.VERSION.RELEASE;
    public static String VERSION = null;
    public static long VERSION_CODE = 0;
    public static String reg_id = "";
    private static String CHANNEL_ID = "1121";
    public static boolean latestLinkVersions;


    public static void initialize(Context context) {
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            VERSION = i.versionName;
            VERSION_CODE = i.getLongVersionCode();
            KLog.v("VERSION : " + VERSION + " " + VERSION_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static String getUserSession() {
        if (userSession == null) {
            User user = User.getCurrentUser(App.getContext());
            if (user != null)
                userSession = user.getSessionKey();
        }
        return userSession;
    }

    public static void shareWithShareDialog(Context context, String message, String dialogTitle) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setType("text/plain");
        String title = dialogTitle.isEmpty() ? "Share" : dialogTitle;

        try {
            if (context != null) {
                Intent chooserIntent = Intent.createChooser(sendIntent, title);
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooserIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean openShareIntent(Context context, String packageName, String message, String gaLabel, String alterPackageName) {
        if (context != null && !TextUtils.isEmpty(packageName)) {
            try {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);

                if (intent != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setPackage(packageName);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        KLog.v("openShareIntent : " + packageName);
                        context.startActivity(shareIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!TextUtils.isEmpty(alterPackageName)) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static void syncCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().flush();
        }
    }


    public static void goToVoucherDetailActivity(Activity activity, Voucher voucher, ImageView ivVoucher) {
        Intent intent = new Intent(activity, VoucherDetailActivity.class);
        intent.putExtra("voucher", JsonUtil.toString(voucher));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, ivVoucher, "voucher_image");
        activity.startActivity(intent, options.toBundle());
    }

    public static void goToVoucherDetailActivity(Activity activity, Voucher voucher) {
        Intent intent = new Intent(activity, VoucherDetailActivity.class);
        intent.putExtra("voucher", JsonUtil.toString(voucher));
        activity.startActivity(intent);
    }

    public static void copyToClipboard(Context context, String content) {
        try {
            if (!TextUtils.isEmpty(content)) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", content);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, R.string.security_google2fa_copy_message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private static void clearCookies(Context context) {
        KLog.v("clearCookies");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }

    public static void logOutClear(Context context, int code) {

        KLog.v("================= logOutClear =================");

        User user = User.getCurrentUser(context);

        //facebook logout
        LoginManager.getInstance().logOut();

        TpUtil tpUtil = new TpUtil(context);
        tpUtil.removePrivate();

        TableContentDaoUtil.getInstance().clearAllCache();

        clearCookies(context);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("log_out", true);
        if (code > 0) {
            intent.putExtra("code", code);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void sendLocalNotification(Context context, String title, String message, String url) {
        NotificationCompat.Builder builder = StaticGroup.getDefaultNotificationBuilder(context, message, title, null, System.currentTimeMillis(), 0, true);
        Intent targetIntent;
        //TODO  asign targetIntent
//        if (!getRecentTaskInfo(context)) {
//            targetIntent = new Intent(context, MainActivity.class);
//            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        } else {
        targetIntent = new Intent(context, MainActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        }
        targetIntent.putExtra("t_type", "noti");
        targetIntent.putExtra("t_url", url);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setCategory(Notification.CATEGORY_MESSAGE);
        builder.setPriority(2);

        NotificationManager manager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        createNotificationChannel(context);

        Notification notification = builder.build();
        manager.cancel(ConstantGroup.ID_LOCAL_PUSH);
        manager.notify(ConstantGroup.ID_LOCAL_PUSH, notification);

//        try {
//            if (SettingCondition.getCurrentSettingCondition(App.getContext()).isNotifSoundEnabled())
//                StaticGroup.goSoundEffectSound(context, R.raw.twinkle);
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
    }

    public static void sendLocalNotificationWithBigImage(Context context, String title, String message, String url, Bitmap img) {
        NotificationCompat.Builder builder = StaticGroup.getDefaultNotificationBuilder(context, message, title, null, System.currentTimeMillis(), 0, true);
        Intent targetIntent;
        //TODO  asign targetIntent
//        if (!getRecentTaskInfo(context)) {
//            targetIntent = new Intent(context, MainActivity.class);
//            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        } else {
        targetIntent = new Intent(context, MainActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        }
        targetIntent.putExtra("t_type", "noti");
        targetIntent.putExtra("t_url", url);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setPriority(2)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(img));

        NotificationManager manager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        createNotificationChannel(context);

        Notification notification = builder.build();
        manager.cancel(ConstantGroup.ID_LOCAL_PUSH);
        manager.notify(ConstantGroup.ID_LOCAL_PUSH, notification);

//        try {
//            if (SettingCondition.getCurrentSettingCondition(App.getContext()).isNotifSoundEnabled())
//                StaticGroup.goSoundEffectSound(context, R.raw.twinkle);
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = ConstantGroup.NOTIF_CHANNEL_NAME;
            String description = ConstantGroup.NOTIF_CHANNEL_DESC;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static String[] findPackNameClass(Context context, String pkName) {
        String[] packName = null;
        PackageManager mPm = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_SEND, null);
        mainIntent.setType("image/jpeg");
        List<ResolveInfo> resolveInfoList = mPm.queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo != null) {
                String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
                String className = resolveInfo.activityInfo.name;
                ComponentName componentName = new ComponentName(pkgName, className);
                CharSequence title = resolveInfo.loadLabel(mPm);
                KLog.v(pkgName + " " + className + " " + componentName + " " + title);

                if (pkgName.equalsIgnoreCase(pkName)) {
                    packName = new String[2];
                    packName[0] = pkgName;
                    packName[1] = className;
                    break;
                }
            }
        }

        return packName;
    }

    private static boolean isGoogleMarketUrl(String url) {
        return url.startsWith("market://") || ((url.startsWith("http://") || url.startsWith("https://")) && url.contains("play.google.com"));
    }

    public static void shareWithEmail(Context context, String address, String subject, String message) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            if (!TextUtils.isEmpty(address)) {
                KLog.v("address : " + address);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
            }
            if (!TextUtils.isEmpty(subject)) {
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
            if (!TextUtils.isEmpty(message)) {
                emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            }

            try {
                String[] pkName = StaticGroup.findPackNameClass(context, "com.google.android.gm");
                if (pkName != null) {
                    if (!TextUtils.isEmpty(pkName[0])) {
                        emailIntent.setComponent(new ComponentName(pkName[0], pkName[1]));                // 패키지명, 클래스명
                        context.startActivity(emailIntent);
                    } else {
                        // TODO: 09/08/18 update string res
                        context.startActivity(Intent.createChooser(emailIntent, "Report Select"));
                    }
                } else {
                    // TODO: 09/08/18 update string res
                    context.startActivity(Intent.createChooser(emailIntent, "Report Select"));
                }
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void shareWithSms(Context context, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            try {
                context.startActivity(sendIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            //sendIntent.setType("vnd.android-dir/mms-sms");
            sendIntent.putExtra("sms_body", message);
            try {
                context.startActivity(sendIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean handleUrl(Context context, String url) {
        boolean isAlreadyHandled = false;
        if (isGoogleMarketUrl(url)) {
            StaticGroup.openGooglePlay(context, url);
            isAlreadyHandled = true;
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            isAlreadyHandled = false;
        } else if (url.startsWith("vexgift://link")) {
            isAlreadyHandled = false;
        } else if (url.startsWith("vexgift://open.web")) {
            try {
                Uri uri = Uri.parse(url);
                String content = uri.getQueryParameter("url");
                StaticGroup.openAndroidBrowser(context, content);
                isAlreadyHandled = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (url.startsWith("vexgift://share")) {
            try {
                Uri uri = Uri.parse(url);
                String method = uri.getQueryParameter("mt");
                String packageName = uri.getQueryParameter("pn");
                String alternativePackageName = uri.getQueryParameter("apn");
                String content = uri.getQueryParameter("msg");

                if (!TextUtils.isEmpty(content)) {
                    if (!TextUtils.isEmpty(method)) {
                        if (method.equalsIgnoreCase("sms")) {
                            StaticGroup.shareWithSms(context, content);
                            return true;
                        } else if (method.equalsIgnoreCase("email")) {
                            StaticGroup.shareWithEmail(context, "", "", content);
                            return true;
                        } else if (method.equalsIgnoreCase("copy")) {
                            StaticGroup.copyToClipboard(context, content);
                            return true;
                        }
                    }

                    if (!TextUtils.isEmpty(packageName)) {
                        if (packageName.contains("facebook")) {
                            StaticGroup.copyToClipboard(context, content);
                        }
                        StaticGroup.shareWithPackageName(context, packageName, content, method, alternativePackageName);
                    } else {
                        // TODO: 09/08/18 update string res
                        StaticGroup.shareWithShareDialog(context, content, "Share link");
                    }
                    isAlreadyHandled = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (url.startsWith("mailto")) {
            final Activity activity = (Activity) context;
            if (activity != null) {
                MailTo mt = MailTo.parse(url);
                StaticGroup.shareWithEmail(context, mt.getTo(), mt.getSubject(), mt.getBody());
                isAlreadyHandled = true;
                KLog.v("to2 : " + mt.getTo() + "\nsubject2 : " + mt.getSubject() + "\nbody2 : " + mt.getBody());
            }

        } else if (url.startsWith("vexgift://mail")) {
            try {
                Uri uri = Uri.parse(url);
                String to = uri.getQueryParameter("to");
                String subject = uri.getQueryParameter("subject");
                String body = uri.getQueryParameter("body");
                StaticGroup.shareWithEmail(context, to, subject, body);
                isAlreadyHandled = true;
                KLog.v("to : " + to + "\nsubject : " + subject + "\nbody : " + body);
            } catch (Exception e) {
                KLog.v("e : " + e.getMessage());
            }

        } else if (url.startsWith("vexgift://notification")) {
            try {
                Uri uri = Uri.parse(url);

                String title = uri.getQueryParameter("title");
                String message = uri.getQueryParameter("msg");
                String timeStampStr = uri.getQueryParameter("at");
                long timeStamp = TextUtils.isEmpty(timeStampStr) ? System.currentTimeMillis() : Long.parseLong(timeStampStr) * 1000;
                String targetUrl = uri.getQueryParameter("url");

                KLog.v("timeStamp : " + timeStampStr + " / " + timeStamp);

                AlarmUtil.getInstance().startLocalNotiAlarm(context, title, message, targetUrl, timeStamp);
                isAlreadyHandled = true;
            } catch (Exception e) {
                KLog.v("e : " + e.getMessage());
            }

        }

        return isAlreadyHandled;
    }

    public static boolean isInIDLocale() {
        boolean ret = true;

        if (Locale.getDefault() != null && !TextUtils.isEmpty(Locale.getDefault().toString())) {
            ret = Locale.getDefault().toString().contains("in");
        }

        return ret;
    }

    public static NotificationCompat.Builder getDefaultNotificationBuilder(Context context, String message, String title, Intent targetIntent, long whenTimeStamp, int badgeNumber, boolean autoCancel) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_logo_notif : R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_notif));
            builder.setColor(ColorUtil.getColor(context, R.color.point_color));

        }

        if (autoCancel) {
            builder.setAutoCancel(true);
        }

        if (context.getResources() != null && TextUtils.isEmpty(title)) {
            title = context.getResources().getString(R.string.app_name);
        }
        builder.setContentTitle(title);

        if (targetIntent == null) {
            targetIntent = new Intent(context, MainActivity.class);
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.setWhen(whenTimeStamp);
        if (badgeNumber > 0) {
            builder.setNumber(badgeNumber);
        }
        //----------------------------//
        builder.setTicker(message);
        builder.setContentText(message);

        return builder;
    }

    public static boolean getRecentTaskInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTaskList = am.getRecentTasks(100, Intent.FLAG_ACTIVITY_NEW_TASK);
        if (recentTaskList != null) {
            for (ActivityManager.RecentTaskInfo recentTask : recentTaskList) {
                Intent intent = recentTask.baseIntent;
                ComponentName cName = intent.getComponent();
                if (cName.getPackageName().contains("com.vexanium")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void sendReferrerData(String sess) {
        TpUtil tpUtil = new TpUtil(App.getContext());
        String parentCode = tpUtil.getString(TpUtil.KEY_REFERRER, "");

        if (!TextUtils.isEmpty(parentCode)) {
            try {
                parentCode = URLDecoder.decode(parentCode, "UTF-8");
                KLog.v("PARENT TEST", "referDbData.referrer SpUtil : " + parentCode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

//        if (TextUtils.isEmpty(parentCode)) {
//            TablePreference prefDb = TablePreferenceDaoUtil.getInstance().getItem();
//
//            if (prefDb != null) {
//                if (!TextUtils.isEmpty(prefDb.getParentCode())) {
//                    KLog.v("PARENT TEST", "referDbData.referrer database : " + prefDb.getPageCode());
//                    try {
//                        parentCode = URLDecoder.decode(prefDb.getParentCode(), "UTF-8");
//                        KLog.v("PARENT TEST", "referDbData.referrer decoded database : " + parentCode);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }

        if (!TextUtils.isEmpty(parentCode)) {
            Uri uri = Uri.parse("?" + parentCode);
            KLog.json(JsonUtil.toString(uri));
            final String invCode = uri.getQueryParameter("i");
            KLog.v("PARENT TEST", "send Referrer Data : " + parentCode + "     i=" + invCode);
//            RetrofitManager.getInstance(HostType.COMMON_API).requestAddParent(sess, invCode)
//                    .subscribe(new Subscriber<EmptyResponse>() {
//                        @Override
//                        public void onCompleted() {
//                            KLog.v("PARENT TEST", "AutoReferral_onCompleted");
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            KLog.v("PARENT TEST", "AutoReferral_onError");
//
//                            try {
//                                RetrofitException error = (RetrofitException) e;
//                                HttpResponse response = error.getErrorBodyAs(HttpResponse.class);
//                                if (response != null) {
//                                    KLog.v("PARENT TEST", "Success 1");
//                                    if (response.getMeta().getStatus() == 10101 || (response.getMeta().getStatus() == 200)) {
//                                        KLog.v("PARENT TEST", "Success 2");
////                                        TablePreference prefDb = TablePreferenceDaoUtil.getInstance().getItem(true);
////                                        prefDb.setParentCode("");
////                                        TablePreferenceDaoUtil.getInstance().insertOrUpdateItem(prefDb);
//
//                                        TpUtil tpUtil = new TpUtil(App.getContext());
//                                        tpUtil.put(TpUtil.KEY_REFERRER, "");
//                                        tpUtil.remove(TpUtil.KEY_REFERRER);
//                                    }
//                                }
//                            } catch (Exception ex) {
//                                KLog.v("HPtes undefined ");
//                                ex.printStackTrace();
//                            }
//                            e.printStackTrace();
//                        }
//
//                        @Override
//                        public void onNext(EmptyResponse response) {
//                            KLog.v("PARENT TEST", "AutoReferral_OK");
//
////                            TablePreference prefDb = TablePreferenceDaoUtil.getInstance().getItem(true);
////                            prefDb.setParentCode("");
////                            TablePreferenceDaoUtil.getInstance().insertOrUpdateItem(prefDb);
//
//                            TpUtil tpUtil = new TpUtil(App.getContext());
//                            tpUtil.put(TpUtil.KEY_REFERRER, "");
//                            tpUtil.remove(TpUtil.KEY_REFERRER);
//                        }
//                    });
        }
    }

    public static boolean isScreenOn(Context context, boolean defaultValue) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            return pm.isInteractive();
        }

        return defaultValue;
    }


    public static void openVexgiftGooglePlay(Context context) {
        String storeUrl = "https://play.google.com/store/apps/details?id=com.vexanium.vexgift";
        StaticGroup.openGooglePlay(context, storeUrl);
    }

    public static void openGooglePlay(Context context, String marketUrl) {
        if (!TextUtils.isEmpty(marketUrl)) {
            try {
                context.getPackageManager().getPackageInfo("com.android.vending", 0);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                KLog.v("openGooglePlay via market : " + marketUrl);
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                String replacedUrl;
                if (marketUrl.startsWith("market://")) {
                    String detailUrl = marketUrl.replace("market://", "");
                    replacedUrl = "https://play.google.com/store/apps/" + detailUrl;
                } else {
                    replacedUrl = marketUrl;
                }
                KLog.v("openGooglePlay via browser : " + replacedUrl);
                openAndroidBrowser(context, replacedUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void shareWithPackageName(Context context, String packageName, String message, String gaLabel, String alterPackageName) {
        if (context != null && !TextUtils.isEmpty(packageName)) {
            boolean isCompleted = openShareIntent(context, packageName, message, gaLabel, alterPackageName);
            if (!isCompleted) {
                openShareIntent(context, alterPackageName, message, gaLabel, "");
            }
        }
    }

    public static void openAndroidBrowser(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) return;

        try {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PackageManager packageManager = context.getPackageManager();
            Intent fakeIntent = new Intent();
            fakeIntent.setAction("android.intent.action.VIEW");
            fakeIntent.setData(Uri.parse("http://www.google.com"));

            ResolveInfo selectedBrowserInfo = packageManager.resolveActivity(fakeIntent, PackageManager.MATCH_DEFAULT_ONLY);

            ActivityInfo targetBrowserInfo = null;

            if (selectedBrowserInfo != null && selectedBrowserInfo.activityInfo != null && !selectedBrowserInfo.activityInfo.name.endsWith("ResolverActivity")) {
                targetBrowserInfo = selectedBrowserInfo.activityInfo;
                KLog.v("Browser Process", "Default Browser package name : " + targetBrowserInfo.applicationInfo.packageName);

            } else {
                fakeIntent.addCategory("android.intent.category.BROWSABLE");
                List<ResolveInfo> localBrowserList = packageManager.queryIntentActivities(fakeIntent, 0);

                if (localBrowserList.size() > 0) {
                    ArrayList<String> browserList = new ArrayList<>();
                    browserList.add("com.UCMobile.intl");
                    browserList.add("com.android.chrome");
                    browserList.add("com.android.browser");
                    browserList.add("com.opera.browser");

                    outerLoop:
                    for (String packageName : browserList) {
                        for (ResolveInfo resolveInfo : localBrowserList) {
                            if (resolveInfo != null) {
                                final ActivityInfo activity = resolveInfo.activityInfo;
                                if (activity != null) {
                                    String pkgName = activity.applicationInfo.packageName;
                                    if (pkgName != null && pkgName.equalsIgnoreCase(packageName)) {
                                        targetBrowserInfo = activity;
                                        break outerLoop;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (targetBrowserInfo != null && !TextUtils.isEmpty(targetBrowserInfo.name) && !TextUtils.isEmpty(targetBrowserInfo.applicationInfo.packageName)) {
                KLog.v("Browser Process", "Browser package name : " + targetBrowserInfo.applicationInfo.packageName);
                browserIntent.setClassName(targetBrowserInfo.applicationInfo.packageName, targetBrowserInfo.name);
            } else {
                KLog.v("Browser Process", "Default Browser or Select dialog opened");
            }

            context.startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Voucher> getVouchers(ArrayList<Voucher> origin, int count) {
        ArrayList<Voucher> vouchers = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            vouchers.add(origin.get(i));
//        }
        for(Voucher v : origin){
            if(v.getVendor().getName().equalsIgnoreCase("KFC")){
                vouchers.add(v);
            }
            if(v.getVendor().getName().equalsIgnoreCase("VexPizza")){
                vouchers.add(v);
            }
            if(v.getVendor().getName().equalsIgnoreCase("Coffeelicious")){
                vouchers.add(v);
            }
        }
        return vouchers;
    }

    public static Voucher getVoucherById(ArrayList<Voucher> origin, int id) {
        ArrayList<Voucher> vouchers = new ArrayList<>();
        for (Voucher voucher : origin) {
            if (voucher.getId() == id) return voucher;
        }
        return null;
    }

    public static ArrayList<com.vexanium.vexgift.bean.model.Notification> setAllNotifToAbsolute(ArrayList<com.vexanium.vexgift.bean.model.Notification> notifs) {
        ArrayList<com.vexanium.vexgift.bean.model.Notification> arrayList = new ArrayList<>();
        for (com.vexanium.vexgift.bean.model.Notification n : notifs) {
            n.setNew(false);
            arrayList.add(n);
        }

        TableContentDaoUtil.getInstance().saveNotifsToDb(JsonUtil.toString(arrayList));
        return arrayList;
    }

    public static void showPremiumMemberDialog(Context context, String title, String message) {
        new VexDialog.Builder(context)
                .optionType(DialogOptionType.OK)
                .title(title)
                .content(message)
                .autoDismiss(true)
                .show();
    }

    public static void showPremiumMemberDialog(final Context context) {
        String title = context.getString(R.string.premium_dialog_title);
        String desc = context.getString(R.string.premium_dialog_desc);
        String pButton = context.getString(R.string.premium_dialog_button);
        String nButton = context.getString(R.string.cancel);

        new VexDialog.Builder(context)
                .optionType(DialogOptionType.YES_NO)
                .positiveText(pButton)
                .negativeText(nButton)
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(context, PremiumMemberActivity.class);
                        context.startActivity(intent);
                    }
                })
                .title(title)
                .content(desc)
                .autoDismiss(true)
                .show();
    }

    public static void showCommonErrorDialog(Context context, String title, String message) {
        new VexDialog.Builder(context)
                .optionType(DialogOptionType.OK)
                .title(title)
                .content(message)
                .autoDismiss(true)
                .show();
    }

    public static void showCommonErrorDialog(Context context, String message) {
        String title = context.getString(R.string.cm_dialog_all_error_title);
        String desc = message;
        showCommonErrorDialog(context, title, desc);
    }

    public static void showCommonErrorDialog(Context context, int code) {
        String title = context.getString(R.string.cm_dialog_all_error_title);
        String desc = String.format(context.getString(R.string.cm_dialog_all_error_desc), String.valueOf(code));
        showCommonErrorDialog(context, title, desc);
    }

    public static Bundle getFacebookFields() {
        Bundle parameters = new Bundle();
        StringBuilder parameterStr = new StringBuilder();
        parameterStr.append("email,");
        parameterStr.append("id,");
        parameterStr.append("cover,");
        parameterStr.append("name,");
        parameterStr.append("first_name,");
        parameterStr.append("last_name");
        parameters.putString("fields", parameterStr.toString());

        return parameters;
    }

    public static List<String> getFacebookPermission() {
        return Arrays.asList(
                "email",
                "public_profile"
        );
    }




}
