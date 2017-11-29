package com.u91porn.ui;

import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxFragment;

/**
 * @author flymegoc
 * @date 2017/11/20
 * @describe
 */

public abstract class BaseFragment extends RxFragment {

    protected void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public String getTitle() {
        return "";
    }
}
