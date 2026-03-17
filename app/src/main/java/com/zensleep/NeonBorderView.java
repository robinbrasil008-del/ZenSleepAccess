package com.zensleep;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class NeonBorderView extends View {

    private Paint paint;
    private RectF rect;
    private float hue = 0f;

    public NeonBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 🔥 NECESSÁRIO pro glow funcionar

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6f);

        rect = new RectF();

        startAnimation();
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 360f);
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);

        animator.addUpdateListener(animation -> {
            hue = (float) animation.getAnimatedValue();
            invalidate();
        });

        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rect.set(6, 6, getWidth() - 6, getHeight() - 6);

        int color = Color.HSVToColor(new float[]{hue, 1f, 1f});
        paint.setColor(color);

        // 🔥 glow real (borda neon)
        paint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));

        canvas.drawRoundRect(rect, 40f, 40f, paint);
    }
}
