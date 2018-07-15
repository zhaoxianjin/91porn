package com.u91porn.behavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.u91porn.R;

/**
 * 显示隐藏
 *
 * @author flymegoc
 * @date 2017/12/30
 */

public class BottomBehavior extends CoordinatorLayout.Behavior<LinearLayout> {
    /**
     * 视频控件的高度即为整个appbarlayout 可移动的距离
     */
    private int height;
    private float oLy;
    private boolean isFirst = true;

    public BottomBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        boolean returnValue = super.onDependentViewChanged(parent, child, dependency);
        if (dependency instanceof AppBarLayout) {
            float dis = Math.abs(dependency.getY());
            float f = dis / height;
            child.setY(oLy - child.getHeight() * f);
        }
        return returnValue;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, LinearLayout child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        if (isFirst) {
            isFirst = false;
            if (oLy == 0) {
                oLy = child.getY() + child.getHeight();
            }
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                if (view instanceof AppBarLayout) {
                    height = view.getMeasuredHeight();
                    break;
                }
            }

            child.setY(oLy);
        }

        return true;
    }
}
