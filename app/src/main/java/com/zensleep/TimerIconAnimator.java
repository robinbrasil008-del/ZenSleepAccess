package com.zensleep;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class TimerIconAnimator {

    private ObjectAnimator rotateAnimator;

    public void start(View icon) {

        if (rotateAnimator != null) return;

        rotateAnimator = ObjectAnimator.ofFloat(icon, "rotation", 0f, 360f);
        rotateAnimator.setDuration(2000);
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.start();
    }

    public void stop() {

        if (rotateAnimator != null) {
            rotateAnimator.cancel();
            rotateAnimator = null;
        }

    }
}
