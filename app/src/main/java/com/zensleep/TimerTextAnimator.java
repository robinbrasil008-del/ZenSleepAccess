package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class TimerTextAnimator {

    private ValueAnimator scaleAnimator;
    private ValueAnimator colorAnimator;

    private boolean animating = false;

    public void start(textView) {

        stop();

        animating = true;

        // 💓 PULSAR
        scaleAnimator = ValueAnimator.ofFloat(1f, 1.15f);
        scaleAnimator.setDuration(700);
        scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimator.setRepeatMode(ValueAnimator.REVERSE);

        scaleAnimator.addUpdateListener(animation -> {

            if (!animating) return;

            float value = (float) animation.getAnimatedValue();

            textView.setScaleX(value);
            textView.setScaleY(value);

        });

        scaleAnimator.start();


        // 💚 COR VERDE ANIMADA
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


        // ✨ GLOW
        textView.setShadowLayer(
                30f,
                0f,
                0f,
                Color.parseColor("#00FF9C")
        );
    }

    public void stop(textView) {

        animating = false;

        if (scaleAnimator != null) scaleAnimator.cancel();
        if (colorAnimator != null) colorAnimator.cancel();

        textView.setScaleX(1f);
        textView.setScaleY(1f);
        textView.setTextColor(Color.parseColor("#00FF9C"));
    }
}
