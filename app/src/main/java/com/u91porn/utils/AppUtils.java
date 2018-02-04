package com.u91porn.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ImageView;

import com.u91porn.R;
import com.u91porn.utils.constants.Keys;

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

    public static String buildTitle(String title, String author, String datetime) {
        return "<h3>" + title + "</h3>"
                + "<div align=\"right\"><span>" +
                "作者："+author + "&nbsp;&nbsp;&nbsp;时间：" + datetime +"&nbsp;"+
                "</span>\n" +
                "</div>"
                + "<HR style=\"FILTER: progid:DXImageTransform.Microsoft.Glow(color=#987cb9,strength=10)\" width=\"100%\" color=#987cb9 SIZE=1>";
    }

    public static String buildHtml(String data, Context context) {
        boolean isNightModel = (boolean) SPUtils.get(context, Keys.KEY_SP_OPEN_NIGHT_MODE, false);
        String backgroundColor;
        String fontColor;
        if (isNightModel) {
            backgroundColor = "#49505A";
            fontColor = "#ADB4BE";
        } else {
            backgroundColor = "#FFFFFF";
            fontColor = "#353C46";
        }
        String dat1 = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">" +
                "<style type=\"text/css\">\n" +
                "body\n" +
                "{ \n" +
                "background: " + backgroundColor + " no-repeat fixed center; \n" +
                "color: " + fontColor +
                "}\n" +
                "</style>" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"app\" style=\"width:100%;height:100%;word-wrap: break-word;word-break:break-all;\">";
        String dat2 = "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        return dat1 + data + dat2;
    }
}
