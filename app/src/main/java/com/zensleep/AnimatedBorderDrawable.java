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

    private Paint fillPaint;
    private Paint strokePaint;

    private RectF rect = new RectF();

    private float cornerRadius;
    private float strokeWidth;

    private SweepGradient gradient;
    private Matrix matrix = new Matrix();

    private float rotation = 0f;

    private ValueAnimator animator;

    private View hostView;

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

        rect.set(
                bounds.left + strokeWidth / 2f,
                bounds.top + strokeWidth / 2f,
                bounds.right - strokeWidth / 2f,
                bounds.bottom - strokeWidth / 2f
        );

        float cx = rect.centerX();
        float cy = rect.centerY();

        int[] colors = new int[]{
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.parseColor("#FFD400"),
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#7CFF00"),
                Color.TRANSPARENT,
                Color.TRANSPARENT
        };

        float[] positions = new float[]{
                0.0f,
                0.45f,
                0.49f,
                0.50f,
                0.51f,
                0.55f,
                1.0f
        };

        gradient = new SweepGradient(cx, cy, colors, positions);

        strokePaint.setShader(gradient);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fillPaint);

        if (gradient != null) {
            matrix.setRotate(rotation, rect.centerX(), rect.centerY());
            gradient.setLocalMatrix(matrix);
        }

        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint);
    }

    public void start() {

        stop();

        animator = ValueAnimator.ofFloat(0f, 360f);
        animator.setDuration(1200);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {

            rotation = (float) animation.getAnimatedValue();

            invalidateSelf();

            if (hostView != null) {
                hostView.invalidate();
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
