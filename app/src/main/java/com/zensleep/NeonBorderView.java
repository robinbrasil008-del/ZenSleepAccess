package com.zensleep;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class NeonBorderView extends View {

    private Paint borderPaint;
    private Paint glowPaint;
    private RectF rect;

    private float hue = 0;

    public NeonBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        setWillNotDraw(false);

        rect = new RectF();

        // BORDA COLORIDA (RGB)
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(dp(3));

        // GLOW EXTERNO (SUAVE)
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(dp(12));
        glowPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.NORMAL));

        // ANIMAÇÃO RGB
        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            hue = (float) animation.getAnimatedValue();
            invalidate();
        });

        animator.start();

        setLayerType(LAYER_TYPE_SOFTWARE, null); // necessário pro glow
    }

    @Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    rect.set(
            dp(6),
            dp(6),
            getWidth() - dp(6),
            getHeight() - dp(6)
    );

    float cx = getWidth() / 2f;
    float cy = getHeight() / 2f;

    // 🔥 GRADIENTE RGB (BORDA)
    int[] colors = new int[]{
            Color.parseColor("#8A2BE2"),
            Color.parseColor("#00FFFF"),
            Color.parseColor("#FF00FF"),
            Color.parseColor("#FFD700"),
            Color.parseColor("#8A2BE2")
    };

    SweepGradient sweep = new SweepGradient(cx, cy, colors, null);

    Matrix matrix = new Matrix();
    matrix.setRotate(hue, cx, cy);
    sweep.setLocalMatrix(matrix);

    borderPaint.setShader(sweep);
    glowPaint.setShader(sweep);

    // 🔥 GLOW EXTERNO FORTE (igual imagem)
    glowPaint.setStrokeWidth(dp(20));
    glowPaint.setAlpha(180);
    canvas.drawRoundRect(rect, dp(40), dp(40), glowPaint);

    // 🔥 BORDA PRINCIPAL
    borderPaint.setStrokeWidth(dp(3));
    canvas.drawRoundRect(rect, dp(40), dp(40), borderPaint);

    // 🔥 FUNDO ROXO (ESSENCIAL)
    Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    fill.setStyle(Paint.Style.FILL);

    LinearGradient bg = new LinearGradient(
            0, 0, getWidth(), getHeight(),
            new int[]{
                    Color.parseColor("#B066FF"),
                    Color.parseColor("#7A3FFF")
            },
            null,
            Shader.TileMode.CLAMP
    );

    fill.setShader(bg);

    // desenha fundo por baixo da borda
    canvas.drawRoundRect(
            rect.left + dp(4),
            rect.top + dp(4),
            rect.right - dp(4),
            rect.bottom - dp(4),
            dp(40),
            dp(40),
            fill
    );
}
    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
