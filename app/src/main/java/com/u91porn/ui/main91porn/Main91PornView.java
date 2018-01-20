package com.u91porn.ui.main91porn;

import com.u91porn.data.model.Category;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 *
 * @author flymegoc
 * @date 2018/1/19
 */

public interface Main91PornView extends BaseView{
    void onLoadCategoryData(List<Category> categoryList);
    void onLoadAllCategoryData(List<Category> categoryList);
}
