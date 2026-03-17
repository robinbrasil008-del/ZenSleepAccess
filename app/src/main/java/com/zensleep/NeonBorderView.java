package com.zensleep;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class NeonBorderView extends View {

    private Paint borderPaint;
    private Paint glowPaint;
    private RectF rect;

    private float hue = 0;

    public NeonBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        setWillNotDraw(false);

        rect = new RectF();

        // BORDA COLORIDA (RGB)
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(dp(3));

        // GLOW EXTERNO (SUAVE)
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(dp(12));
        glowPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));

        // ANIMAÇÃO RGB
        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            hue = (float) animation.getAnimatedValue();
            invalidate();
        });

        animator.start();

        setLayerType(LAYER_TYPE_SOFTWARE, null); // necessário pro glow
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rect.set(
                dp(6),
                dp(6),
                getWidth() - dp(6),
                getHeight() - dp(6)
        );

        int color = Color.HSVToColor(new float[]{hue, 1f, 1f});

        // GLOW
        glowPaint.setColor(color);
        canvas.drawRoundRect(rect, dp(40), dp(40), glowPaint);

        // BORDA
        borderPaint.setColor(color);
        canvas.drawRoundRect(rect, dp(40), dp(40), borderPaint);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
