package com.zensleep;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.graphics.SweepGradient;
import android.graphics.Matrix;

public class AnimatedTimerCardLayout extends LinearLayout {

    private Paint fillPaint;
    private Paint borderPaint;
    private Paint movingPaint;
    private SweepGradient gradient;
    private Matrix gradientMatrix = new Matrix();

    private RectF rectF = new RectF();
    private Path borderPath = new Path();

    private float cornerRadius;
    private float strokeWidth;

    private float pathLength;
    private float phase = 0f;

    private boolean animating = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable animatorRunnable = new Runnable() {
        @Override
        public void run() {

            if (!animating) return;

            phase -= 10;

            invalidate();

            handler.postDelayed(this, 16);
        }
    };

    public AnimatedTimerCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        setWillNotDraw(false);

        float density = getResources().getDisplayMetrics().density;

        cornerRadius = 32f * density;
        strokeWidth = 4f * density;

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(Color.parseColor("#1E2A3A"));
        fillPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStrokeWidth(strokeWidth);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#2A3445"));

        movingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        movingPaint.setStrokeWidth(strokeWidth);
        movingPaint.setStyle(Paint.Style.STROKE);
        movingPaint.setColor(Color.parseColor("#FFD400"));
        movingPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        rectF.set(
                strokeWidth / 2f,
                strokeWidth / 2f,
                w - strokeWidth / 2f,
                h - strokeWidth / 2f
        );

        borderPath.reset();
        borderPath.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW);

        PathMeasure measure = new PathMeasure(borderPath, true);
        pathLength = measure.getLength();

        gradient = new SweepGradient(
        rectF.centerX(),
        rectF.centerY(),
        new int[]{
                Color.parseColor("#FFD400"),
                Color.parseColor("#FF6B00"),
                Color.parseColor("#FF00E1"),
                Color.parseColor("#7CFF00"),
                Color.parseColor("#00E5FF"),
                Color.parseColor("#FFD400")
        },
        null
);

movingPaint.setShader(gradient);
    }

    @Override
protected void onDraw(Canvas canvas) {

    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, fillPaint);

    canvas.drawPath(borderPath, borderPaint);

    gradientMatrix.setRotate(-phase, rectF.centerX(), rectF.centerY());
    gradient.setLocalMatrix(gradientMatrix);

    if (animating) {

        float segment = pathLength * 0.15f;

        movingPaint.setPathEffect(
                new DashPathEffect(
                        new float[]{segment, pathLength},
                        phase
                )
        );

        canvas.drawPath(borderPath, movingPaint);

    } else {

        movingPaint.setPathEffect(null);
        movingPaint.setColor(Color.parseColor("#FFD400"));

        canvas.drawPath(borderPath, movingPaint);
    }

    super.onDraw(canvas);
}

    public void startBorderAnimation() {

        animating = true;

        handler.post(animatorRunnable);
    }

    public void stopBorderAnimation() {

    animating = false;

    handler.removeCallbacks(animatorRunnable);

    phase = 0;

    movingPaint.setPathEffect(null);

    invalidate();
    }
}
