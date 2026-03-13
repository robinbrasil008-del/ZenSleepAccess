package com.zensleep;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class HourglassAnimator {

    private final LottieAnimationView icon;

    public HourglassAnimator(LottieAnimationView icon) {
        this.icon = icon;

        icon.setRepeatCount(LottieDrawable.INFINITE);
        icon.setSpeed(1f);
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
