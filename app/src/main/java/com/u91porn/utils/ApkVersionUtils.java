package com.u91porn.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 应用版本工具
 *
 * @author flymegoc
 * @date 2017/12/23
 */

public class ApkVersionUtils {
    /**
     * 获取versionCode
     *
     * @param context cox
     * @return versionCode
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取versionName
     *
     * @param context cox
     * @return versionName
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
