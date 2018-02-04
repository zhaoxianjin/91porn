package com.u91porn.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.u91porn.data.model.Category;
import com.u91porn.ui.porn91video.common.CommonFragment;
import com.u91porn.ui.porn91forum.Forum91IndexFragment;
import com.u91porn.ui.porn91forum.ForumFragment;
import com.u91porn.ui.porn91video.index.IndexFragment;
import com.u91porn.ui.images.meizitu.MeiZiTuFragment;
import com.u91porn.ui.images.mm99.Mm99Fragment;
import com.u91porn.ui.pigav.PigAvFragment;
import com.u91porn.ui.porn91video.recentupdates.RecentUpdatesFragment;

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
    private FragmentTransaction mCurTransaction;
    private FragmentManager mFragmentManager;

    private boolean isDestroy = false;

    public BaseMainFragmentAdapter(FragmentManager fm, List<Category> categoryList, int categoryType) {
        super(fm);
        mFragmentManager = fm;
        this.categoryList = categoryList;
        this.categoryType = categoryType;
    }

    public boolean isDestroy() {
        return isDestroy;
    }

    public void setDestroy(boolean destroy) {
        isDestroy = destroy;
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
            case Category.TYPE_PIG_AV:
                PigAvFragment pigAvFragment = PigAvFragment.getInstance();
                pigAvFragment.setCategory(category);
                return pigAvFragment;
            case Category.TYPE_99_MM:
                Mm99Fragment mm99Fragment = Mm99Fragment.getInstance();
                mm99Fragment.setCategory(category);
                return mm99Fragment;
            default:
        }
        return new Fragment();
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (isDestroy) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.remove((Fragment) object);
        } else {
            super.destroyItem(container, position, object);
        }

    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
        if (isDestroy) {
            if (mCurTransaction != null) {
                mCurTransaction.commitNowAllowingStateLoss();
                mCurTransaction = null;
            }
        }
    }
}
