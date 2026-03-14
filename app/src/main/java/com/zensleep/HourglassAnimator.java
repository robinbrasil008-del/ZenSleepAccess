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

        Icon.setAnimationFromUrl("https://assets2.lottiefiles.com/packages/lf20_jcikwtux.json");
        icon.setProgress(4f);
        icon.playAnimation();
    }

    public void stop() {
        if (icon == null) return;

        icon.cancelAnimation();
        icon.setProgress(0f);
    }
}
