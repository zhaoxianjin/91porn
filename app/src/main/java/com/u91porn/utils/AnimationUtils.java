package com.u91porn.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * @author flymegoc
 * @date 2018/1/19
 */

public class AnimationUtils {
    public static void rotateUp(View view) {
        RotateAnimation rotate = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(200);
        rotate.setRepeatCount(0);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        view.startAnimation(rotate);
    }
    public static void rotateDown(View view) {
        RotateAnimation rotate = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(200);
        rotate.setRepeatCount(0);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        view.startAnimation(rotate);
    }
}
