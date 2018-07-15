package com.u91porn.utils;

import android.text.TextUtils;

import com.u91porn.data.model.User;

/**
 * 用户帮助
 *
 * @author flymegoc
 * @date 2017/12/29
 */

public class UserHelper {
    /**
     * 随机生成10位机器指纹
     *
     * @return 指纹码
     */
    public static String randomFingerprint() {
        String keys = "0123456789";
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < keys.length(); i++) {
            int pos = (int) (Math.random() * keys.length());
            pos = (int) Math.floor(pos);
            key.append(keys.charAt(pos));
        }
        return key.toString();
    }

    /**
     * 随机生成4位验证码
     *
     * @return 4位验证码
     */
    public static String randomCaptcha() {
        String keys = "0123456789";
        int length = 4;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int pos = (int) (Math.random() * keys.length());
            pos = (int) Math.floor(pos);
            key.append(keys.charAt(pos));
        }
        return key.toString();
    }

    /**
     * 随机生成32位机器指纹
     *
     * @return 指纹码
     */
    public static String randomFingerprint2() {
        String keys = "abcdefghijklmnopqrstuvwxyz0123456789";
        int keyLength = 32;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < keyLength; i++) {
            int pos = (int) (Math.random() * keys.length());
            pos = (int) Math.floor(pos);
            key.append(keys.charAt(pos));
        }
        return key.toString();
    }

    public static boolean isUserInfoComplete(User user) {
        return user != null && user.getUserId() > 0 && !TextUtils.isEmpty(user.getUserName());
    }

    public static boolean isPornVideoLoginSuccess(String html) {
        return (!html.contains("登录") || !html.contains("注册") || html.contains("退出"));
    }
}
