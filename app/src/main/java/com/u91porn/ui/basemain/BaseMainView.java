package com.u91porn.ui.basemain;

import com.u91porn.data.model.Category;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 *
 * @author flymegoc
 * @date 2018/1/25
 */

public interface BaseMainView extends BaseView{
    void onLoadCategoryData(List<Category> categoryList);
    void onLoadAllCategoryData(List<Category> categoryList);
}
