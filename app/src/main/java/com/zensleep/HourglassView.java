package com.zensleep;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class HourglassView extends View {

    private final Paint framePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint flowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float phase = 0f;
    private int halfTurnCount = 0;
    private ValueAnimator animator;

    public HourglassView(Context context) {
        super(context);
        init();
    }

    public HourglassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HourglassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(dp(2.2f));
        framePaint.setColor(Color.parseColor("#F3F3F3"));
        framePaint.setStrokeCap(Paint.Cap.ROUND);
        framePaint.setStrokeJoin(Paint.Join.ROUND);

        glassPaint.setStyle(Paint.Style.FILL);
        glassPaint.setColor(Color.parseColor("#22FFFFFF"));

        sandPaint.setStyle(Paint.Style.FILL);
        sandPaint.setColor(Color.parseColor("#FFD54A"));

        flowPaint.setStyle(Paint.Style.STROKE);
        flowPaint.setStrokeWidth(dp(1.6f));
        flowPaint.setColor(Color.parseColor("#FFE082"));
        flowPaint.setStrokeCap(Paint.Cap.ROUND);

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1800);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);

        animator.addUpdateListener(a -> {
            phase = (float) a.getAnimatedValue();
            invalidate();
        });

        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {
                halfTurnCount++;
            }
        });
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        if (w <= 0 || h <= 0) return;

        float cx = w / 2f;
        float cy = h / 2f;

        float left = w * 0.18f;
        float right = w * 0.82f;
        float top = h * 0.10f;
        float bottom = h * 0.90f;
        float neckHalf = w * 0.06f;
        float midGap = h * 0.06f;

        float currentRotation = (halfTurnCount * 180f) + (phase * 180f);

        canvas.save();
        canvas.rotate(currentRotation, cx, cy);

        Path topGlass = new Path();
        topGlass.moveTo(left, top);
        topGlass.lineTo(right, top);
        topGlass.lineTo(cx + neckHalf, cy - midGap);
        topGlass.lineTo(cx - neckHalf, cy - midGap);
        topGlass.close();

        Path bottomGlass = new Path();
        bottomGlass.moveTo(cx - neckHalf, cy + midGap);
        bottomGlass.lineTo(cx + neckHalf, cy + midGap);
        bottomGlass.lineTo(right, bottom);
        bottomGlass.lineTo(left, bottom);
        bottomGlass.close();

        canvas.drawPath(topGlass, glassPaint);
        canvas.drawPath(bottomGlass, glassPaint);

        canvas.drawPath(topGlass, framePaint);
        canvas.drawPath(bottomGlass, framePaint);

        float topFill = 1f - phase;
        float bottomFill = phase;

        if (topFill > 0.02f) {
            float maxTopHeight = (cy - midGap) - top - dp(4);
            float currentTopHeight = maxTopHeight * topFill;

            Path topSand = new Path();
            topSand.moveTo(cx - (w * 0.17f * topFill), (cy - midGap) - currentTopHeight);
            topSand.lineTo(cx + (w * 0.17f * topFill), (cy - midGap) - currentTopHeight);
            topSand.lineTo(cx + neckHalf - dp(1), cy - midGap - dp(1));
            topSand.lineTo(cx - neckHalf + dp(1), cy - midGap - dp(1));
            topSand.close();

            canvas.drawPath(topSand, sandPaint);
        }

        if (phase > 0.04f && phase < 0.96f) {
            canvas.drawLine(cx, cy - midGap + dp(1), cx, cy + midGap - dp(1), flowPaint);
        }

        if (bottomFill > 0.02f) {
            float maxBottomHeight = bottom - (cy + midGap) - dp(4);
            float currentBottomHeight = maxBottomHeight * bottomFill;

            Path bottomSand = new Path();
            bottomSand.moveTo(cx - (w * 0.20f * bottomFill), bottom - dp(2));
            bottomSand.lineTo(cx + (w * 0.20f * bottomFill), bottom - dp(2));
            bottomSand.lineTo(cx, bottom - currentBottomHeight);
            bottomSand.close();

            canvas.drawPath(bottomSand, sandPaint);
        }

        canvas.restore();
    }

    public void start() {
        if (animator == null) return;
        if (!animator.isStarted()) {
            animator.start();
        } else if (animator.isPaused()) {
            animator.resume();
        }
    }

    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
        phase = 0f;
        halfTurnCount = 0;
        invalidate();
    }
}
