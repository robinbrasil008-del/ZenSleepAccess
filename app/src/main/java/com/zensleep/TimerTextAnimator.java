package com.zensleep;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class TimerTextAnimator {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean animating = false;
    private TextView target;

    private final Runnable pulseRunnable = new Runnable() {
        @Override
        public void run() {

            if (!animating || target == null) return;

            // 💥 IMPACTO (cresce rápido)
            target.animate()
                    .scaleX(1.25f)
                    .scaleY(1.25f)
                    .setDuration(120)
                    .withEndAction(() -> {

                        // 🌊 RELAXAMENTO (volta suave)
                        target.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(600)
                                .start();
                    })
                    .start();

            // 💚 COR + GLOW
            int color = Color.parseColor("#00FF9C");

            target.setTextColor(color);
            target.setShadowLayer(40f, 0f, 0f, color);

            // ⏱ REPETE A CADA 1 SEGUNDO
            handler.postDelayed(this, 1000);
        }
    };

    public void start(TextView textView) {

        stop(textView);

        target = textView;
        animating = true;

        handler.post(pulseRunnable);
    }

    public void stop(TextView textView) {

        animating = false;
        handler.removeCallbacks(pulseRunnable);

        if (textView != null) {
            textView.setScaleX(1f);
            textView.setScaleY(1f);

            textView.setTextColor(Color.parseColor("#AB47BC"));
            textView.setShadowLayer(30f, 0f, 0f, Color.parseColor("#AB47BC"));
        }

        target = null;
    }
}
