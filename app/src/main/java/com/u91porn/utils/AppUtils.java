package com.u91porn.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;

import com.u91porn.R;

/**
 * @author flymegoc
 * @date 2017/10/28
 * @describe
 */

public class AppUtils {
    /**
     * drawable 着色
     */
    public static void setImageViewColor(ImageView view, int colorResId) {
        //mutate()
        Drawable modeDrawable = view.getDrawable().mutate();
        Drawable temp = DrawableCompat.wrap(modeDrawable);
        ColorStateList colorStateList = ColorStateList.valueOf(view.getResources().getColor(colorResId));
        DrawableCompat.setTintList(temp, colorStateList);
        view.setImageDrawable(temp);
    }

    public static void setColorSchemeColors(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.green), ContextCompat.getColor(context, R.color.lightred), ContextCompat.getColor(context, R.color.yeloo));
    }

    public static String buildHtml(String data) {
        String dat1 = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"app\">";
        String dat2 = "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        return dat1 + data + dat2;
    }
}
