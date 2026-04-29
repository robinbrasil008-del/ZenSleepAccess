package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.view.ViewGroup;

public class TutorialHelper {

    private Context context;
    private View rootView;
    private RelativeLayout tutorialOverlay;
    private View highlightFrame;
    private LinearLayout tutorialBox;
    private TextView tutorialText;
    private Button btnProximo;
    private int tutorialStep = 0;

    public TutorialHelper(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        
        // Mapeia os componentes do XML
        this.tutorialOverlay = rootView.findViewById(R.id.tutorialOverlay);
        this.highlightFrame = rootView.findViewById(R.id.highlightFrame);
        this.tutorialBox = rootView.findViewById(R.id.tutorialBox);
        this.tutorialText = rootView.findViewById(R.id.tutorialText);
        this.btnProximo = rootView.findViewById(R.id.btnProximo);
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
                    focar(rootView.findViewById(R.id.cardChuva)); 
                    break;
                case 1:
                    tutorialText.setText("Regule o volume aqui para criar o ambiente perfeito.");
                    focar(rootView.findViewById(R.id.seekChuva)); 
                    break;
                case 2:
                    tutorialText.setText("Você pode tocar vários sons ao mesmo tempo! Misture como preferir para relaxar.");
                    if (highlightFrame != null) highlightFrame.setVisibility(View.GONE);
                    moverCaixaTexto(true);
                    break;
                case 3:
                    tutorialText.setText("Os sons com o cadeado são PREMIUM. Assista um anúncio rápido e desbloqueie!");
                    
                    // 🔥 O TRUQUE: Pegamos o cadeado e deixamos ele quase invisível 
                    // para o "furo" do tutorial mostrar o card lá no fundo!
                    View cadeado = rootView.findViewById(R.id.lockOverlayFloresta);
                    if (cadeado != null) {
                        cadeado.animate().alpha(0.2f).setDuration(500).start();
                    }
                    
                    focar(cadeado);
                    break;
            }
            tutorialBox.animate().alpha(1f).setDuration(200);
        });
    }

        private void focar(View alvo) {
        if (alvo == null || highlightFrame == null) return;
        
        highlightFrame.setVisibility(View.VISIBLE);
        highlightFrame.setAlpha(0f);
        highlightFrame.animate().alpha(1f).setDuration(200).start();

        alvo.post(() -> {
            int[] posAlvo = new int[2];
            int[] posOverlay = new int[2];
            
            alvo.getLocationOnScreen(posAlvo);
            tutorialOverlay.getLocationOnScreen(posOverlay);
            
            // Cálculo preciso para posicionar a moldura roxa (sem desvios)
            float finalX = posAlvo[0] - posOverlay[0] - 20; 
            float finalY = posAlvo[1] - posOverlay[1] - 20;
            
            highlightFrame.animate()
                .x(finalX)
                .y(finalY)
                .setDuration(500).start();
                
            ViewGroup.LayoutParams params = highlightFrame.getLayoutParams();
            params.width = alvo.getWidth() + 40;
            params.height = alvo.getHeight() + 40;
            highlightFrame.setLayoutParams(params);

            // 🔥 A MÁGICA DO FURO: Dizemos para a máscara criar o buraco!
            // Passamos a posição exata e o tamanho para ele apagar o fundo
            if (tutorialOverlay instanceof TutorialMaskView) {
                ((TutorialMaskView) tutorialOverlay).setTarget(
                    finalX, // Posição X dentro do overlay
                    finalY, // Posição Y dentro do overlay
                    alvo.getWidth() + 40, // Largura total com respiro
                    alvo.getHeight() + 40 // Altura total com respiro
                );
            }

            // Move a caixa de texto
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
            // Foco está no topo, caixa vai para baixo
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.bottomMargin = 250;
            params.topMargin = 0;
        } else {
            // Foco está embaixo, caixa vai para cima
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.topMargin = 250;
            params.bottomMargin = 0;
        }
        tutorialBox.setLayoutParams(params);
    }

        private void finalizar() {
        tutorialOverlay.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            tutorialOverlay.setVisibility(View.GONE);
            
            // 🔄 VOLTA O CADEADO AO NORMAL (ESCURO)
            View cadeado = rootView.findViewById(R.id.lockOverlayFloresta);
            if (cadeado != null) cadeado.setAlpha(1.0f);

            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("tutorial_visto", true).apply();
        });
    }

}
