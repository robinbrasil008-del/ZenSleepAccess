package com.zensleep;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class TimerIconAnimator {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean animating = false;
    private float rotation = 0f;

    private View targetView;

    private final Runnable animatorRunnable = new Runnable() {
        @Override
        public void run() {
            if (!animating || targetView == null) return;

            rotation += 2f;

            if (rotation >= 360f) {
                rotation = 0f;
            }

            targetView.setRotation(rotation);

            handler.postDelayed(this, 16);
        }
    };

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
        }

        targetView = null;
        rotation = 0f;
    }
}
