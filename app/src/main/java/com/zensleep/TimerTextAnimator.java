package com.zensleep;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class TimerTextAnimator {

    private AnimatorSet pulseSet;
    private ValueAnimator colorAnimator;

    private boolean animating = false;

    public void start(TextView textView) {

        stop(textView);

        animating = true;

        textView.post(() -> {
            if (!animating) return;

            textView.setPivotX(textView.getWidth() / 2f);
            textView.setPivotY(textView.getHeight() / 2f);

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, View.SCALE_X, 1f, 1.20f);
            scaleX.setDuration(550);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, View.SCALE_Y, 1f, 1.20f);
            scaleY.setDuration(550);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);

            pulseSet = new AnimatorSet();
            pulseSet.playTogether(scaleX, scaleY);
            pulseSet.start();
        });

        colorAnimator = ValueAnimator.ofArgb(
                Color.parseColor("#00FF9C"),
                Color.parseColor("#00C853")
        );
        colorAnimator.setDuration(700);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.addUpdateListener(animation -> {
            if (!animating) return;
            textView.setTextColor((int) animation.getAnimatedValue());
        });
        colorAnimator.start();

        textView.setShadowLayer(
                30f,
                0f,
                0f,
                Color.parseColor("#00FF9C")
        );
    }

    public void stop(TextView textView) {

        animating = false;

        if (pulseSet != null) {
            pulseSet.cancel();
            pulseSet = null;
        }

        if (colorAnimator != null) {
            colorAnimator.cancel();
            colorAnimator = null;
        }

        textView.setScaleX(1f);
        textView.setScaleY(1f);
        textView.setTextColor(Color.parseColor("#00FF9C"));
        textView.setShadowLayer(
                30f,
                0f,
                0f,
                Color.parseColor("#00FF9C")
        );
    }
}
