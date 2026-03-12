package com.zensleep;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class AnimatedTimerCardLayout extends LinearLayout {

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint baseBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint movingBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF rectF = new RectF();
    private final Path borderPath = new Path();

    private float cornerRadiusPx;
    private float strokeWidthPx;
    private float pathLength = 0f;
    private float phase = 0f;

    private boolean borderAnimating = false;
    private ValueAnimator animator;

    public AnimatedTimerCardLayout(Context context) {
        super(context);
        init();
    }

    public AnimatedTimerCardLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedTimerCardLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        float density = getResources().getDisplayMetrics().density;
        cornerRadiusPx = 32f * density;
        strokeWidthPx = 4f * density;

        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#1E2A3A"));

        baseBorderPaint.setStyle(Paint.Style.STROKE);
        baseBorderPaint.setStrokeWidth(strokeWidthPx);
        baseBorderPaint.setColor(Color.parseColor("#2A3445"));

        movingBorderPaint.setStyle(Paint.Style.STROKE);
        movingBorderPaint.setStrokeWidth(strokeWidthPx);
        movingBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        movingBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        movingBorderPaint.setColor(Color.parseColor("#FFD400"));

        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        rectF.set(
                strokeWidthPx / 2f,
                strokeWidthPx / 2f,
                w - strokeWidthPx / 2f,
                h - strokeWidthPx / 2f
        );

        borderPath.reset();
        borderPath.addRoundRect(rectF, cornerRadiusPx, cornerRadiusPx, Path.Direction.CW);

        PathMeasure measure = new PathMeasure(borderPath, true);
        pathLength = measure.getLength();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(rectF, cornerRadiusPx, cornerRadiusPx, fillPaint);

        if (borderAnimating) {
            canvas.drawPath(borderPath, baseBorderPaint);

            float segment = pathLength * 0.18f;
            movingBorderPaint.setPathEffect(
                    new DashPathEffect(
                            new float[]{segment, pathLength},
                            phase
                    )
            );
            canvas.drawPath(borderPath, movingBorderPaint);
        } else {
            movingBorderPaint.setPathEffect(null);
            movingBorderPaint.setColor(Color.parseColor("#FFD400"));
            canvas.drawPath(borderPath, movingBorderPaint);
        }

        super.onDraw(canvas);
    }

    public void startBorderAnimation() {
        stopBorderAnimation();

        borderAnimating = true;

        animator = ValueAnimator.ofFloat(0f, pathLength);
        animator.setDuration(1400);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            phase = -(float) animation.getAnimatedValue();
            postInvalidateOnAnimation();
        });

        animator.start();
    }

    public void stopBorderAnimation() {
        borderAnimating = false;

        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        phase = 0f;
        postInvalidateOnAnimation();
    }
}
