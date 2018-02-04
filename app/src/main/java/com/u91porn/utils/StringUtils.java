package com.u91porn.utils;

import android.text.TextUtils;

/**
 * @author flymegoc
 * @date 2018/1/28
 */

public class StringUtils {
    /**
     * \u3000\u3000 首行缩进
     * 空格：&#160;
     * &#8194;半个中文字更准确点，
     * &#8195;一个中文字但用起来会比中文字宽一点点。
     */

    public static String subString(String str, int startIndex, int endIndex) {
        if (TextUtils.isEmpty(str) || startIndex < 0 || endIndex < 0 || startIndex >= str.length() || endIndex - startIndex < 0) {
            return "";
        }
        if (endIndex > str.length()) {
            return str.substring(startIndex, str.length());
        }
        return str.substring(startIndex, endIndex);
    }
}
