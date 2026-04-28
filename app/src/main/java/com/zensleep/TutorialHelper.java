package com.zensleep;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;

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
        tutorialBox.animate().alpha(0.3f).setDuration(200).withEndAction(() -> {
            switch (step) {
                case 0:
                    tutorialText.setText("Toque em um card para dar o play no som da natureza.");
                    focar(rootView.findViewById(R.id.cardChuva)); 
                    break;
                case 1:
                    tutorialText.setText("Regule o volume aqui para criar o ambiente perfeito.");
                    focar(rootView.findViewById(R.id.seekBarChuva)); 
                    break;
                case 2:
                    tutorialText.setText("Você pode tocar vários sons ao mesmo tempo! Misture como preferir.");
                    highlightFrame.setVisibility(View.GONE);
                    break;
                case 3:
                    tutorialText.setText("Os sons com o cadeado são PREMIUM. Assista um anúncio e relaxe!");
                    highlightFrame.setVisibility(View.VISIBLE);
                    focar(rootView.findViewById(R.id.lockFloresta));
                    break;
            }
            tutorialBox.animate().alpha(1f).setDuration(200);
        });
    }

    private void focar(View alvo) {
        if (alvo == null) return;
        highlightFrame.setVisibility(View.VISIBLE);
        alvo.post(() -> {
            int[] pos = new int[2];
            alvo.getLocationInWindow(pos);
            
            highlightFrame.animate()
                .x(pos[0] - 15)
                .y(pos[1] - 15)
                .setDuration(400).start();
                
            highlightFrame.getLayoutParams().width = alvo.getWidth() + 30;
            highlightFrame.getLayoutParams().height = alvo.getHeight() + 30;
            highlightFrame.requestLayout();
        });
    }

    private void finalizar() {
        tutorialOverlay.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            tutorialOverlay.setVisibility(View.GONE);
            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("tutorial_visto", true).apply();
        });
    }
}

