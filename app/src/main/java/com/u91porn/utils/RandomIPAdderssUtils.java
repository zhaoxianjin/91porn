package com.u91porn.utils;

import java.util.Random;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe 随机生成IPAdderss
 */

public class RandomIPAdderssUtils {
    private static Random mRandom = new Random();

    /**
     * 获取随机ip地址
     *
     * @return
     */
    public static String getRandomIPAdderss() {

        return String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255));
    }
}
