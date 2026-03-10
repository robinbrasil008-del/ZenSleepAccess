package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AnimatedBorderDrawable extends Drawable {

    private final Paint fillPaint;
    private final Paint strokePaint;
    private final RectF rectF = new RectF();
    private final float cornerRadius;
    private final float strokeWidth;
    private final View hostView;

    private float rotation = 0f;
    private SweepGradient sweepGradient;
    private final Matrix gradientMatrix = new Matrix();
    private ValueAnimator animator;

    public AnimatedBorderDrawable(View hostView, float cornerRadius, float strokeWidth) {
        this.hostView = hostView;
        this.cornerRadius = cornerRadius;
        this.strokeWidth = strokeWidth;

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#1E2A3A"));

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        float cx = bounds.exactCenterX();
        float cy = bounds.exactCenterY();

        int[] colors = new int[]{
                Color.TRANSPARENT,
                Color.parseColor("#FFD400"),
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#7CFF00"),
                Color.TRANSPARENT
        };

        float[] positions = new float[]{
                0.00f,
                0.08f,
                0.14f,
                0.20f,
                0.28f
        };

        sweepGradient = new SweepGradient(cx, cy, colors, positions);
        strokePaint.setShader(sweepGradient);

        rectF.set(
                bounds.left + strokeWidth / 2f,
                bounds.top + strokeWidth / 2f,
                bounds.right - strokeWidth / 2f,
                bounds.bottom - strokeWidth / 2f
        );
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint);

        if (sweepGradient != null) {
            gradientMatrix.reset();
            gradientMatrix.setRotate(rotation, rectF.centerX(), rectF.centerY());
            sweepGradient.setLocalMatrix(gradientMatrix);
        }

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, strokePaint);
    }

    public void start() {
        stop();

        animator = ValueAnimator.ofFloat(0f, 360f);
        animator.setDuration(1600);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            rotation = (float) animation.getAnimatedValue();
            invalidateSelf();

            if (hostView != null) {
                hostView.postInvalidateOnAnimation();
            }
        });

        animator.start();
    }

    public void stop() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    @Override
    public void setAlpha(int alpha) {
        fillPaint.setAlpha(alpha);
        strokePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        fillPaint.setColorFilter(colorFilter);
        strokePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
