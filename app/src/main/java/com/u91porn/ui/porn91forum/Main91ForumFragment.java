package com.u91porn.ui.porn91forum;


import android.support.v4.app.Fragment;

import com.u91porn.data.model.Category;
import com.u91porn.ui.basemain.BaseMainFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class Main91ForumFragment extends BaseMainFragment {


    private static final String TAG = Main91ForumFragment.class.getSimpleName();

    @Override
    public int getCategoryType() {
        return Category.TYPE_91PORN_FORUM;
    }

    public static Main91ForumFragment getInstance() {
        return new Main91ForumFragment();
    }
}
