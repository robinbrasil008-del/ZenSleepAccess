package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

public class ButtonGlowAnimator {

    private ValueAnimator animator;
    private boolean animating = false;

    public void start(Button button) {
        stop(button);

        if (button == null) return;

        animating = true;

        // 🔥 FUNDO PRINCIPAL (ROXO)
        GradientDrawable main = new GradientDrawable();
        main.setShape(GradientDrawable.RECTANGLE);
        main.setCornerRadius(dp(button, 30));
        main.setColors(new int[]{
                Color.parseColor("#B57CFF"),
                Color.parseColor("#9F6BFF")
        });

        // 🔥 BORDA BRILHANTE
        main.setStroke((int) dp(button, 2), Color.parseColor("#E0C3FF"));

        // 🔥 GLOW EXTERNO (MAIS SUAVE)
        GradientDrawable glow = new GradientDrawable();
        glow.setShape(GradientDrawable.RECTANGLE);
        glow.setCornerRadius(dp(button, 34));
        glow.setColor(Color.parseColor("#33A855F7")); // bem leve

        // 🔥 ORDEM CORRETA (GLOW ATRÁS!)
        LayerDrawable layer = new LayerDrawable(new GradientDrawable[]{
                glow,  // fundo
                main   // frente
        });

        // 🔥 INSET PEQUENO PRA NÃO ENGOLIR TEXTO
        layer.setLayerInset(1, 4, 4, 4, 4);

        button.setBackground(layer);

        // 🔥 GARANTE TEXTO VISÍVEL
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setIncludeFontPadding(false);

        // 🔥 SOMBRA NO TEXTO (FAZ APARECER)
        button.setShadowLayer(
                8f,
                0f,
                0f,
                Color.parseColor("#A855F7")
        );

        // 🔥 ANIMAÇÃO SUAVE
        animator = ValueAnimator.ofFloat(0.7f, 1f);
        animator.setDuration(1400);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            if (!animating) return;

            float value = (float) animation.getAnimatedValue();

            // brilho leve
            glow.setAlpha((int) (255 * value));

            // pulso leve (SEM estourar layout)
            float scale = 1f + (value * 0.02f);
            button.setScaleX(scale);
            button.setScaleY(scale);
        });

        animator.start();
    }

    public void stop(Button button) {
        animating = false;

        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        if (button != null) {
            button.setScaleX(1f);
            button.setScaleY(1f);
        }
    }

    private float dp(View view, float value) {
        return value * view.getResources().getDisplayMetrics().density;
    }
}
