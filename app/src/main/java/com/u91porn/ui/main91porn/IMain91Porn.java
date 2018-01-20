package com.u91porn.ui.main91porn;

import com.u91porn.data.model.Category;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/19
 */

public interface IMain91Porn extends IBaseMain91Porn {
    void loadAllCategoryData();

    void loadCategoryData();

    Category findCategoryById(Long id);

    void updateCategoryData(List<Category> categoryList);
}
