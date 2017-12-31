package com.u91porn.utils;

import android.view.View;
import android.widget.TextView;

/**
 * @author flymegoc
 * @date 2017/12/27
 */

public class LoadHelperUtils {
    public static void setLoadingText(View view, int vid, String text) {
        findViewById(view, vid).setText(text);
    }

    public static void setEmptyText(View view, int vid, String text) {
        findViewById(view, vid).setText(text);
    }

    public static void setErrorText(View view, int vid, String text) {
        findViewById(view, vid).setText(text);
    }

    private static TextView findViewById(View view, int vid) {
        return view.findViewById(vid);
    }
}
