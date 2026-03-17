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

    rect.set(
            dp(6),
            dp(6),
            getWidth() - dp(6),
            getHeight() - dp(6)
    );

    float cx = getWidth() / 2f;
    float cy = getHeight() / 2f;

    // 🌈 GRADIENTE RGB PREMIUM
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

    // 🔥 GLOW EXTERNO SUAVE (mais natural)
    glowPaint.setStrokeWidth(dp(18));
    glowPaint.setAlpha(90);
    canvas.drawRoundRect(rect, dp(40), dp(40), glowPaint);

    // 🔥 BORDA EXTERNA
    borderPaint.setStrokeWidth(dp(2));
    canvas.drawRoundRect(rect, dp(40), dp(40), borderPaint);

    // 💜 FUNDO COM GRADIENTE + LUZ
    Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);

    LinearGradient bg = new LinearGradient(
            0, 0, getWidth(), getHeight(),
            new int[]{
                    Color.parseColor("#C084FC"),
                    Color.parseColor("#9333EA"),
                    Color.parseColor("#6B21A8")
            },
            new float[]{0f, 0.5f, 1f},
            Shader.TileMode.CLAMP
    );

    fill.setShader(bg);

    canvas.drawRoundRect(
            rect.left + dp(3),
            rect.top + dp(3),
            rect.right - dp(3),
            rect.bottom - dp(3),
            dp(40),
            dp(40),
            fill
    );

    // ✨ BORDA INTERNA (SEGREDO DO DESIGN)
    Paint innerStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    innerStroke.setStyle(Paint.Style.STROKE);
    innerStroke.setStrokeWidth(dp(1.5f));
    innerStroke.setColor(Color.parseColor("#80FFFFFF"));

    canvas.drawRoundRect(
            rect.left + dp(6),
            rect.top + dp(6),
            rect.right - dp(6),
            rect.bottom - dp(6),
            dp(40),
            dp(40),
            innerStroke
    );

    // ✨ REFLEXO SUPERIOR SUAVE
    Paint highlight = new Paint(Paint.ANTI_ALIAS_FLAG);

    LinearGradient shine = new LinearGradient(
            0, rect.top,
            0, rect.top + dp(30),
            new int[]{
                    Color.parseColor("#66FFFFFF"),
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
            dp(40),
            dp(40),
            highlight
    );
}

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
