package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

public class TutorialHelper {

    private Context context;
    private View rootView;
    private RelativeLayout tutorialOverlay;
    private View highlightFrame;
    private LinearLayout tutorialBox;
    private TextView tutorialText;
    private Button btnProximo;
    
    // Variáveis para a seta
    private ImageView tutorialArrow;
    private ObjectAnimator arrowAnimator;
    
    private int tutorialStep = 0;

    public TutorialHelper(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        this.tutorialOverlay = rootView.findViewById(R.id.tutorialOverlay);
        this.highlightFrame = rootView.findViewById(R.id.highlightFrame);
        this.tutorialBox = rootView.findViewById(R.id.tutorialBox);
        this.tutorialText = rootView.findViewById(R.id.tutorialText);
        this.btnProximo = rootView.findViewById(R.id.btnProximo);
        
        // Mapeia a seta do XML
        this.tutorialArrow = rootView.findViewById(R.id.tutorialArrow);
    }

    public void iniciarSeNecessario() {
        SharedPreferences prefs = context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE);
        if (prefs.getBoolean("tutorial_visto", false) || tutorialOverlay == null) return;

        tutorialOverlay.setVisibility(View.VISIBLE);
        tutorialOverlay.setAlpha(0f);
        tutorialOverlay.animate().alpha(1f).setDuration(500).start();

