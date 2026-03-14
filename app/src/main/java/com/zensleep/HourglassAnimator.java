package com.zensleep;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class HourglassAnimator {

    private LottieAnimationView icon;

    public HourglassAnimator(LottieAnimationView icon) {
        this.icon = icon;

        if (this.icon != null) {
            this.icon.setRepeatCount(LottieDrawable.INFINITE);
            this.icon.setSpeed(1f);
        }
    }

    public void start() {
        if (icon == null) return;

        icon.cancelAnimation();
        icon.setProgress(0f);
        icon.playAnimation();
    }

    public void stop() {
        if (icon == null) return;

        icon.cancelAnimation();
        icon.setProgress(0f);
    }
}
