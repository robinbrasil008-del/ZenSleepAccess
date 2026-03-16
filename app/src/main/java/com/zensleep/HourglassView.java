package com.zensleep;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class HourglassView extends View {

    private Paint glassPaint;
    private Paint sandPaint;

    private float sandLevel = 1f;
    private float rotation = 0f;

    private ValueAnimator animator;

    public HourglassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

        glassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glassPaint.setStyle(Paint.Style.STROKE);
        glassPaint.setStrokeWidth(6);
        glassPaint.setColor(Color.WHITE);

        sandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sandPaint.setStyle(Paint.Style.FILL);
        sandPaint.setColor(Color.parseColor("#FFD400"));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int w = getWidth();
        int h = getHeight();

        canvas.save();
        canvas.rotate(rotation, w/2f, h/2f);

        Path glass = new Path();

        glass.moveTo(w*0.2f, h*0.1f);
        glass.lineTo(w*0.8f, h*0.1f);
        glass.lineTo(w*0.55f, h*0.45f);
        glass.lineTo(w*0.8f, h*0.9f);
        glass.lineTo(w*0.2f, h*0.9f);
        glass.lineTo(w*0.45f, h*0.45f);
        glass.close();

        canvas.drawPath(glass, glassPaint);

        float sandHeight = h * 0.35f * sandLevel;

        canvas.drawRect(
                w*0.32f,
                h*0.55f,
                w*0.68f,
                h*0.55f + sandHeight,
                sandPaint
        );

        canvas.restore();
    }

    public void start(){

        stop();

        animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(a -> {

            float p = (float)a.getAnimatedValue();

            sandLevel = 1f - p;
            rotation = p * 180f;

            invalidate();

        });

        animator.start();
    }

    public void stop(){

        if(animator != null){
            animator.cancel();
        }

        sandLevel = 1f;
        rotation = 0f;
        invalidate();
    }
}
