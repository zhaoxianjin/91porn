package com.u91porn.ui.pigav;

import com.u91porn.data.model.Category;
import com.u91porn.ui.basemain.BaseMainFragment;

/**
 * @author flymegoc
 * @date 2018/1/29
 */

public class MainPigAvFragment extends BaseMainFragment {

    public static MainPigAvFragment getInstance() {
        return new MainPigAvFragment();
    }

    @Override
    public int getCategoryType() {
        return Category.TYPE_PIG_AV;
    }

    @Override
    public boolean isNeedDestroy() {
        return true;
    }
}
