package com.u91porn.ui.setting;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

/**
 * @author flymegoc
 * @date 2018/2/6
 */

public interface SettingView extends MvpView {
    void showTesting(boolean isTest);

    void testSuccess(String message, QMUICommonListItemView qmuiCommonListItemView, String key);

    void testFailure(String message,QMUICommonListItemView qmuiCommonListItemView,String key);
}
