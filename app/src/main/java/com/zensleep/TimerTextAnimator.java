package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextView;

public class TimerTextAnimator {

    private ValueAnimator sizeAnimator;
    private ValueAnimator colorAnimator;

    private boolean animating = false;

    public void start(TextView textView) {

        stop(textView);

        animating = true;

        final float normalSizeSp = 32f;
        final float pulseSizeSp = 36f;

        // 💓 PULSAR DE VERDADE PELO TAMANHO DO TEXTO
        sizeAnimator = ValueAnimator.ofFloat(normalSizeSp, pulseSizeSp);
        sizeAnimator.setDuration(600);
        sizeAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sizeAnimator.setRepeatMode(ValueAnimator.REVERSE);

        sizeAnimator.addUpdateListener(animation -> {
            if (!animating) return;

            float value = (float) animation.getAnimatedValue();
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
        });

        sizeAnimator.start();

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

    public void stop(TextView textView) {

        animating = false;

        if (sizeAnimator != null) {
            sizeAnimator.cancel();
            sizeAnimator = null;
        }

        if (colorAnimator != null) {
            colorAnimator.cancel();
            colorAnimator = null;
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);
        textView.setTextColor(Color.parseColor("#00FF9C"));
        textView.setShadowLayer(
                30f,
                0f,
                0f,
                Color.parseColor("#00FF9C")
        );
    }
}
