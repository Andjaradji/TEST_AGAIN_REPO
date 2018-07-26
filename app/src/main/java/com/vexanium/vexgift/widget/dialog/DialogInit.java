package com.vexanium.vexgift.widget.dialog;

import android.support.annotation.LayoutRes;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vexanium.vexgift.R;
import com.vexanium.vexgift.app.App;


/**
 * Created by mac on 5/26/17.
 */

public class DialogInit {

    @LayoutRes
    static int getInflateLayout(DialogTheme theme) {
        switch (theme) {
            default:
                return R.layout.item_vexgift_dialog_view;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @UiThread
    public static void init(final VexDialog dialog) {
        final VexDialog.Builder builder = dialog.builder;

        dialog.mdTitle = (TextView) dialog.view.findViewById(R.id.md_title);
        dialog.mdContent = (TextView) dialog.view.findViewById(R.id.md_content);

        dialog.mdBackground = (RelativeLayout) dialog.view.findViewById(R.id.md_background);

        dialog.positiveButtonTxt = (TextView) dialog.view.findViewById(R.id.positive_button_txt);
        dialog.negativeButtonTxt = (TextView) dialog.view.findViewById(R.id.negative_button_txt);
        dialog.positiveButton = (RelativeLayout) dialog.view.findViewById(R.id.positive_button);
        dialog.negativeButton = (RelativeLayout) dialog.view.findViewById(R.id.negative_button);

        //Set up title and content
        if (builder.title != null) {
            dialog.mdTitle.setText(builder.title);
        } else {
            dialog.mdTitle.setVisibility(View.GONE);
        }
//        App.getApplication().setLatoBoldFontToView(dialog.mdTitle);

        if (builder.content != null) {
            dialog.mdContent.setText(builder.content);
        } else {
            dialog.mdContent.setVisibility(View.GONE);
        }
//        App.getApplication().setLatoRegularFontToView(dialog.mdContent);

        // Set up the focus of action buttons
        dialog.positiveButton.setFocusable(true);
        dialog.negativeButton.setFocusable(true);
        if (builder.positiveFocus) {
            dialog.positiveButton.requestFocus();
        }
        if (builder.okFocus) {
            dialog.positiveButton.requestFocus();
        }
        if (builder.negativeFocus) {
            dialog.negativeButton.requestFocus();
        }

        //Set up the text of action button
        TextView positiveTextView = dialog.positiveButtonTxt;
        positiveTextView.setText(builder.positiveText);

        dialog.positiveButton.setTag(DialogAction.POSITIVE);
        dialog.positiveButton.setOnClickListener(dialog);
        dialog.positiveButton.setVisibility(View.VISIBLE);
        if (builder.positiveColor != null)
            dialog.positiveButtonTxt.setTextColor(builder.positiveColor);

        TextView negativeTextView = dialog.negativeButtonTxt;
        negativeTextView.setText(builder.positiveText);

        dialog.negativeButton.setTag(DialogAction.POSITIVE);
        dialog.negativeButton.setOnClickListener(dialog);
        dialog.negativeButton.setVisibility(View.VISIBLE);
        if (builder.negativeColor != null)
            dialog.negativeButtonTxt.setTextColor(builder.negativeColor);

        //Set Up the button based on OptionType
        if (builder.optionType == DialogOptionType.OK) {
            dialog.negativeButton.setVisibility(View.GONE);
            dialog.positiveButtonTxt.setText(builder.okText != null ? builder.okText : DialogDefaultConfig.DEFAULT_OK_TEXT);

        } else if (builder.optionType == DialogOptionType.NONE) {
            dialog.negativeButton.setVisibility(View.GONE);
            dialog.positiveButton.setVisibility(View.GONE);
        } else {
            dialog.positiveButtonTxt.setText(builder.positiveText != null ? builder.positiveText : DialogDefaultConfig.DEFAULT_POSITIVE_TEXT);
            dialog.negativeButtonTxt.setText(builder.negativeText != null ? builder.negativeText : DialogDefaultConfig.DEFAULT_NEGATIVE_TEXT);
        }

        switch (dialog.theme) {
            case BASIC:
//                dialog.mdDivider = dialog.view.findViewById(R.id.md_button_divider);
                dialog.mdCustomView = (LinearLayout) dialog.view.findViewById(R.id.md_custom_view);

                if (builder.optionType == DialogOptionType.OK) {
//                    dialog.mdDivider.setVisibility(View.GONE);
                }

                if (builder.customView != null) {
                    dialog.mdCustomView.addView(builder.customView);
                    dialog.mdCustomView.setVisibility(View.VISIBLE);
                } else {
                    dialog.mdCustomView.setVisibility(View.GONE);
                }

                break;
        }


//        ((App) builder.context.getApplicationContext()).setLatoRegularFontToView(dialog.positiveButtonTxt, dialog.negativeButtonTxt);
//
//        if(builder.isPositiveTextBold)
//            ((App) builder.context.getApplicationContext()).setLatoBoldFontToView(dialog.positiveButtonTxt);
//        if(builder.isNegativeTextBold)
//            ((App) builder.context.getApplicationContext()).setLatoBoldFontToView(dialog.negativeButtonTxt);
//        if(builder.isOkTextBold)
//            ((App) builder.context.getApplicationContext()).setLatoBoldFontToView(dialog.positiveButtonTxt)


        //Set up the listener
        dialog.autoDismiss = builder.autoDismiss;
        dialog.cancelOnTouchOutside = builder.canceledOnTouchOutside;
        dialog.setCancelable(builder.cancelable);

        dialog.view.findViewById(R.id.md_dialog_container).setOnClickListener(dialog);
        dialog.mdBackground.setOnClickListener(dialog);
        dialog.onPositiveCallback = builder.onPositiveCallback;
        dialog.onNegativeCallback = builder.onNegativeCallback;

        if (builder.showListener != null) {
            dialog.setOnShowListener(builder.showListener);
        }
        if (builder.cancelListener != null) {
            dialog.setOnCancelListener(builder.cancelListener);
        }
        if (builder.dismissListener != null) {
            dialog.setOnDismissListener(builder.dismissListener);
        }

    }
}
