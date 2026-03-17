package com.zensleep;

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

            if (growing) {
                scale += 0.02f;
                if (scale >= 1.2f) growing = false;
            } else {
                scale -= 0.02f;
                if (scale <= 1f) growing = true;
            }

            target.setScaleX(scale);
            target.setScaleY(scale);

            handler.postDelayed(this, 16);
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
            textView.setScaleX(2f);
            textView.setScaleY(2f);
        }

        target = null;
        scale = 1f;
        growing = true;
    }
}
