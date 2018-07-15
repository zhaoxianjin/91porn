package com.u91porn.ui.basemain;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.model.Category;

import java.util.List;

/**
 *
 * @author flymegoc
 * @date 2018/1/25
 */

public class BaseMainPresenter extends MvpBasePresenter<BaseMainView> implements IBaseMain{
    protected DataBaseManager dataBaseManager;

    public BaseMainPresenter(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    @Override
    public void loadAllCategoryData(int type) {
        final List<Category> categoryList = dataBaseManager.loadAllCategoryData(type);
        ifViewAttached(new ViewAction<BaseMainView>() {
            @Override
            public void run(@NonNull BaseMainView view) {
                view.onLoadAllCategoryData(categoryList);
            }
        });
    }

    @Override
    public void loadCategoryData(int type) {
        final List<Category> categoryList = dataBaseManager.loadCategoryData(type);
        ifViewAttached(new ViewAction<BaseMainView>() {
            @Override
            public void run(@NonNull BaseMainView view) {
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
