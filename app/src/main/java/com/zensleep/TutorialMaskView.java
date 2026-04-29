package com.zensleep;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class TutorialMaskView extends RelativeLayout {

    private Paint backgroundPaint;
    private Path maskPath;
    private RectF targetRect;
    private float cornerRadius = 30f;

    public TutorialMaskView(Context context) {
        super(context);
        init();
    }

    public TutorialMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Agora NÃO precisamos mais desativar a aceleração de hardware!
        // O app vai rodar mais liso.
        setWillNotDraw(false);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Cor do fundo (Preto com 65% de transparência para um visual elegante)
        backgroundPaint.setColor(Color.parseColor("#A6000000"));
        backgroundPaint.setStyle(Paint.Style.FILL);

        maskPath = new Path();
        targetRect = new RectF();
    }

    /**
     * Técnica de Desenho Inverso:
     * Nós desenhamos um retângulo na tela toda e um "buraco" no meio.
     * O modo EVEN_ODD diz ao Android para pintar apenas o que NÃO está sobreposto.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (targetRect.width() > 0) {
            maskPath.reset();
            // 1. Define o modo de preenchimento inverso
            maskPath.setFillType(Path.FillType.EVEN_ODD);
            
            // 2. Adiciona o retângulo da tela inteira
            maskPath.addRect(0, 0, getWidth(), getHeight(), Path.Direction.CW);
            
            // 3. Adiciona o retângulo do furo (arredondado)
            maskPath.addRoundRect(targetRect, cornerRadius, cornerRadius, Path.Direction.CW);
            
            // 4. Desenha o resultado (A tela escura com o furo perfeito)
            canvas.drawPath(maskPath, backgroundPaint);
        } else {
            // Se não tiver alvo, pinta a tela toda normalmente
            canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        }

        super.onDraw(canvas);
    }

    /**
     * Atualiza a posição do furo e redesenha a tela instantaneamente.
     */
    public void setTarget(float x, float y, float width, float height) {
        targetRect.set(x, y, x + width, y + height);
        
        // Arredondamento inteligente: cantos mais suaves para itens maiores
        cornerRadius = width / 5f; 
        if (cornerRadius > 40) cornerRadius = 40; // Limite máximo de arredondamento
        
        invalidate(); // Força o Android a chamar o onDraw de novo
    }
}
