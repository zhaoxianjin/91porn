package com.u91porn.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.gyf.barlibrary.ImmersionBar;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.navi2.component.support.NaviAppCompatActivity;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.navi.NaviLifecycle;
import com.u91porn.R;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.play.PlayVideoActivity;
import com.u91porn.utils.AppManager;
import com.u91porn.utils.Keys;

/**
 * @author flymegoc
 * @date 2017/11/20
 * @describe
 */

public abstract class BaseAppCompatActivity extends NaviAppCompatActivity {
    private AppManager appManager = AppManager.getAppManager();
    public final LifecycleProvider<ActivityEvent> provider = NaviLifecycle.createActivityLifecycleProvider(this);
    protected ImmersionBar mImmersionBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        SwipeBackHelper.onCreate(this);
        appManager.addActivity(this);
        mImmersionBar = ImmersionBar.with(this).fitsSystemWindows(true,R.color.colorPrimary,R.color.colorPrimary,1f).keyboardEnable(true);
        mImmersionBar.init();   //所有子类都将继承这些相同的属性
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
        appManager.finishActivity(this);
        if (mImmersionBar != null){
            mImmersionBar.destroy();
        }
    }

    /**
     * 带动画的启动activity
     */
    public void startActivityWithAnimotion(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.side_out_left);
    }

    /**
     * 带动画的启动activity
     */
    public void startActivityForResultWithAnimotion(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.side_out_left);
    }

    protected void goToPlayVideo(UnLimit91PornItem unLimit91PornItem) {
        Intent intent = new Intent(this, PlayVideoActivity.class);
        intent.putExtra(Keys.KEY_INTENT_UNLIMIT91PORNITEM, unLimit91PornItem);
        startActivityWithAnimotion(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.side_out_right);
    }

    protected void showMessage(String msg, int type) {
        TastyToast.makeText(getApplicationContext(), msg, TastyToast.LENGTH_SHORT, type).show();
    }

    protected void showTopMessage(String message) {
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
