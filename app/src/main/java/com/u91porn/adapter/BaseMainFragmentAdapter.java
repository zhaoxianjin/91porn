package com.u91porn.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.u91porn.data.model.Category;
import com.u91porn.ui.common.CommonFragment;
import com.u91porn.ui.forum91porn.Forum91IndexFragment;
import com.u91porn.ui.forum91porn.ForumFragment;
import com.u91porn.ui.index.IndexFragment;
import com.u91porn.ui.meizitu.MeiZiTuFragment;
import com.u91porn.ui.recentupdates.RecentUpdatesFragment;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/24
 * @describe
 */

public class BaseMainFragmentAdapter extends FragmentPagerAdapter {

    private static final String TAG = BaseMainFragmentAdapter.class.getSimpleName();
    private List<Category> categoryList;
    private int categoryType;

    public BaseMainFragmentAdapter(FragmentManager fm, List<Category> categoryList, int categoryType) {
        super(fm);
        this.categoryList = categoryList;
        this.categoryType = categoryType;
    }

    @Override
    public Fragment getItem(int position) {
        return buildFragmentItem(categoryType, position);
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

    private Fragment buildFragmentItem(int type, int position) {
        Category category = categoryList.get(position);
        switch (type) {
            case Category.TYPE_91PORN:
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
            case Category.TYPE_91PORN_FORUM:
                if (("index").equals(category.getCategoryValue())) {
                    Forum91IndexFragment forum91IndexFragment = Forum91IndexFragment.getInstance();
                    forum91IndexFragment.setCategory(category);
                    return forum91IndexFragment;
                } else {
                    ForumFragment forumFragment = ForumFragment.getInstance();
                    forumFragment.setCategory(category);
                    return forumFragment;
                }
            case Category.TYPE_MEI_ZI_TU:
                MeiZiTuFragment meiZiTuFragment = MeiZiTuFragment.getInstance();
                meiZiTuFragment.setCategory(category);
                return meiZiTuFragment;
            default:
        }
        return new Fragment();
    }
}
