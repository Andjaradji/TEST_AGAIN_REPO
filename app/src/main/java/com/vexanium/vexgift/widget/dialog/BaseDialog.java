package com.vexanium.vexgift.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by mac on 5/24/17.
 */

public class BaseDialog extends Dialog  implements DialogInterface.OnShowListener{
    public RelativeLayout view;
    private OnShowListener showListener;

    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, @StyleRes int theme){
        super(context,theme);
    }

    @Override
    public View findViewById(int id) {
        return view.findViewById(id);
    }

    @Override
    public final void setOnShowListener(OnShowListener listener) {
        showListener = listener;
    }

    final void setOnShowListenerInternal() {
        super.setOnShowListener(this);
    }

    final void setViewInternal(View view) {
        super.setContentView(view);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (showListener != null) {
            showListener.onShow(dialog);
        }
    }


}
