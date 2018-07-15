package com.u91porn.ui;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.trello.rxlifecycle2.LifecycleTransformer;

import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2017/11/17
 * @describe
 */

public interface BaseView extends MvpView {

    void showLoading(boolean pullToRefresh);

    void showContent();

    void showMessage(String msg,int type);

    void showError(String message);
}
