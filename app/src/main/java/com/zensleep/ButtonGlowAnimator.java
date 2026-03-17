package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
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

        // 🔥 TEXTO GARANTIDO
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setTextSize(16);
        button.setGravity(Gravity.CENTER);
        button.setIncludeFontPadding(false);

        // 🔥 SOMBRA DO TEXTO
        button.setShadowLayer(
                14f,
                0f,
                0f,
                Color.parseColor("#A855F7")
        );

        // NÃO ESCALAR O BOTÃO
        button.setScaleX(1f);
        button.setScaleY(1f);
        button.setAlpha(1f);

        // 🔥 ANIMAÇÃO DE BRILHO REAL
        glowAnimator = ValueAnimator.ofFloat(0.55f, 1f);
        glowAnimator.setDuration(1400);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        glowAnimator.setInterpolator(new LinearInterpolator());

        glowAnimator.addUpdateListener(animation -> {
            if (!animating) return;

            float value = (float) animation.getAnimatedValue();

            // brilho externo
            glow.setAlpha((int) (255 * value));

            // borda pulsando suave
            int strokeAlpha = (int) (180 + (75 * value));
            String hexAlpha = Integer.toHexString(Math.min(255, strokeAlpha)).toUpperCase();
            if (hexAlpha.length() == 1) hexAlpha = "0" + hexAlpha;

            main.setStroke(
                    (int) dp(button, 2),
                    Color.parseColor("#" + hexAlpha + "E9D5FF")
            );

            // leve brilho geral sem deformar
            button.setAlpha(0.96f + (value * 0.04f));

            // sombra do texto pulsando junto
            button.setShadowLayer(
                    10f + (value * 10f),
                    0f,
                    0f,
                    Color.parseColor("#A855F7")
            );
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
            button.setAlpha(1f);
            button.setTextColor(Color.WHITE);
            button.setShadowLayer(
                    14f,
                    0f,
                    0f,
                    Color.parseColor("#A855F7")
            );
        }
    }

    private float dp(View view, float value) {
        return value * view.getResources().getDisplayMetrics().density;
    }
}
