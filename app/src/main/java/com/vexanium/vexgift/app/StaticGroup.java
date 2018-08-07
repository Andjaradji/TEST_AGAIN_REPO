package com.vexanium.vexgift.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
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
import com.vexanium.vexgift.bean.response.UserLoginResponse;
import com.vexanium.vexgift.bean.response.VoucherResponse;
import com.vexanium.vexgift.module.detail.ui.VoucherDetailActivity;
import com.vexanium.vexgift.module.login.ui.LoginActivity;
import com.vexanium.vexgift.module.main.ui.MainActivity;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.util.TpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.vexanium.vexgift.app.ConstantGroup.KYC_ACCEPTED;
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
        String title = dialogTitle.isEmpty() ? "" : dialogTitle;

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

    public static void goToVoucherDetailActivity(Activity activity, VoucherResponse voucherResponse, ImageView ivVoucher) {
        Intent intent = new Intent(activity, VoucherDetailActivity.class);
        intent.putExtra("voucher", JsonUtil.toString(voucherResponse));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, ivVoucher, "voucher_image");
        activity.startActivity(intent, options.toBundle());
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
        if (!getRecentTaskInfo(context)) {
            targetIntent = new Intent(context, MainActivity.class);
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            targetIntent = new Intent(context, MainActivity.class);
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        targetIntent.putExtra("t_type", "noti");
        targetIntent.putExtra("t_url", url);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

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

    public static NotificationCompat.Builder getDefaultNotificationBuilder(Context context, String message, String title, Intent targetIntent, long whenTimeStamp, int badgeNumber, boolean autoCancel) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_logo_notif : R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
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
}
