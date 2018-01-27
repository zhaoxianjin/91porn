package com.u91porn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.navi2.component.support.NaviFragment;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.model.Category;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.eventbus.BaseUrlChangeEvent;
import com.u91porn.eventbus.ProxySetEvent;
import com.u91porn.utils.Keys;
import com.u91porn.utils.PlaybackEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author flymegoc
 * @date 2017/11/20
 * @describe
 */

public abstract class BaseFragment extends NaviFragment {
    private final String TAG = getClass().getSimpleName();
    private final String KEY_SAVE_DIN_STANCE_STATE_CATEGORY = "key_save_din_stance_state_category";
    protected final LifecycleProvider<FragmentEvent> provider = NaviLifecycle.createFragmentLifecycleProvider(this);
    protected Context context;
    protected Activity activity;
    protected Category category;
    protected boolean mIsLoadedData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getContext();
        activity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (savedInstanceState != null) {
            category = (Category) savedInstanceState.getSerializable(KEY_SAVE_DIN_STANCE_STATE_CATEGORY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SAVE_DIN_STANCE_STATE_CATEGORY, category);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            handleOnVisibilityChangedToUser(isVisibleToUser);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProxySetEvent(ProxySetEvent proxySetEvent) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBaseUrlChangeEvent(BaseUrlChangeEvent baseUrlChangeEvent) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(false);
        }
    }

    /**
     * 处理对用户是否可见
     *
     * @param isVisibleToUser 可见
     */
    private void handleOnVisibilityChangedToUser(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                mIsLoadedData = true;
                onLazyLoadOnce();
            }
            onVisibleToUser();
        } else {
            // 对用户不可见
            onInvisibleToUser();
        }
    }

    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    protected void onLazyLoadOnce() {
    }

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected void onVisibleToUser() {
    }

    /**
     * 对用户不可见时触发该方法
     */
    protected void onInvisibleToUser() {
    }

    public String getTitle() {
        return "";
    }

    /**
     * 带动画的启动activity
     */
    public void startActivityWithAnimotion(Intent intent) {
        startActivity(intent);
        playAnimation();
    }

    /**
     * 带动画的启动activity
     */
    public void startActivityForResultWithAnimotion(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        playAnimation();
    }

    private void playAnimation() {
        if (activity != null) {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.side_out_left);
        }
    }

    protected void goToPlayVideo(UnLimit91PornItem unLimit91PornItem) {
        Intent intent = PlaybackEngine.getPlaybackEngineIntent(getContext());
        intent.putExtra(Keys.KEY_INTENT_UNLIMIT91PORNITEM, unLimit91PornItem);
        if (activity != null) {
            startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.side_out_left);
        } else {
            showMessage("无法获取宿主Activity", TastyToast.INFO);
        }
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.t(TAG).d("------------------onDestroyView()");
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        Logger.t(TAG).d("------------------onDestroy()");
    }

    protected void showMessage(String msg, int type) {
        TastyToast.makeText(context.getApplicationContext(), msg, TastyToast.LENGTH_SHORT, type).show();
    }
}
