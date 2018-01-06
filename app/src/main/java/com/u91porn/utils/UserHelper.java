package com.u91porn.utils;

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
     * 随机生成32位机器指纹
     *
     * @return 指纹码
     */
    public static String randomFingerprint2() {
        String keys = "abcdefghijklmnopqrstuvwxyz0123456789";
        int keyLength=32;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < keyLength; i++) {
            int pos = (int) (Math.random() * keys.length());
            pos = (int) Math.floor(pos);
            key.append(keys.charAt(pos));
        }
        return key.toString();
    }
}
