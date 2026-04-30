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
import android.graphics.Color;
import android.graphics.Typeface;

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
    private TextView sinalMais;
    
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

    // 🔥 FUNÇÃO CORRIGIDA: Pega o ID "ic_play" de dentro de cada card e troca a imagem
    private void setCardPlayingState(View card, boolean isPlaying) {
        if (card == null) return;
        
        // Procura o componente pelo ID que está no seu layout
        ImageView btnPlayImage = card.findViewById(R.id.ic_play);
        
        if (btnPlayImage != null) {
            // Se estiver "tocando", coloca o desenho do PAUSE (ic_midia_pause)
            // Se não, volta pro desenho do PLAY (ic_midia_play)
            int imagemId = isPlaying ? R.drawable.ic_midia_pause : R.drawable.ic_midia_play;
            btnPlayImage.setImageResource(imagemId);
        }
        
        // Liga ou desliga o equalizador
        View equalizer = card.findViewById(R.id.equalizer);
        if (equalizer != null) {
            if (isPlaying) {
                equalizer.setVisibility(View.VISIBLE);
                equalizer.setAlpha(1f);
                equalizer.bringToFront();
            } else {
                equalizer.setVisibility(View.GONE);
            }
        }
    }

    private void configurarEtapa(int step) {
        tutorialBox.animate().alpha(0f).setDuration(200).withEndAction(() -> {
            switch (step) {
                case 0:
                    tutorialText.setText("Toque em um card para dar o play no som da natureza.");
                    View cardChuva = rootView.findViewById(R.id.cardChuva);
                    setCardPlayingState(cardChuva, false);
                    focar(cardChuva);
                    
                    if (cardChuva != null) {
                        posicionarSeta(cardChuva.findViewById(R.id.ic_play));
                    }
                    break;
                    
                case 1:
                    tutorialText.setText("Regule o volume aqui para criar o ambiente perfeito.");
                    View vol = rootView.findViewById(R.id.seekChuva);
                    if (vol != null) {
                        vol.setVisibility(View.VISIBLE);
                        vol.setAlpha(1f);
                    }
                    
                    // Coloca a Chuva pra "tocar" (mostra o pause e o equalizer)
                    setCardPlayingState(rootView.findViewById(R.id.cardChuva), true);
                    
                    focar(vol);
                    posicionarSeta(vol);
                    break;
                    
                case 2:
                    esconderSeta();
                    tutorialText.setText("Você pode tocar vários sons ao mesmo tempo! Misture como preferir para relaxar.");
                    
                    View card1 = rootView.findViewById(R.id.cardChuva);
                    View card2 = rootView.findViewById(R.id.cardFloresta);
                    
                    // Os dois cards tocando (Pause + Equalizer)
                    setCardPlayingState(card1, true);
                    setCardPlayingState(card2, true);

                    // Sinal de "+" vertical entre os cards
                    if (sinalMais == null) {
                        sinalMais = new TextView(context);
                        sinalMais.setText("+");
                        sinalMais.setTextSize(65f);
                        sinalMais.setTextColor(Color.WHITE);
                        sinalMais.setTypeface(null, Typeface.BOLD);
                        sinalMais.setShadowLayer(15f, 0f, 0f, Color.parseColor("#CC000000"));
                        tutorialOverlay.addView(sinalMais);
                    }
                    sinalMais.setVisibility(View.VISIBLE);
                    sinalMais.setAlpha(0f);

                    if (card1 != null && card2 != null) {
                        card1.post(() -> {
                            int[] pos1 = new int[2];
                            int[] pos2 = new int[2];
                            int[] posOverlay = new int[2];
                            card1.getLocationOnScreen(pos1);
                            card2.getLocationOnScreen(pos2);
                            tutorialOverlay.getLocationOnScreen(posOverlay);
                            
                            float centroX = pos1[0] - posOverlay[0] + (card1.getWidth() / 2f);
                            float posY = (pos1[1] - posOverlay[1] + card1.getHeight() + (pos2[1] - posOverlay[1])) / 2f - 45;
                            sinalMais.setX(centroX - 25);
                            sinalMais.setY(posY);
                            sinalMais.animate().alpha(1f).setDuration(400).start();
                        });
                    }

                    focar(card1, card2);
                    break;
                    
                case 3:
                    if (sinalMais != null) sinalMais.setVisibility(View.GONE);
                    
                    // Para a Floresta (volta pro play)
                    setCardPlayingState(rootView.findViewById(R.id.cardFloresta), false);
                    
                    tutorialText.setText("Os sons com o cadeado são PREMIUM. Assista um anúncio rápido e desbloqueie!");
                    View lock = rootView.findViewById(R.id.lockOverlayFloresta);
                    focar(lock);
                    posicionarSeta(lock);
                    break;
            }
            tutorialBox.animate().alpha(1f).setDuration(200);
        });
    }

    // Métodos posicionarSeta, esconderSeta, focar, moverCaixaTexto e finalizar continuam iguais...
    // (Abaixo apenas os métodos de suporte para garantir que o código rode)

    private void posicionarSeta(View alvoSeta) {
        if (tutorialArrow == null || alvoSeta == null) return;
        tutorialArrow.post(() -> {
            int[] posAlvo = new int[2];
            int[] posOverlay = new int[2];
            alvoSeta.getLocationOnScreen(posAlvo);
            tutorialOverlay.getLocationOnScreen(posOverlay);
            float alvoCentroX = posAlvo[0] - posOverlay[0] + (alvoSeta.getWidth() / 2f);
            float alvoCentroY = posAlvo[1] - posOverlay[1] + (alvoSeta.getHeight() / 2f);
            float setaX = (alvoCentroX > tutorialOverlay.getWidth()/2f) ? alvoCentroX - tutorialArrow.getWidth() - 70 : alvoCentroX + 70;
            if (alvoCentroX <= tutorialOverlay.getWidth()/2f) tutorialArrow.setScaleX(-1f); else tutorialArrow.setScaleX(1f);
            float setaY = alvoCentroY + (alvoSeta.getHeight() > 200 ? alvoSeta.getHeight()/2f : 70f);
            tutorialArrow.animate().x(setaX).y(setaY).alpha(1f).setDuration(400).start();
            if (arrowAnimator != null) arrowAnimator.cancel();
            arrowAnimator = ObjectAnimator.ofFloat(tutorialArrow, "translationY", setaY, setaY + 15f);
            arrowAnimator.setDuration(600).setRepeatMode(ValueAnimator.REVERSE);
            arrowAnimator.setRepeatCount(ValueAnimator.INFINITE);
            arrowAnimator.start();
            
            if (tutorialBox != null) {
                float caixaY = setaY + tutorialArrow.getHeight() - 20;
                tutorialBox.animate().y(caixaY).setDuration(400).start();
            }
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
        highlightFrame.setVisibility(View.VISIBLE);
        alvos[0].post(() -> {
            int[] posOverlay = new int[2];
            tutorialOverlay.getLocationOnScreen(posOverlay);
            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
            for (View alvo : alvos) {
                if (alvo == null) continue;
                int[] pos = new int[2];
                alvo.getLocationOnScreen(pos);
                float startX = pos[0] - posOverlay[0] - 20, startY = pos[1] - posOverlay[1] - 20;
                minX = Math.min(minX, startX); minY = Math.min(minY, startY);
                maxX = Math.max(maxX, startX + alvo.getWidth() + 40); maxY = Math.max(maxY, startY + alvo.getHeight() + 40);
            }
            highlightFrame.animate().x(minX).y(minY).setDuration(500).start();
            ViewGroup.LayoutParams p = highlightFrame.getLayoutParams();
            p.width = (int)(maxX - minX); p.height = (int)(maxY - minY);
            highlightFrame.setLayoutParams(p);
            if (tutorialOverlay instanceof TutorialMaskView) ((TutorialMaskView) tutorialOverlay).setTarget(minX, minY, p.width, p.height);
            moverCaixaTexto(minY, p.height);
        });
    }

    private void moverCaixaTexto(float alvoY, float alvoAltura) {
        if (tutorialArrow != null && tutorialArrow.getVisibility() == View.VISIBLE) return;
        float caixaY = (alvoY < 1000) ? alvoY + alvoAltura + 50 : alvoY - tutorialBox.getHeight() - 50;
        tutorialBox.animate().y(caixaY).setDuration(400).start();
    }

    private void finalizar() {
        esconderSeta();
        if (sinalMais != null) sinalMais.setVisibility(View.GONE);
        tutorialOverlay.animate().alpha(0f).setDuration(500).withEndAction(() -> {
            tutorialOverlay.setVisibility(View.GONE);
            setCardPlayingState(rootView.findViewById(R.id.cardChuva), false);
            setCardPlayingState(rootView.findViewById(R.id.cardFloresta), false);
            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE).edit().putBoolean("tutorial_visto", true).apply();
        });
    }
}

