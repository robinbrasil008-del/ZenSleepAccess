package com.zensleep;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.BlurMaskFilter;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.view.animation.LinearInterpolator;

public class CardGlowLayout extends FrameLayout {

    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    private boolean glowing = false;
    private float glowAlpha = 0f;
    private ValueAnimator glowAnimator;

    public CardGlowLayout(Context context) {
        super(context);
        init();
    }

    public CardGlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardGlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(dp(3));
        glowPaint.setColor(Color.parseColor("#AA00E5FF"));
        glowPaint.setMaskFilter(new BlurMaskFilter(dp(18), BlurMaskFilter.Blur.NORMAL));
    }

    public void startGlow() {
        glowing = true;

        if (glowAnimator != null) {
            glowAnimator.cancel();
        }

        glowAnimator = ValueAnimator.ofFloat(120f, 255f);
        glowAnimator.setDuration(900);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        glowAnimator.setInterpolator(new LinearInterpolator());
        glowAnimator.addUpdateListener(animation -> {
            glowAlpha = (float) animation.getAnimatedValue();
            invalidate();
        });
        glowAnimator.start();

        invalidate();
    }

    public void stopGlow() {
        glowing = false;

        if (glowAnimator != null) {
            glowAnimator.cancel();
            glowAnimator = null;
        }

        glowAlpha = 0f;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!glowing) return;

        rect.set(
                dp(3),
                dp(3),
                getWidth() - dp(3),
                getHeight() - dp(3)
        );

        glowPaint.setAlpha((int) glowAlpha);

        canvas.drawRoundRect(rect, dp(24), dp(24), glowPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
