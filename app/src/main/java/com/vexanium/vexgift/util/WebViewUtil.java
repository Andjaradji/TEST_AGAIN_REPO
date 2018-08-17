package com.vexanium.vexgift.util;

import android.net.Uri;
import android.text.TextUtils;

import com.socks.library.KLog;
import com.vexanium.vexgift.BuildConfig;
import com.vexanium.vexgift.app.ConstantGroup;
import com.vexanium.vexgift.app.StaticGroup;
import com.vexanium.vexgift.http.Api;
import com.vexanium.vexgift.http.HostType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WebViewUtil {
    public static boolean isNeedToReloadWebPage(String moduleName) {
        if (BuildConfig.DEBUG) return true;

        boolean isNeedToReload = true;
        if (SpUtil.has(ConstantGroup.KEY_LAST_LINK_VERSIONS) && !TextUtils.isEmpty(SpUtil.getString(ConstantGroup.KEY_LAST_LINK_VERSIONS))) {
            Map lastLoadedVersions = (Map) JsonUtil.toObject(SpUtil.getString(ConstantGroup.KEY_LAST_LINK_VERSIONS), Map.class);
            if (lastLoadedVersions != null && lastLoadedVersions.containsKey(moduleName)) {
                String lastLoadedVersion = (String)lastLoadedVersions.get(moduleName);
                String latestVersion = ""+getWebPageVersion(moduleName);
                KLog.v("IntraWeb", "Validate version [" + moduleName + "]: " + lastLoadedVersion + " / " + latestVersion);
                if (lastLoadedVersion.equalsIgnoreCase(latestVersion)) {
                    isNeedToReload = false;
                }
            }
        }

        return isNeedToReload;
    }

    public static String getPageUrl(String moduleName) {
        return getPageUrl(moduleName, "");
    }

    public static String getPageUrl(String moduleName, String queryString) {
        String hostUrl = Api.getHost(HostType.WEB_URL) + "/app/" + moduleName;
        hostUrl = String.format("%s?lc=%s", hostUrl, Locale.getDefault());
        if (getWebPageVersion(moduleName) > 0) {
            hostUrl = String.format("%s&v=%s", hostUrl, ""+getWebPageVersion(moduleName));
        }
        if (!TextUtils.isEmpty(queryString)) {
            hostUrl = String.format("%s&%s", hostUrl, queryString);
        }

        return hostUrl;
    }

    public static int getWebPageVersion(String moduleName) {
        int version = 0;
//        if (StaticGroup.latestLinkVersions == null) {
//            StaticGroup.latestLinkVersions = OptionResponse.getLinkVersion();
//        }
//
//        if (StaticGroup.latestLinkVersions != null && StaticGroup.latestLinkVersions.containsKey(moduleName)
//                && StaticGroup.latestLinkVersions.get(moduleName) > 0) {
//            version = StaticGroup.latestLinkVersions.get(moduleName);
//        }

        return version;
    }

    public static void saveLoadedVersionCode(String moduleName, String url) {
        if (TextUtils.isEmpty(url)) return;

        Uri uri = Uri.parse(url);
        String version = uri.getQueryParameter("v");
        if (!TextUtils.isEmpty(version)) {
            KLog.v("IntraWeb", "Saved version : " + version);

            Map<String, String> lastLoadedVersions = new HashMap<>();
            if (SpUtil.has(ConstantGroup.KEY_LAST_LINK_VERSIONS) && !TextUtils.isEmpty(SpUtil.getString(ConstantGroup.KEY_LAST_LINK_VERSIONS))) {
                lastLoadedVersions = (Map<String, String>)JsonUtil.toObject(SpUtil.getString(ConstantGroup.KEY_LAST_LINK_VERSIONS), Map.class);
            }

            lastLoadedVersions.put(moduleName, version);

            SpUtil.put(ConstantGroup.KEY_LAST_LINK_VERSIONS, JsonUtil.toString(lastLoadedVersions));
        }
    }
}
