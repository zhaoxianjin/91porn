package com.u91porn.utils;

/**
 * @author flymegoc
 * @date 2018/1/22
 */

public class FragmentUtils {
    /**
     * FragmentPagerAdapter 内部生成tag的方法
     *
     * @param viewId viewpager id
     * @param id     getItemId 获取到的id
     * @return tag
     */
    public static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
