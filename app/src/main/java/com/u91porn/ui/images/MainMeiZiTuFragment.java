package com.u91porn.ui.images;


import android.support.v4.app.Fragment;

import com.u91porn.data.model.Category;
import com.u91porn.ui.basemain.BaseMainFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class MainMeiZiTuFragment extends BaseMainFragment {

    @Override
    public int getCategoryType() {
        return Category.TYPE_MEI_ZI_TU;
    }

    public static MainMeiZiTuFragment getInstance() {
        return new MainMeiZiTuFragment();
    }

    @Override
    public boolean isNeedDestroy() {
        return true;
    }
}
