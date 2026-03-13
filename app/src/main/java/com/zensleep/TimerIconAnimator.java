package com.zensleep;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class TimerIconAnimator {

    private ObjectAnimator animator;

    public void start(View icon) {

        stop();

        animator = ObjectAnimator.ofFloat(icon, View.ROTATION, 0f, 360f);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }

    public void stop() {

        if (animator != null) {
            animator.cancel();
            animator = null;
        }

    }
}
