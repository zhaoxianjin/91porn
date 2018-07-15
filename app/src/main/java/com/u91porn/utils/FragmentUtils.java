package com.u91porn.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.orhanobut.logger.Logger;

/**
 * @author flymegoc
 * @date 2018/1/22
 */

public class FragmentUtils {
    private static final String TAG = FragmentUtils.class.getSimpleName();

    /**
     * 切换
     *
     * @param fragmentManager fm管理器
     * @param currentFragment 当前
     * @param toShowFragment  将要显示
     * @param viewId          容器id
     * @param itemId          position标识id
     * @param isInnerReplace  是否是同一位置替换
     * @return 当前显示
     */
    public static Fragment switchContent(FragmentManager fragmentManager, Fragment currentFragment, Fragment toShowFragment, int viewId, long itemId, boolean isInnerReplace) {
        if (fragmentManager == null) {
            return null;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        //如果当前为空，则直接加入
        if (currentFragment == null) {
            transaction.add(viewId, toShowFragment, FragmentUtils.makeFragmentName(viewId, itemId)).commit();
            Logger.t(TAG).d("currentFragment is null,just add new one");
            return toShowFragment;
        }
        //如果当前等于要显示的，直接返回，其实并不会触发，因为点击相同位置，BottomNavigationBar 并不会回调点击事件
        if (currentFragment == toShowFragment) {
            Logger.t(TAG).d("currentFragment equals toShowFragment,just return");
            return toShowFragment;
        }
        // 先判断当前位置是否已经存在一个add过的
        String name = FragmentUtils.makeFragmentName(viewId, itemId);
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if (fragment == null) {
            // 隐藏当前的fragment，add下一个到Activity中
            transaction.hide(currentFragment).add(viewId, toShowFragment, FragmentUtils.makeFragmentName(viewId, itemId)).commit();
            Logger.t(TAG).d("old fragment is null,just hide and add new one");
            return toShowFragment;
        } else {
            //同一位置切换，则先移除旧，替换新的（例如：从91视频切换到朱古力视频，是同一位置）
            if (isInnerReplace) {
                transaction.remove(fragment);
                //再add新的
                transaction.add(viewId, toShowFragment, FragmentUtils.makeFragmentName(viewId, itemId)).commit();
                Logger.t(TAG).d("isInnerReplace is true ,remove old and add new");
            } else {
                transaction.hide(currentFragment).show(fragment).commit();
                Logger.t(TAG).d("isInnerReplace is false ,just hide and show");
            }
            return toShowFragment;
        }
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
