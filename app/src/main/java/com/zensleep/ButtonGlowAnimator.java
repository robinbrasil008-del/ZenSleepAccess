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

        // ===== ESTILO BASE DO BOTÃO =====
        GradientDrawable glowDrawable = new GradientDrawable();
        glowDrawable.setShape(GradientDrawable.RECTANGLE);
        glowDrawable.setCornerRadius(dp(button, 32));
        glowDrawable.setColor(Color.parseColor("#33FFFFFF")); // glow suave externo

        GradientDrawable mainDrawable = new GradientDrawable();
        mainDrawable.setShape(GradientDrawable.RECTANGLE);
        mainDrawable.setCornerRadius(dp(button, 32));
        mainDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        mainDrawable.setColors(new int[]{
                Color.parseColor("#B57CFF"),
                Color.parseColor("#A56EF0")
        });
        mainDrawable.setStroke((int) dp(button, 2), Color.parseColor("#CCF4E8FF"));

        LayerDrawable layerDrawable = new LayerDrawable(new GradientDrawable[]{
                glowDrawable,
                mainDrawable
        });
        layerDrawable.setLayerInset(1, 3, 3, 3, 3);

        button.setBackground(layerDrawable);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setAlpha(1f);
        button.setScaleX(1f);
        button.setScaleY(1f);

        // ===== ANIMAÇÃO =====
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1400);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            if (!animating) return;

            float value = (float) animation.getAnimatedValue();

            // brilho suave
            float alpha = 0.90f + (value * 0.10f);
            button.setAlpha(alpha);

            // pulso leve
            float scale = 1f + (value * 0.03f);
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
            button.setAlpha(1f);
            button.setScaleX(1f);
            button.setScaleY(1f);
        }
    }

    private float dp(View view, float value) {
        return value * view.getResources().getDisplayMetrics().density;
    }
}
