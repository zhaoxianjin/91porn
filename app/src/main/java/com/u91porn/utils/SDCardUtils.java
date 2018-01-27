package com.u91porn.utils;

import android.os.Environment;

/**
 * @author flymegoc
 * @date 2018/1/13
 */

public class SDCardUtils {
    private static final String ROOT_FOLDER = Environment.getExternalStorageDirectory() + "/91porn/";
    public static final String DOWNLOAD_VIDEO_PATH = ROOT_FOLDER + "video/";
    public static final String DOWNLOAD_IMAGE_PATH = ROOT_FOLDER + "image/";
    public static final String DATE_FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String EXPORT_FILE = ROOT_FOLDER + "export.txt";

    /**
     * 存储卡是否挂载
     *
     * @return
     */
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
