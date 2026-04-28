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
        // Se já viu ou se não achar o layout, não faz nada
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
                    tutorialText.setText("Você pode tocar vários sons ao mesmo tempo! Misture como preferir.");
                    highlightFrame.setVisibility(View.GONE);
                    moverCaixaTexto(true); // Centraliza
                    break;
                case 3:
                    tutorialText.setText("Os sons com o cadeado são PREMIUM. Assista um anúncio e relaxe!");
                    focar(rootView.findViewById(R.id.lockOverlayFloresta));
                    break;
            }
            tutorialBox.animate().alpha(1f).setDuration(200);
        });
    }

    private void focar(View alvo) {
        if (alvo == null || highlightFrame == null) return;
        
        highlightFrame.setVisibility(View.VISIBLE);
        alvo.post(() -> {
            int[] posAlvo = new int[2];
            int[] posPai = new int[2];
            
            alvo.getLocationInWindow(posAlvo);
            tutorialOverlay.getLocationInWindow(posPai);
            
            // Calcula a posição real subtraindo a posição do layout pai
            float finalX = posAlvo[0] - posPai[0] - 15;
            float finalY = posAlvo[1] - posPai[1] - 15;
            
            highlightFrame.animate()
                .x(finalX)
                .y(finalY)
                .setDuration(400).start();
                
            // Ajusta o tamanho da moldura para o tamanho do item
            ViewGroup.LayoutParams params = highlightFrame.getLayoutParams();
            params.width = alvo.getWidth() + 30;
            params.height = alvo.getHeight() + 30;
            highlightFrame.setLayoutParams(params);

            // Move a caixa de texto para não ficar em cima do foco
            moverCaixaTexto(finalY < 800); 
        });
    }

    private void moverCaixaTexto(boolean itemNoTopo) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tutorialBox.getLayoutParams();
        params.removeRule(RelativeLayout.CENTER_IN_PARENT);
        
        if (itemNoTopo) {
            // Se o item focado está em cima, move a caixa para baixo
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.bottomMargin = 200;
        } else {
            // Se o item está embaixo, move a caixa para cima
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.topMargin = 200;
        }
        tutorialBox.setLayoutParams(params);
    }

    private void finalizar() {
        tutorialOverlay.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            tutorialOverlay.setVisibility(View.GONE);
            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("tutorial_visto", true).apply();
        });
    }
}
