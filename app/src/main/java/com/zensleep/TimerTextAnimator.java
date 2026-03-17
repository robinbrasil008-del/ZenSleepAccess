package com.zensleep;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class TimerTextAnimator {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean animating = false;
    private float scale = 1f;
    private boolean growing = true;

    private TextView target;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (!animating || target == null) return;

            // 💓 PULSO MAIS SUAVE (ANTES ERA 0.02 MUITO RÁPIDO)
            float speed = 0.008f;

            if (growing) {
                scale += speed;
                if (scale >= 1.15f) growing = false;
            } else {
                scale -= speed;
                if (scale <= 1f) growing = true;
            }

            target.setScaleX(scale);
            target.setScaleY(scale);

            // 💚 VERDE + GLOW DINÂMICO
            int color = Color.parseColor("#00FF9C");
            float glow = 15f + (scale - 1f) * 60f;

            target.setTextColor(color);
            target.setShadowLayer(glow, 0f, 0f, color);

            // ⏱ MAIS LENTO (ANTES 16ms = muito rápido)
            handler.postDelayed(this, 25);
        }
    };

    public void start(TextView textView) {

        stop(textView);

        target = textView;
        animating = true;

        handler.post(runnable);
    }

    public void stop(TextView textView) {

        animating = false;
        handler.removeCallbacks(runnable);

        if (textView != null) {
            textView.setScaleX(1f);
            textView.setScaleY(1f);

            textView.setTextColor(Color.parseColor("#00FF9C"));
            textView.setShadowLayer(30f, 0f, 0f, Color.parseColor("#00FF9C"));
        }

        target = null;
        scale = 1f;
        growing = true;
    }
}
