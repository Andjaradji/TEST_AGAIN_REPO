package com.vexanium.vexgift.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.support.annotation.NonNull;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.vexanium.vexgift.R;
import com.vexanium.vexgift.util.ColorUtil;
import com.vexanium.vexgift.util.JsonUtil;
import com.vexanium.vexgift.widget.dialog.DialogAction;
import com.vexanium.vexgift.widget.dialog.DialogOptionType;
import com.vexanium.vexgift.widget.dialog.VexDialog;

public class BaseWebChromeClient extends WebChromeClient {
    private Context mContext;
    private VexDialog alertDialog;
    private android.webkit.JsResult jsResult;

    public BaseWebChromeClient(Context context) {
        mContext = context;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
        DialogInfo dialogInfo = getDialogInfo(message);

        try {
            alertDialog = new VexDialog.Builder(mContext)
                    .optionType(DialogOptionType.OK)
                    .title(dialogInfo.title)
                    .content(dialogInfo.message)
                    .okText(dialogInfo.positive)
                    .autoDismiss(true)
                    .cancelable(true)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            result.confirm();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.cancel();

        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final android.webkit.JsResult result) {
        DialogInfo dialogInfo = getDialogInfo(message);

        jsResult = result;
        try {
            alertDialog = new VexDialog.Builder(mContext)
                    .optionType(DialogOptionType.YES_NO)
                    .title(dialogInfo.title)
                    .content(dialogInfo.message)
                    .positiveText(dialogInfo.positive)
                    .negativeText(dialogInfo.negative)
                    .negativeColor(ColorUtil.getColor(mContext, R.color.material_black_5555))
                    .cancelListener(new CancelListener(result))
                    .autoDismiss(true)
                    .cancelable(true)
                    .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            result.confirm();
                        }
                    })
                    .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                        @Override
                        public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                            result.cancel();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
        WebView newWebView = new WebView(mContext);

        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();

        return true;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        final GeolocationPermissions.Callback myCallback = callback;
        final String originMsg = origin;

        alertDialog = new VexDialog.Builder(mContext)
                .optionType(DialogOptionType.YES_NO)
                .title("Request message")
                .content("Allow current location?")
                .positiveText("Allow")
                .negativeText("Decline")
                .negativeColor(ColorUtil.getColor(mContext, R.color.material_black_5555))
                .onPositive(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        myCallback.invoke(originMsg, false, false);
                    }
                })
                .onNegative(new VexDialog.MaterialDialogButtonCallback() {
                    @Override
                    public void onClick(@NonNull VexDialog dialog, @NonNull DialogAction which) {
                        myCallback.invoke(originMsg, true, false);
                    }
                })
                .show();
    }

    public void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            if (jsResult != null) {
                jsResult.cancel();
            }
            alertDialog.dismiss();
        }
    }

    private DialogInfo getDialogInfo(String message) {
        DialogInfo dialogInfo = new DialogInfo();
        dialogInfo.title = mContext.getString(R.string.app_name);
        dialogInfo.message = message;
        dialogInfo.positive = mContext.getString(R.string.ok);
        dialogInfo.negative = mContext.getString(R.string.cancel);

        if (message.contains("{") && message.contains("}")) {
            dialogInfo = (DialogInfo) JsonUtil.toObject(message, DialogInfo.class);
        }

        return dialogInfo;
    }

    private class CancelListener implements VexDialog.OnCancelListener, VexDialog.OnClickListener {
        private final JsResult mResult;

        CancelListener(JsResult result) {
            mResult = result;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            mResult.cancel();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            mResult.cancel();
        }
    }

    class DialogInfo {
        public String title;
        public String message;
        public String positive;
        public String negative;
    }

}
