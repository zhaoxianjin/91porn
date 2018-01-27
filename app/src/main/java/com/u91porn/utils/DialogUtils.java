package com.u91porn.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.u91porn.R;

/**
 * @author flymegoc
 * @date 2017/12/10
 */

public class DialogUtils {
    public static AlertDialog initLodingDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.LoadingDialogStyle);
        View view = View.inflate(context, R.layout.loading_layout, null);
        if (!TextUtils.isEmpty(message)) {
            TextView textView = view.findViewById(R.id.textView);
            textView.setText(message);
        }
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setCancelable(false);

        return mAlertDialog;
    }
}