        btnProximo.setOnClickListener(v -> avancar());
        configurarEtapa(0);
    }

    private void avancar() {
        tutorialStep++;
        if (tutorialStep > 3) {
            finalizar();
        } else {
            configurarEtapa(tutorialStep);
        }
    }

    private void configurarEtapa(int step) {
        tutorialBox.animate().alpha(0f).setDuration(200).withEndAction(() -> {
            switch (step) {
                case 0:
                    tutorialText.setText("Toque em um card para dar o play no som da natureza.");
                    
                    // O "furo" roxo continua pegando o card inteiro
                    View cardChuva = rootView.findViewById(R.id.cardChuva);
                    focar(cardChuva); 
                    
                    // 🔥 AGORA SIM: A seta vai apontar DIRETAMENTE para o botão Play!
                    View btnPlay = rootView.findViewById(R.id.btnPlayChuva);
                    posicionarSeta(btnPlay);
                    break;
                    
                case 1:
                    esconderSeta(); // Some com a seta a partir daqui
                    tutorialText.setText("Regule o volume aqui para criar o ambiente perfeito.");
                    
                    View vol = rootView.findViewById(R.id.seekChuva);
                    if (vol != null) {
                        vol.setVisibility(View.VISIBLE);
                        vol.setAlpha(1f);
                    }
                    focar(vol);
                    break;
                    
                case 2:
                    esconderSeta();
                    tutorialText.setText("Você pode tocar vários sons ao mesmo tempo! Misture como preferir para relaxar.");
                    
                    View volAnterior = rootView.findViewById(R.id.seekChuva);
                    if (volAnterior != null) volAnterior.setVisibility(View.GONE);

                    View lockFloresta = rootView.findViewById(R.id.lockOverlayFloresta);
                    if (lockFloresta != null) lockFloresta.setVisibility(View.INVISIBLE);

                    View card1 = rootView.findViewById(R.id.cardChuva);
                    View card2 = rootView.findViewById(R.id.cardFloresta);
                    
                    focar(card1, card2);
                    break;
                    
                case 3:
                    esconderSeta();
                    tutorialText.setText("Os sons com o cadeado são PREMIUM. Assista um anúncio rápido e desbloqueie!");
                    
                    View lock = rootView.findViewById(R.id.lockOverlayFloresta);
                    if (lock != null) {
                        lock.setVisibility(View.VISIBLE);
                        lock.setBackgroundResource(0); 
                    }
                    focar(lock);
                    break;
            }
            tutorialBox.animate().alpha(1f).setDuration(200);
        });
    }

    // 🔥 NOVA MATEMÁTICA: Posiciona no centro exato do botão!
    private void posicionarSeta(View alvoSeta) {
        if (tutorialArrow == null || alvoSeta == null) return;
        
        // Espera o botão de play carregar o tamanho na tela para não dar erro
        if (alvoSeta.getWidth() <= 0) {
            alvoSeta.postDelayed(() -> posicionarSeta(alvoSeta), 50);
            return;
        }
        
        tutorialArrow.setVisibility(View.VISIBLE);
        tutorialArrow.setAlpha(0f);
        
        tutorialArrow.post(() -> {
            int[] posAlvo = new int[2];
            int[] posOverlay = new int[2];
            
            alvoSeta.getLocationOnScreen(posAlvo);
            tutorialOverlay.getLocationOnScreen(posOverlay);
            
            // X: Centro exato do botão de Play, descontando o tamanho da própria seta
            float setaX = posAlvo[0] - posOverlay[0] + (alvoSeta.getWidth() / 2f) - (tutorialArrow.getWidth() / 2f); 
            // Y: Exatamente embaixo do botão de Play (com um espacinho de 5px)
            float setaY = posAlvo[1] - posOverlay[1] + alvoSeta.getHeight() + 5; 
            
            tutorialArrow.setX(setaX);
            tutorialArrow.setY(setaY + 40); // Nasce um pouco mais embaixo para o efeito de entrada
            
            // Animação de entrada (surgindo e subindo para o lugar certo)
            tutorialArrow.animate().alpha(1f).translationY(setaY).setDuration(500).start();
            
            // Animação da seta flutuando para sempre (subindo e descendo apontando para o play)
            if (arrowAnimator != null) arrowAnimator.cancel();
            arrowAnimator = ObjectAnimator.ofFloat(tutorialArrow, "translationY", setaY, setaY + 20f);
            arrowAnimator.setDuration(600);
            arrowAnimator.setRepeatMode(ValueAnimator.REVERSE);
            arrowAnimator.setRepeatCount(ValueAnimator.INFINITE);
            arrowAnimator.start();
        });
    }

    private void esconderSeta() {
        if (tutorialArrow != null) {
            tutorialArrow.setVisibility(View.GONE);
            if (arrowAnimator != null) arrowAnimator.cancel();
        }
    }

    private void focar(View... alvos) {
        if (alvos == null || alvos.length == 0 || highlightFrame == null) return;
        
        for (View alvo : alvos) {
            if (alvo != null && alvo.getWidth() <= 0) {
                alvos[0].postDelayed(() -> focar(alvos), 50);
                return;
            }
        }

        highlightFrame.setVisibility(View.VISIBLE);
        highlightFrame.setAlpha(0f);
        highlightFrame.animate().alpha(1f).setDuration(200).start();

        alvos[0].post(() -> {
            int[] posOverlay = new int[2];
            tutorialOverlay.getLocationOnScreen(posOverlay);
            
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            for (View alvo : alvos) {
                if (alvo == null) continue;
                int[] posAlvo = new int[2];
                alvo.getLocationOnScreen(posAlvo);

                float startX = posAlvo[0] - posOverlay[0] - 20;
                float startY = posAlvo[1] - posOverlay[1] - 20;
                float endX = startX + alvo.getWidth() + 40;
                float endY = startY + alvo.getHeight() + 40;

                if (startX < minX) minX = startX;
                if (startY < minY) minY = startY;
                if (endX > maxX) maxX = endX;
                if (endY > maxY) maxY = endY;
            }

            if (minX == Float.MAX_VALUE) return;

            float finalX = minX;
            float finalY = minY;
            int larguraFinal = (int) (maxX - minX);
            int alturaFinal = (int) (maxY - minY);

            highlightFrame.animate().x(finalX).y(finalY).setDuration(500).start();
                
            ViewGroup.LayoutParams params = highlightFrame.getLayoutParams();
            params.width = larguraFinal;
            params.height = alturaFinal;
            highlightFrame.setLayoutParams(params);

            if (tutorialOverlay instanceof TutorialMaskView) {
                ((TutorialMaskView) tutorialOverlay).setTarget(finalX, finalY, larguraFinal, alturaFinal);
            }

            moverCaixaTexto(finalY < 1000); 
        });
    }

    private void moverCaixaTexto(boolean itemNoTopo) {
        if (tutorialBox == null) return;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tutorialBox.getLayoutParams();
        params.removeRule(RelativeLayout.CENTER_IN_PARENT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        
        if (itemNoTopo) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.bottomMargin = 250;
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.topMargin = 250;
        }
        tutorialBox.setLayoutParams(params);
    }

    private void finalizar() {
        esconderSeta(); // Garante que a seta suma caso o usuário saia rápido
        
        tutorialOverlay.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            tutorialOverlay.setVisibility(View.GONE);
            
            View lock = rootView.findViewById(R.id.lockOverlayFloresta);
            if (lock != null) {
                lock.setVisibility(View.VISIBLE);
                lock.setBackgroundColor(android.graphics.Color.parseColor("#CC000000"));
            }
            
            View vol = rootView.findViewById(R.id.seekChuva);
            if (vol != null) {
                vol.setVisibility(View.GONE);
            }

            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("tutorial_visto", true).apply();
        });
    }
}

