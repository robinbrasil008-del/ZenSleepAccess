package com.zensleep;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.widget.TextView;

public class TimerTextAnimator {

    private boolean animating = false;
    private ValueAnimator colorAnimator;

    public void start(TextView textView) {

        stop(textView);

        animating = true;

        // 💓 PULSO REAL (ANDROID NATIVO - NÃO FALHA)
        textView.animate()
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (!animating) return;

                        textView.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(500)
                                .withEndAction(this)
                                .start();
                    }
                })
                .start();

        // 💚 COR ANIMADA
        colorAnimator = ValueAnimator.ofObject(
                new ArgbEvaluator(),
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

    public void stop(TextView textView) {

        animating = false;

        textView.animate().cancel();

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
