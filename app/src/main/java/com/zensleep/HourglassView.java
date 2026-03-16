package com.zensleep;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HourglassView extends View {

    private Paint glassPaint;
    private Paint sandPaint;

    private float rotation = 0f;
    private float sandLevel = 0f;

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

        glassPaint = new Paint();
        glassPaint.setColor(Color.WHITE);
        glassPaint.setStyle(Paint.Style.STROKE);
        glassPaint.setStrokeWidth(6f);
        glassPaint.setAntiAlias(true);

        sandPaint = new Paint();
        sandPaint.setColor(Color.YELLOW);
        sandPaint.setStyle(Paint.Style.FILL);
        sandPaint.setAntiAlias(true);

        animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        animator.addUpdateListener(animation -> {

            float value = (float) animation.getAnimatedValue();

            rotation = value * 360f;
            sandLevel = value;

            invalidate();

        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        canvas.save();
        canvas.rotate(rotation, w/2, h/2);

        // vidro superior
        canvas.drawLine(w*0.2f,h*0.1f,w*0.8f,h*0.1f,glassPaint);
        canvas.drawLine(w*0.2f,h*0.1f,w*0.5f,h*0.45f,glassPaint);
        canvas.drawLine(w*0.8f,h*0.1f,w*0.5f,h*0.45f,glassPaint);

        // vidro inferior
        canvas.drawLine(w*0.2f,h*0.9f,w*0.8f,h*0.9f,glassPaint);
        canvas.drawLine(w*0.2f,h*0.9f,w*0.5f,h*0.55f,glassPaint);
        canvas.drawLine(w*0.8f,h*0.9f,w*0.5f,h*0.55f,glassPaint);

        // areia descendo
        float sandY = h*0.55f + (h*0.3f * sandLevel);

        canvas.drawRect(
                w*0.45f,
                sandY,
                w*0.55f,
                h*0.9f,
                sandPaint
        );

        canvas.restore();
    }

    public void start(){
        if(!animator.isStarted()){
            animator.start();
        }
    }

    public void stop(){
        animator.cancel();
        rotation = 0f;
        sandLevel = 0f;
        invalidate();
    }
}
