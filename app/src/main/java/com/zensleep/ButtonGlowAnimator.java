package com.zensleep;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ButtonGlowAnimator {

    private ValueAnimator glowAnimator;
    private boolean animating = false;

    public void start(View button) {

        stop(button);

        animating = true;

        // 🔥 GARANTE PERFORMANCE
        button.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        glowAnimator = ValueAnimator.ofFloat(0.85f, 1f);
        glowAnimator.setDuration(1200);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        glowAnimator.setInterpolator(new LinearInterpolator());

        glowAnimator.addUpdateListener(animation -> {

            if (!animating) return;

            float value = (float) animation.getAnimatedValue();

            // ✨ brilho suave
            button.setAlpha(value);

            // 💎 leve escala (efeito premium)
            button.setScaleX(1f + (value - 0.85f) * 0.1f);
            button.setScaleY(1f + (value - 0.85f) * 0.1f);

        });

        glowAnimator.start();
    }

    public void stop(View button) {

        animating = false;

        if (glowAnimator != null) glowAnimator.cancel();

        if (button != null) {
            button.setAlpha(1f);
            button.setScaleX(1f);
            button.setScaleY(1f);

            button.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }
}
