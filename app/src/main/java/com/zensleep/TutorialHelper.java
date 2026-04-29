package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
                    tutorialText.setText("Podes tocar vários sons ao mesmo tempo! Mistura como preferires para relaxar.");
                    if (highlightFrame != null) highlightFrame.setVisibility(View.GONE);
                    if (tutorialOverlay instanceof TutorialMaskView) {
                        ((TutorialMaskView) tutorialOverlay).setTarget(0, 0, 0, 0);
                    }
                    moverCaixaTexto(true);
                    break;
                    case 3:
        tutorialText.setText("Os sons com o cadeado são PREMIUM. Assista um anúncio rápido e desbloqueie!");
        
        // Pegamos o overlay do cadeado
        View lock = rootView.findViewById(R.id.lockOverlayFloresta);
        
        if (lock != null) {
            // 🔥 O SEGREDO: Tiramos apenas o FUNDO escuro, 
            // mas o ícone do cadeado e o texto continuam lá!
            lock.setBackgroundResource(0); 
        }
        
        focar(lock);
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
            int[] posOverlay = new int[2];
            
            alvo.getLocationOnScreen(posAlvo);
            tutorialOverlay.getLocationOnScreen(posOverlay);
            
            float finalX = posAlvo[0] - posOverlay[0] - 20; 
            float finalY = posAlvo[1] - posOverlay[1] - 20;
            
            highlightFrame.animate().x(finalX).y(finalY).setDuration(500).start();
                
            ViewGroup.LayoutParams params = highlightFrame.getLayoutParams();
            params.width = alvo.getWidth() + 40;
            params.height = alvo.getHeight() + 40;
            highlightFrame.setLayoutParams(params);

            if (tutorialOverlay instanceof TutorialMaskView) {
                ((TutorialMaskView) tutorialOverlay).setTarget(finalX, finalY, alvo.getWidth() + 40, alvo.getHeight() + 40);
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
        tutorialOverlay.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            tutorialOverlay.setVisibility(View.GONE);
            
            // 🔄 VOLTA O FUNDO ESCURO DO CADEADO (Para ele ficar bloqueado de novo)
            View lock = rootView.findViewById(R.id.lockOverlayFloresta);
            if (lock != null) {
                // Aqui usamos a cor padrão de bloqueio (Preto com transparência)
                lock.setBackgroundColor(android.graphics.Color.parseColor("#CC000000"));
            }

            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("tutorial_visto", true).apply();
        });
    }
}

