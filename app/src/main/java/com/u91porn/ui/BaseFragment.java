package com.u91porn.ui;

import android.widget.Toast;

import com.trello.navi2.component.support.NaviFragment;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;

/**
 * @author flymegoc
 * @date 2017/11/20
 * @describe
 */

public abstract class BaseFragment extends NaviFragment {
    protected final LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(this);

    protected void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public String getTitle() {
        return "";
    }
}
