package com.u91porn.ui;

import android.content.Intent;

import com.sdsmdg.tastytoast.TastyToast;
import com.trello.navi2.component.support.NaviFragment;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.utils.Keys;
import com.u91porn.utils.SwitchPlaybackEngine;

/**
 * @author flymegoc
 * @date 2017/11/20
 * @describe
 */

public abstract class BaseFragment extends NaviFragment {
    protected final LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(this);

    protected void showMessage(String msg, int type) {
        TastyToast.makeText(getContext().getApplicationContext(), msg, TastyToast.LENGTH_SHORT, type).show();
    }

    public String getTitle() {
        return "";
    }

    protected void goToPlayVideo(UnLimit91PornItem unLimit91PornItem) {
        Intent intent = SwitchPlaybackEngine.getPlaybackEngineIntent(getContext());
        intent.putExtra(Keys.KEY_INTENT_UNLIMIT91PORNITEM, unLimit91PornItem);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.startActivityWithAnimotion(intent);
        }
    }
}
