package com.zensleep;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AnimatedBorderDrawable extends Drawable {

    private final View hostView;

    private final float cornerRadius;
    private final float strokeWidth;

    private final Paint fillPaint;
    private final Paint baseBorderPaint;
    private final Paint movingBorderPaint;

    private final RectF rectF = new RectF();
    private final Path borderPath = new Path();

    private float pathLength = 0f;
    private float phase = 0f;

    private ValueAnimator animator;

    public AnimatedBorderDrawable(View hostView, float cornerRadius, float strokeWidth) {
        this.hostView = hostView;
        this.cornerRadius = cornerRadius;
        this.strokeWidth = strokeWidth;

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#1E2A3A"));

        baseBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseBorderPaint.setStyle(Paint.Style.STROKE);
        baseBorderPaint.setStrokeWidth(strokeWidth);
        baseBorderPaint.setColor(Color.parseColor("#2A3445"));

        movingBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        movingBorderPaint.setStyle(Paint.Style.STROKE);
        movingBorderPaint.setStrokeWidth(strokeWidth);
        movingBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        movingBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        movingBorderPaint.setColor(Color.parseColor("#FFD400"));
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        rectF.set(
                bounds.left + strokeWidth / 2f,
                bounds.top + strokeWidth / 2f,
                bounds.right - strokeWidth / 2f,
                bounds.bottom - strokeWidth / 2f
        );

        borderPath.reset();
        borderPath.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW);

        PathMeasure measure = new PathMeasure(borderPath, true);
        pathLength = measure.getLength();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint);

        // borda base escura
        canvas.drawPath(borderPath, baseBorderPaint);

        // segmento de luz correndo na borda
        if (pathLength > 0f) {
            float segment = pathLength * 0.16f; // tamanho da luz
            movingBorderPaint.setPathEffect(
                    new DashPathEffect(
                            new float[]{segment, pathLength},
                            phase
                    )
            );
            canvas.drawPath(borderPath, movingBorderPaint);
        }
    }

    public void start() {
        stop();

        animator = ValueAnimator.ofFloat(0f, pathLength);
        animator.setDuration(1400);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            phase = -(float) animation.getAnimatedValue();
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
        movingBorderPaint.setPathEffect(null);
    }

    @Override
    public void setAlpha(int alpha) {
        fillPaint.setAlpha(alpha);
        baseBorderPaint.setAlpha(alpha);
        movingBorderPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        fillPaint.setColorFilter(colorFilter);
        baseBorderPaint.setColorFilter(colorFilter);
        movingBorderPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
