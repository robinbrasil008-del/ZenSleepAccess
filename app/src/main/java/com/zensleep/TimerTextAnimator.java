package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class TimerTextAnimator {

    private ValueAnimator scaleAnimator;
    private ValueAnimator colorAnimator;

    private boolean animating = false;

    public void start(TextView textView) {

        stop(textView);

        animating = true;

        // 🔥 GARANTE PERFORMANCE (IMPORTANTE)
        textView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // 🔥 ESPERA A VIEW TER TAMANHO (ESSA É A CHAVE DO BUG)
        textView.post(() -> {

            // 💓 PULSAR (AGORA FUNCIONA)
            scaleAnimator = ValueAnimator.ofFloat(1f, 1.18f);

            scaleAnimator.setDuration(600);
            scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleAnimator.setRepeatMode(ValueAnimator.REVERSE);

            scaleAnimator.addUpdateListener(animation -> {

                if (!animating) return;

                float value = (float) animation.getAnimatedValue();

                // 🔥 CENTRALIZA ANTES DE ESCALAR
                textView.setPivotX(textView.getWidth() / 2f);
                textView.setPivotY(textView.getHeight() / 2f);

                textView.setScaleX(value);
                textView.setScaleY(value);
            });

            scaleAnimator.start();
        });


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

        if (scaleAnimator != null) scaleAnimator.cancel();
        if (colorAnimator != null) colorAnimator.cancel();

        textView.setScaleX(1f);
        textView.setScaleY(1f);
        textView.setTextColor(Color.parseColor("#00FF9C"));

        // 🔥 REMOVE HARDWARE LAYER
        textView.setLayerType(View.LAYER_TYPE_NONE, null);
    }
}
