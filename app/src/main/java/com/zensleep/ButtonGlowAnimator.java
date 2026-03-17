package com.zensleep;

import android.view.View;

public class ButtonGlowAnimator {

    private NeonBorderView neonView;

    public void attach(View parent, NeonBorderView border) {
        this.neonView = border;

        if (neonView != null) {
            neonView.setVisibility(View.VISIBLE);
        }
    }

    public void stop() {
        if (neonView != null) {
            neonView.setVisibility(View.GONE);
        }
    }
}
