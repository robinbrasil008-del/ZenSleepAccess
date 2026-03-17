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
    glowPaint.setStrokeWidth(dp(22));
    glowPaint.setAlpha(200); // MAIS FORTE
    glowPaint.setMaskFilter(new BlurMaskFilter(35, BlurMaskFilter.Blur.NORMAL));
    canvas.drawRoundRect(rect, radius, radius, glowPaint);

    // 🔥 segunda camada de glow (mais intensa)
    glowPaint.setAlpha(120);
    glowPaint.setStrokeWidth(dp(30));
    canvas.drawRoundRect(rect, radius, radius, glowPaint);

    // 🔥 BORDA
    borderPaint.setShadowLayer(20, 0, 0, Color.parseColor("#A855F7"));
    canvas.drawRoundRect(rect, radius, radius, borderPaint);

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

    // ✨✨ BRILHO ESPALHADO (EFEITO PREMIUM REAL)

Paint sparkle = new Paint(Paint.ANTI_ALIAS_FLAG);
sparkle.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));

// leve cintilação animada
float pulse = (float) (Math.sin(Math.toRadians(hue * 2)) * 0.5f + 0.5f);

// 🔥 camada de brilho suave geral (ESSENCIAL)
RadialGradient glowSpread = new RadialGradient(
        rect.centerX(),
        rect.centerY(),
        getWidth() * 0.8f,
        new int[]{
                Color.parseColor("#33FFFFFF"),
                Color.parseColor("#22C084FC"),
                Color.TRANSPARENT
        },
        new float[]{0f, 0.6f, 1f},
        Shader.TileMode.CLAMP
);

sparkle.setShader(glowSpread);
canvas.drawRoundRect(rect, dp(40), dp(40), sparkle);

// ✨ partículas espalhadas (random fixo baseado no hue)

sparkle.setShader(null);
sparkle.setColor(Color.WHITE);

for (int i = 0; i < 12; i++) {

    float x = rect.left + (getWidth() * ((i * 37 + hue) % 100) / 100f);
    float y = rect.top + (getHeight() * ((i * 53 + hue) % 100) / 100f);

    float size = dp(1.5f + pulse * 2);

    sparkle.setAlpha(100 + (i * 10));
    canvas.drawCircle(x, y, size, sparkle);
}

    canvas.restore();
    
}
    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
