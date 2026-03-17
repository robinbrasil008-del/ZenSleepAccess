package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.widget.TextView;

public class TimerTextAnimator {

    private ValueAnimator pulseAnimator;
    private ValueAnimator colorAnimator;

    private boolean animating = false;

    public void start(TextView textView) {

        stop(textView);

        animating = true;

        // 💓 PULSO VISÍVEL DE VERDADE (alpha + glow)
        pulseAnimator = ValueAnimator.ofFloat(0f, 1f);
        pulseAnimator.setDuration(700);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);

        pulseAnimator.addUpdateListener(animation -> {
            if (!animating) return;

            float value = (float) animation.getAnimatedValue();

            // alpha pulsa
            float alpha = 0.55f + (value * 0.45f); // vai de 0.55 até 1.00
            textView.setAlpha(alpha);

            // glow pulsa junto
            float glow = 10f + (value * 28f); // vai de 10 até 38
            textView.setShadowLayer(
                    glow,
                    0f,
                    0f,
                    Color.parseColor("#00FF9C")
            );
        });

        pulseAnimator.start();

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
    }

    public void stop(TextView textView) {

        animating = false;

        if (pulseAnimator != null) {
            pulseAnimator.cancel();
            pulseAnimator = null;
        }

        if (colorAnimator != null) {
            colorAnimator.cancel();
            colorAnimator = null;
        }

        textView.setAlpha(1f);
        textView.setTextColor(Color.parseColor("#00FF9C"));
        textView.setShadowLayer(
                30f,
                0f,
                0f,
                Color.parseColor("#00FF9C")
        );
    }
}
