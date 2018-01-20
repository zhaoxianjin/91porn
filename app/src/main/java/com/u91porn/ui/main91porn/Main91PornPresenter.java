package com.u91porn.ui.main91porn;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.model.Category;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/19
 */

public class Main91PornPresenter extends MvpBasePresenter<Main91PornView> implements IMain91Porn {

    private DataBaseManager dataBaseManager;

    public Main91PornPresenter(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    @Override
    public void loadAllCategoryData() {
        final List<Category> categoryList = dataBaseManager.loadAllCategoryData();
        ifViewAttached(new ViewAction<Main91PornView>() {
            @Override
            public void run(@NonNull Main91PornView view) {
                view.onLoadAllCategoryData(categoryList);
            }
        });
    }

    @Override
    public void loadCategoryData() {
        final List<Category> categoryList = dataBaseManager.loadCategoryData();
        ifViewAttached(new ViewAction<Main91PornView>() {
            @Override
            public void run(@NonNull Main91PornView view) {
                view.onLoadCategoryData(categoryList);
            }
        });
    }

    @Override
    public Category findCategoryById(Long id) {
        return dataBaseManager.findCategoryById(id);
    }

    @Override
    public void updateCategoryData(List<Category> categoryList) {
        dataBaseManager.updateCategoryData(categoryList);
    }
}
