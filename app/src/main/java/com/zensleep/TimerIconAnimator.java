package com.zensleep;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class TimerIconAnimator {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean animating = false;
    private float angle = -12f;
    private boolean goingRight = true;
    private float scale = 1.0f;
    private boolean scaleUp = true;

    private final Runnable animatorRunnable = new Runnable() {
        @Override
        public void run() {
            if (!animating || targetView == null) return;

            // balanço
            if (goingRight) {
                angle += 2f;
                if (angle >= 12f) goingRight = false;
            } else {
                angle -= 2f;
                if (angle <= -12f) goingRight = true;
            }

            // respiração
            if (scaleUp) {
                scale += 0.01f;
                if (scale >= 1.08f) scaleUp = false;
            } else {
                scale -= 0.01f;
                if (scale <= 0.96f) scaleUp = true;
            }

            targetView.setRotation(angle);
            targetView.setScaleX(scale);
            targetView.setScaleY(scale);

            handler.postDelayed(this, 16);
        }
    };

    private View targetView;

    public void start(View iconView) {
        stop();

        targetView = iconView;
        animating = true;

        targetView.setPivotX(targetView.getWidth() / 2f);
        targetView.setPivotY(targetView.getHeight() / 2f);

        handler.post(animatorRunnable);
    }

    public void stop() {
        animating = false;
        handler.removeCallbacks(animatorRunnable);

        if (targetView != null) {
            targetView.setRotation(0f);
            targetView.setScaleX(1f);
            targetView.setScaleY(1f);
        }

        targetView = null;
        angle = -12f;
        goingRight = true;
        scale = 1.0f;
        scaleUp = true;
    }
}
