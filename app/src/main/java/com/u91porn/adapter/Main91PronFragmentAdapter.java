package com.u91porn.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.u91porn.data.model.Category;
import com.u91porn.ui.BaseFragment;
import com.u91porn.ui.common.CommonFragment;
import com.u91porn.ui.index.IndexFragment;
import com.u91porn.ui.main91porn.Main91PronContainerFragment;
import com.u91porn.ui.recentupdates.RecentUpdatesFragment;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/24
 * @describe
 */

public class Main91PronFragmentAdapter extends FragmentPagerAdapter {

    private static final String TAG = Main91PronContainerFragment.class.getSimpleName();
    private List<Category> categoryList;

    public Main91PronFragmentAdapter(FragmentManager fm, List<Category> categoryList) {
        super(fm);
        this.categoryList = categoryList;
    }

    @Override
    public Fragment getItem(int position) {
        Logger.t(TAG).d("getItem  执行了");
        Category category = categoryList.get(position);
        if (("index").equals(category.getCategoryValue())) {
            IndexFragment indexFragment = IndexFragment.getInstance();
            indexFragment.setCategory(category);
            return indexFragment;
        } else if ("watch".equals(category.getCategoryValue())) {
            RecentUpdatesFragment recentUpdatesFragment = RecentUpdatesFragment.newInstance();
            recentUpdatesFragment.setCategory(category);
            return recentUpdatesFragment;
        } else {
            CommonFragment commonFragment = CommonFragment.getInstance();
            commonFragment.setCategory(category);
            if (!"top1".equals(category.getCategoryValue())) {
                commonFragment.setM(null);
            } else {
                commonFragment.setM("-1");
            }
            return commonFragment;
        }
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categoryList.get(position).getCategoryName();
    }

    @Override
    public long getItemId(int position) {
        return categoryList.get(position).getId();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Category category = categoryList.get(position);
        Logger.t(TAG).d("destroyItem  执行了  " + category.getCategoryName());
        super.destroyItem(container, position, object);
    }
}
