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

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.NORMAL));

        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(5000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            hue = (float) animation.getAnimatedValue();
            invalidate();
        });

        animator.start();

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    float padding = dp(6);
    rect.set(
            padding,
            padding,
            getWidth() - padding,
            getHeight() - padding
    );

    float radius = dp(40);

    // 🔒 CLIP PRA NÃO VAZAR (RESOLVE O QUADRADO)
    Path clipPath = new Path();
    clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
    canvas.save();
    canvas.clipPath(clipPath);

    float cx = getWidth() / 2f;
    float cy = getHeight() / 2f;

    // 🌈 GRADIENTE RGB
    int[] colors = new int[]{
            Color.parseColor("#A855F7"),
            Color.parseColor("#22D3EE"),
            Color.parseColor("#F472B6"),
            Color.parseColor("#FACC15"),
            Color.parseColor("#A855F7")
    };

    SweepGradient sweep = new SweepGradient(cx, cy, colors, null);

    Matrix matrix = new Matrix();
    matrix.setRotate(hue, cx, cy);
    sweep.setLocalMatrix(matrix);

    borderPaint.setShader(sweep);
    glowPaint.setShader(sweep);

    // 🔥 GLOW CONTROLADO (AGORA NÃO VAZA)
    glowPaint.setStrokeWidth(dp(16));
    glowPaint.setAlpha(100);
    canvas.drawRoundRect(rect, radius, radius, glowPaint);

    // 🔥 BORDA
    borderPaint.setStrokeWidth(dp(2));
    canvas.drawRoundRect(rect, radius, radius, borderPaint);

    canvas.restore();

    // 💎 FUNDO GLASS REAL (CAMADAS)
    Paint glass = new Paint(Paint.ANTI_ALIAS_FLAG);

    RadialGradient glassGradient = new RadialGradient(
        cx,
        cy,
        getWidth(),
        new int[]{
                Color.parseColor("#66B388FF"), // lilás claro (topo)
                Color.parseColor("#559333EA"), // roxo médio
                Color.parseColor("#332D1B69")  // roxo escuro (profundo)
        },
        new float[]{0f, 0.5f, 1f},
        Shader.TileMode.CLAMP
);

    glass.setShader(glassGradient);

    canvas.drawRoundRect(
            rect.left + dp(3),
            rect.top + dp(3),
            rect.right - dp(3),
            rect.bottom - dp(3),
            radius,
            radius,
            glass
    );

    // ✨ REFLEXO TOP (VIDRO REAL)
    Paint highlight = new Paint(Paint.ANTI_ALIAS_FLAG);

    LinearGradient shine = new LinearGradient(
            0,
            rect.top,
            0,
            rect.top + dp(30),
            new int[]{
                    Color.parseColor("#80FFFFFF"),
                    Color.TRANSPARENT
            },
            null,
            Shader.TileMode.CLAMP
    );

    highlight.setShader(shine);

    canvas.drawRoundRect(
            rect.left + dp(6),
            rect.top + dp(6),
            rect.right - dp(6),
            rect.top + dp(30),
            radius,
            radius,
            highlight
    );

    // ✨✨ BRILHOS (SPARKLES PREMIUM)

Paint sparkle = new Paint(Paint.ANTI_ALIAS_FLAG);

// brilho principal
sparkle.setColor(Color.parseColor("#CCFFFFFF"));
sparkle.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

// posição animada (usa o hue que já existe)
float x1 = rect.left + (getWidth() * ((hue % 100) / 100f));
float y1 = rect.top + dp(10);

canvas.drawCircle(x1, y1, dp(3), sparkle);

// brilho secundário
sparkle.setAlpha(180);
float x2 = rect.right - (getWidth() * ((hue % 150) / 150f));
float y2 = rect.top + dp(20);

canvas.drawCircle(x2, y2, dp(2), sparkle);

// micro brilho fixo
sparkle.setAlpha(120);
canvas.drawCircle(rect.left + dp(20), rect.top + dp(15), dp(1.5f), sparkle);
    
}
    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
