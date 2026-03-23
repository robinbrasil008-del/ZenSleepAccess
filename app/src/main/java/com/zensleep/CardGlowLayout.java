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

        // 🔥 CONFIG PREMIUM DO GLOW
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(dp(5)); // mais grosso
        glowPaint.setColor(Color.parseColor("#00F0FF")); // neon forte
        glowPaint.setMaskFilter(new BlurMaskFilter(dp(28), BlurMaskFilter.Blur.NORMAL)); // glow pesado
    }

    public void startGlow() {
        glowing = true;

        if (glowAnimator != null) {
            glowAnimator.cancel();
        }

        // 🔥 EFEITO RESPIRANDO
        glowAnimator = ValueAnimator.ofFloat(80f, 255f);
        glowAnimator.setDuration(700);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.setRepeatMode(ValueAnimator.REVERSE);
        glowAnimator.setInterpolator(new LinearInterpolator());
        glowAnimator.addUpdateListener(animation -> {
            glowAlpha = (float) animation.getAnimatedValue();
            invalidate();
        });
        glowAnimator.start();

        // 🔥 LEVANTA O CARD (EFEITO PREMIUM)
        animate()
                .translationZ(dp(6))
                .setDuration(200)
                .start();

        invalidate();
    }

    public void stopGlow() {
        glowing = false;

        if (glowAnimator != null) {
            glowAnimator.cancel();
            glowAnimator = null;
        }

        glowAlpha = 0f;

        // 🔥 VOLTA AO NORMAL
        animate()
                .translationZ(0)
                .setDuration(200)
                .start();

        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (!glowing) return;

        rect.set(
                dp(4),
                dp(4),
                getWidth() - dp(4),
                getHeight() - dp(4)
        );

        glowPaint.setAlpha((int) glowAlpha);

        canvas.drawRoundRect(rect, dp(24), dp(24), glowPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
