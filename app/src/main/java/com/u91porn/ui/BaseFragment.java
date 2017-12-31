package com.u91porn.ui;

import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
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

    protected void showMessage(String msg,int type) {
        TastyToast.makeText(getContext().getApplicationContext(), msg, TastyToast.LENGTH_SHORT,type).show();
    }

    public String getTitle() {
        return "";
    }
}
