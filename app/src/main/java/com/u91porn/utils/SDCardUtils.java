package com.u91porn.utils;

import android.os.Environment;

/**
 *
 * @author flymegoc
 * @date 2018/1/13
 */

public class SDCardUtils {
    /**
     * 存储卡是否挂载
     * @return
     */
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
