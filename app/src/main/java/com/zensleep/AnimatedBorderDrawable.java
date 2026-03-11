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

    private SweepGradient gradient;
    private final Matrix matrix = new Matrix();

    private float angle = 0f;

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

        int[] colors = {
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.parseColor("#FFD400"),
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#7CFF00"),
                Color.TRANSPARENT,
                Color.TRANSPARENT
        };

        float[] pos = {
                0f,
                0.45f,
                0.48f,
                0.50f,
                0.52f,
                0.55f,
                1f
        };

        gradient = new SweepGradient(cx, cy, colors, pos);
        strokePaint.setShader(gradient);

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

        if (gradient != null) {
            matrix.reset();
            matrix.setRotate(angle, rectF.centerX(), rectF.centerY());
            gradient.setLocalMatrix(matrix);
        }

        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, strokePaint);
    }

    public void start() {

        stop();

        animator = ValueAnimator.ofFloat(0f, 360f);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {

            angle = (float) animation.getAnimatedValue();

            invalidateSelf();          // redesenha o drawable
            hostView.invalidate();     // força a view a redesenhar

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
