package com.u91porn.ui.basemain;

import com.u91porn.data.model.Category;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/25
 */

public interface IBaseMain {
    void loadAllCategoryData(int categoryType);

    void loadCategoryData(int categoryType);

    Category findCategoryById(Long id);

    void updateCategoryData(List<Category> categoryList);
}
