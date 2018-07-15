package com.u91porn.ui.porn91video;


import android.support.v4.app.Fragment;

import com.u91porn.data.model.Category;
import com.u91porn.ui.basemain.BaseMainFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class Main91PronVideoFragment extends BaseMainFragment {

    @Override
    public int getCategoryType() {
        return Category.TYPE_91PORN;
    }

    public static Main91PronVideoFragment getInstance() {
        return new Main91PronVideoFragment();
    }
}
