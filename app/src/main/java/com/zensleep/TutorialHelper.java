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

    // 🔥 FUNÇÃO BLINDADA: Pega o seu botão REAL (btnPlayChuva) e joga o drawable ic_media_pause dentro!
    private void setCardPlayingState(View card, int idBotaoReal, boolean isPlaying) {
        if (card == null) return;
        
        // Pega o botão na tela pelo ID original que você sempre usou
        View btnView = rootView.findViewById(idBotaoReal);
        
        if (btnView instanceof ImageView) {
            ImageView btnPlayImage = (ImageView) btnView;
            // ⚠️ ATENÇÃO: Se no seu projeto estiver escrito ic_midia_pause com "i", mude aqui embaixo!
            int imagemDrawable = isPlaying ? R.drawable.ic_media_pause : R.drawable.ic_media_play;
            btnPlayImage.setImageResource(imagemDrawable);
        }
        
        // O equalizador a gente procura dentro do card (porque o ID é só @+id/equalizer)
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
                    
                    // Garante que tá como PLAY no começo
                    setCardPlayingState(cardChuva, R.id.btnPlayChuva, false);
                    focar(cardChuva);
                    
                    // Aponta pro botão certo
                    View btnPlayC = rootView.findViewById(R.id.btnPlayChuva);
                    posicionarSeta(btnPlayC);
                    break;
                    
                case 1:
                    tutorialText.setText("Regule o volume aqui para criar o ambiente perfeito.");
                    View vol = rootView.findViewById(R.id.seekChuva);
                    if (vol != null) {
                        vol.setVisibility(View.VISIBLE);
                        vol.setAlpha(1f);
                    }
                    if (tutorialArrow != null) tutorialArrow.setVisibility(View.VISIBLE);
                    
                    // 🔥 Chuva COMEÇA A TOCAR (Vira ic_media_pause e liga equalizador)
                    setCardPlayingState(rootView.findViewById(R.id.cardChuva), R.id.btnPlayChuva, true);
                    
                    focar(vol);
                    posicionarSeta(vol);
                    break;
                    
                case 2:
                    esconderSeta();
                    tutorialText.setText("Você pode tocar vários sons ao mesmo tempo! Misture como preferir para relaxar.");
                    
                    View lockFloresta = rootView.findViewById(R.id.lockOverlayFloresta);
                    if (lockFloresta != null) lockFloresta.setVisibility(View.INVISIBLE);

                    View card1 = rootView.findViewById(R.id.cardChuva);
                    View card2 = rootView.findViewById(R.id.cardFloresta);

                    View seekC = rootView.findViewById(R.id.seekChuva);
                    View seekF = rootView.findViewById(R.id.seekFloresta);
                    if (seekC != null) { seekC.setVisibility(View.VISIBLE); seekC.setAlpha(1f); }
                    if (seekF != null) { seekF.setVisibility(View.VISIBLE); seekF.setAlpha(1f); }

                    // 🔥 LIGA TUDO: Os dois cards "tocando" com imagem de PAUSE e Equalizador!
                    setCardPlayingState(card1, R.id.btnPlayChuva, true);
                    setCardPlayingState(card2, R.id.btnPlayFloresta, true);

                    // O SINAL DE "+"
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
                    
                    // 🔥 Para a Floresta (volta pro ic_media_play)
                    setCardPlayingState(rootView.findViewById(R.id.cardFloresta), R.id.btnPlayFloresta, false);

                    View seekF2 = rootView.findViewById(R.id.seekFloresta);
                    if (seekF2 != null) seekF2.setVisibility(View.GONE);
                    
                    tutorialText.setText("Os sons com o cadeado são PREMIUM. Assista um anúncio rápido e desbloqueie!");
                    View lock = rootView.findViewById(R.id.lockOverlayFloresta);
                    if (lock != null) {
                        lock.setVisibility(View.VISIBLE);
                        lock.setBackgroundResource(0);
                    }
                    
                    if (tutorialArrow != null) tutorialArrow.setVisibility(View.VISIBLE);
                    focar(lock);
                    posicionarSeta(lock);
                    break;
            }
            tutorialBox.animate().alpha(1f).setDuration(200);
        });
    }

    private void posicionarSeta(View alvoSeta) {
        if (tutorialArrow == null || alvoSeta == null) return;
        alvoSeta.postDelayed(() -> {
            if (alvoSeta.getWidth() <= 0) {
                posicionarSeta(alvoSeta);
                return;
            }
            tutorialArrow.setVisibility(View.VISIBLE);
            int[] posAlvo = new int[2];
            int[] posOverlay = new int[2];
            alvoSeta.getLocationOnScreen(posAlvo);
            tutorialOverlay.getLocationOnScreen(posOverlay);
            
            float alvoCentroX = posAlvo[0] - posOverlay[0] + (alvoSeta.getWidth() / 2f);
            float alvoCentroY = posAlvo[1] - posOverlay[1] + (alvoSeta.getHeight() / 2f);
            
            float recuoX = 70f;
            float recuoY = 70f;
            if (alvoSeta.getHeight() > 200) {
                recuoX = alvoSeta.getWidth() / 2.0f;
                recuoY = alvoSeta.getHeight() / 2.0f;
            }

            float setaX = (alvoCentroX > tutorialOverlay.getWidth()/2f) ? alvoCentroX - tutorialArrow.getWidth() - recuoX : alvoCentroX + recuoX;
            if (alvoCentroX <= tutorialOverlay.getWidth()/2f) tutorialArrow.setScaleX(-1f); else tutorialArrow.setScaleX(1f);
            
            float setaY = alvoCentroY + recuoY;
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
        }, 50);
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
            
            View lock = rootView.findViewById(R.id.lockOverlayFloresta);
            if (lock != null) {
                lock.setVisibility(View.VISIBLE);
                lock.setBackgroundColor(android.graphics.Color.parseColor("#CC000000"));
            }
            
            View volC = rootView.findViewById(R.id.seekChuva);
            if (volC != null) volC.setVisibility(View.GONE);
            View volF = rootView.findViewById(R.id.seekFloresta);
            if (volF != null) volF.setVisibility(View.GONE);
            
            // 🔥 Volta pros play originais (ic_media_play) quando o tutorial acaba
            setCardPlayingState(rootView.findViewById(R.id.cardChuva), R.id.btnPlayChuva, false);
            setCardPlayingState(rootView.findViewById(R.id.cardFloresta), R.id.btnPlayFloresta, false);
            
            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE).edit().putBoolean("tutorial_visto", true).apply();
        });
    }
}

