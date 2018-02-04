package com.u91porn.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author flymegoc
 * @date 2018/1/22
 */

public class FragmentUtils {
    /**
     * 切换
     *
     * @param fragmentManager fm管理器
     * @param currentFragment 当前
     * @param toShowFragment  将要显示
     * @param viewId          容器id
     * @param itemId          标识id
     * @return 当前显示
     */
    public static Fragment switchContent(FragmentManager fragmentManager, Fragment currentFragment, Fragment toShowFragment, int viewId, long itemId) {
        if (fragmentManager == null) {
            return null;
        }
        Fragment fragment = null;
        if (currentFragment != toShowFragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            // 先判断是否被add过
            String name = FragmentUtils.makeFragmentName(viewId, itemId);
            fragment = fragmentManager.findFragmentByTag(name);
            if (fragment == null) {
                // 隐藏当前的fragment，add下一个到Activity中
                transaction.add(viewId, toShowFragment, FragmentUtils.makeFragmentName(viewId, itemId)).commit();
                fragment = toShowFragment;
            } else if (fragment != toShowFragment) {
                //同一位置不同，则先移除旧，替换新的（例如：从91视频切换到朱古力视频，是同一位置）
                transaction.remove(fragment);
                //再add新的
                transaction.add(viewId, toShowFragment, FragmentUtils.makeFragmentName(viewId, itemId)).commit();
                fragment = toShowFragment;
            } else {
                // 隐藏当前的fragment，显示下一个
                transaction.hide(currentFragment).show(fragment).commit();
            }
        }
        return fragment;
    }

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
