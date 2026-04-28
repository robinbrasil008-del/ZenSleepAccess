package com.zensleep;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class TutorialMaskView extends RelativeLayout {

    private Paint backgroundPaint;
    private Paint eraserPaint;
    private RectF targetRect;
    private float cornerRadius = 20f; // Arredondamento do furo

    public TutorialMaskView(Context context) {
        super(context);
        init();
    }

    public TutorialMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Isso é crucial: sem esta linha, o furo não vai funcionar
        setWillNotDraw(false); 
        
        // Configura a cor de fundo semi-transparente
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#99000000")); // Preto 60%
        backgroundPaint.setStyle(Paint.Style.FILL);

        // Configura o comando de "apagar" para criar o furo
        eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        
        targetRect = new RectF();
    }

    /**
     * Este é o comando mágico. Primeiro ele pinta a tela toda com o fundo semi-transparente.
     * Depois, ele desenha o furo com os cantos arredondados no local exato.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 1. Desenha o fundo semi-transparente sobre a tela inteira
        canvas.drawColor(Color.TRANSPARENT); // Começa transparente
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        // 2. Se houver um alvo definido, desenha o "furo" arredondado
        if (targetRect != null && targetRect.width() > 0) {
            canvas.drawRoundRect(targetRect, cornerRadius, cornerRadius, eraserPaint);
        }
        
        super.onDraw(canvas);
    }

    /**
     * Atualiza as coordenadas do furo e força o redesenho da tela.
     */
    public void setTarget(float x, float y, float width, float height) {
        // Adiciona um pequeno respiro para o furo não ficar colado
        targetRect.set(x, y, x + width, y + height);
        cornerRadius = width / 6; // Ajuste automático do arredondamento
        invalidate(); // Redesenha a tela agora!
    }
}

