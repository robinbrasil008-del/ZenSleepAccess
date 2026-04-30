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
                    
                    if (tutorialArrow != null) tutorialArrow.setVisibility(View.VISIBLE);
                    View cardChuva = rootView.findViewById(R.id.cardChuva);
                    focar(cardChuva); 
                    
                    View btnPlay = rootView.findViewById(R.id.btnPlayChuva);
                    posicionarSeta(btnPlay);
                    break;
                    
                case 1:
                    tutorialText.setText("Regule o volume aqui para criar o ambiente perfeito.");
                    
                    View vol = rootView.findViewById(R.id.seekChuva);
                    if (vol != null) {
                        vol.setVisibility(View.VISIBLE);
                        vol.setAlpha(1f);
                    }
                    if (tutorialArrow != null) tutorialArrow.setVisibility(View.VISIBLE);
                    focar(vol);
                    posicionarSeta(vol);
                    break;
                    
                case 2:
                    esconderSeta(); // Seta some e a Caixa volta a fluir normal
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

    // 🔥 A MÁGICA: A seta acha o botão, e PUXA a caixa de texto pra base dela!
    private void posicionarSeta(View alvoSeta) {
        if (tutorialArrow == null || alvoSeta == null) return;
        
        if (alvoSeta.getWidth() <= 0) {
            alvoSeta.postDelayed(() -> posicionarSeta(alvoSeta), 50);
            return;
        }
        
        tutorialArrow.post(() -> {
            int[] posAlvo = new int[2];
            int[] posOverlay = new int[2];
            alvoSeta.getLocationOnScreen(posAlvo);
            tutorialOverlay.getLocationOnScreen(posOverlay);
            
            // Pega o centro EXATO do botão ou da barra de volume
            float alvoCentroX = posAlvo[0] - posOverlay[0] + (alvoSeta.getWidth() / 2f);
            float alvoCentroY = posAlvo[1] - posOverlay[1] + (alvoSeta.getHeight() / 2f);
            
            // Posiciona a Seta para o bico apontar para o centro
            float setaX = alvoCentroX - tutorialArrow.getWidth() + 40; 
            float setaY = alvoCentroY + 10; 
            
            tutorialArrow.animate().x(setaX).translationY(setaY).alpha(1f).setDuration(400).start();
            
            // GRUDA A CAIXA NA SETA!
            if (tutorialBox != null) {
                if (tutorialBox.getHeight() == 0) {
                    tutorialBox.post(() -> posicionarSeta(alvoSeta));
                    return;
                }
                
                // Limpa as posições velhas
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tutorialBox.getLayoutParams();
                params.removeRule(RelativeLayout.CENTER_IN_PARENT);
                params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                tutorialBox.setLayoutParams(params);
                
                // A caixa vai sentar exatamente embaixo da cauda da seta!
                float caixaY = setaY + tutorialArrow.getHeight() - 30; 
                tutorialBox.animate().y(caixaY).setDuration(400).start();
            }
            
            // Faz a seta flutuar (animar pra cima e pra baixo)
            if (arrowAnimator != null) arrowAnimator.cancel();
            arrowAnimator = ObjectAnimator.ofFloat(tutorialArrow, "translationY", setaY, setaY + 15f);
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

            // Chama a caixa dinâmica enviando a posição Y do brilho roxo
            moverCaixaTexto(finalY, alturaFinal); 
        });
    }

    // 🔥 NOVA MATEMÁTICA DA CAIXA: Esquece rodapé ou topo. Ela fica SEMPRE vizinha do card!
    private void moverCaixaTexto(float alvoY, float alvoAltura) {
        // Se a seta tá na tela mandando na caixa, essa função nem roda
        if (tutorialArrow != null && tutorialArrow.getVisibility() == View.VISIBLE) return;
        
        if (tutorialBox == null) return;
        if (tutorialBox.getHeight() == 0) {
            tutorialBox.post(() -> moverCaixaTexto(alvoY, alvoAltura));
            return;
        }

        // Limpa tudo
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tutorialBox.getLayoutParams();
        params.removeRule(RelativeLayout.CENTER_IN_PARENT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tutorialBox.setLayoutParams(params);
        
        float caixaY;
        if (alvoY < 1000) {
            // Se o brilho roxo tá em cima, a caixa senta logo embaixo dele (só 50px de distância)
            caixaY = alvoY + alvoAltura + 50; 
        } else {
            // Se o brilho tá lá embaixo, a caixa senta logo em cima dele
            caixaY = alvoY - tutorialBox.getHeight() - 50; 
            if (caixaY < 50) caixaY = 50; // Segurança pra não engolir o topo da tela
        }
        
        tutorialBox.animate().y(caixaY).setDuration(400).start();
    }

    private void finalizar() {
        esconderSeta();
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

