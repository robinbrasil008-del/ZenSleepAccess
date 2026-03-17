package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

public class ButtonGlowAnimator {

    private ValueAnimator glowAnimator;
    private boolean animating = false;

    public void start(Button button) {
        stop(button);

        if (button == null) return;

        animating = true;

        // 🔥 CAMADA DE BRILHO (EXTERNA)
        GradientDrawable glow = new GradientDrawable();
        glow.setShape(GradientDrawable.RECTANGLE);
        glow.setCornerRadius(dp(button, 40));
        glow.setColor(Color.parseColor("#66BB86FC")); // glow roxo suave

        // 🔥 BOTÃO PRINCIPAL
        GradientDrawable main = new GradientDrawable();
        main.setShape(GradientDrawable.RECTANGLE);
        main.setCornerRadius(dp(button, 40));
        main.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        main.setColors(new int[]{
                Color.parseColor("#C084FC"),
                Color.parseColor("#A855F7")
        });

        // 🔥 BORDA DESTACADA
        main.setStroke((int) dp(button, 2), Color.parseColor("#E9D5FF"));

        // 🔥 JUNTA AS CAMADAS
        LayerDrawable layer = new LayerDrawable(new GradientDrawable[]{glow, main});
        layer.setLayerInset(1, 6, 6, 6, 6);

        button.setBackground(layer);

        // 🔥 TEXTO VISÍVEL
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setTextSize(16);

        // 🔥 SOMBRA DO TEXTO (IMPORTANTE PRA APARECER)
        button.setShadowLayer(
                12f,
                0f,
                0f,
                Color.parseColor("#A855F7")
        );

        // 🔥 ANIMAÇÃO DE BRILHO REAL
        glowAnimator = ValueAnimator.ofFloat(0.6f, 1f);
        glowAnimator.setDuration(1200);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        glowAnimator.setInterpolator(new LinearInterpolator());

        glowAnimator.addUpdateListener(animation -> {
            if (!animating) return;

            float value = (float) animation.getAnimatedValue();

            // brilho externo
            glow.setAlpha((int) (255 * value));

            // leve pulso
            float scale = 1f + (value * 0.04f);
            button.setScaleX(scale);
            button.setScaleY(scale);
        });

        glowAnimator.start();
    }

    public void stop(Button button) {
        animating = false;

        if (glowAnimator != null) {
            glowAnimator.cancel();
            glowAnimator = null;
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
