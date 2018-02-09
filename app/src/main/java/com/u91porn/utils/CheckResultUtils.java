package com.u91porn.utils;

import android.text.TextUtils;

/**
 * @author flymegoc
 * @date 2018/2/7
 */

public class CheckResultUtils {
    public static boolean check91PronVideoConnectIsSuccess(String html) {
        return !TextUtils.isEmpty(html) && (html.contains("Chinese homemade video") || html.contains("91PORN旗下视频聊天") || html.contains("警告︰此网站只适合十八岁或以上人士观看"));
    }

    public static boolean checkPigAvVideoConnectIsSuccess(String html) {
        return !TextUtils.isEmpty(html) && html.contains("熱門") && html.contains("長片") && html.contains("每日") && html.contains("日韓") && html.contains("精選") || html.contains("朱古力");
    }

    public static boolean check91PornForumConnectIsSuccess(String html) {
        return !TextUtils.isEmpty(html) && html.contains("91自拍达人原创区") && html.contains("91自拍达人原创申请") && html.contains("我爱我妻");
    }
}
