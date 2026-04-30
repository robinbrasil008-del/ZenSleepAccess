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

    // 🔥 CHAVE MESTRA: Esta função liga ou desliga o card (Pause/Play e Equalizador)!
    private void setCardPlayingState(View card, boolean isPlaying) {
        if (card == null) return;
        
        // Troca a imagem para ic_midia_pause se estiver tocando, ou ic_midia_play se parar
        ImageView icPlay = card.findViewById(R.id.ic_play);
        if (icPlay != null) {
            icPlay.setImageResource(isPlaying ? R.drawable.ic_midia_pause : R.drawable.ic_midia_play);
        }
        
        // Liga ou desliga o Equalizador e traz ele para a frente de tudo
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
                    
                    if (tutorialArrow != null) tutorialArrow.setVisibility(View.VISIBLE);
                    View cardChuva = rootView.findViewById(R.id.cardChuva);
                    
                    // Garante que a Chuva mostra o botão de PLAY no começo
                    setCardPlayingState(cardChuva, false);
                    focar(cardChuva); 
                    
                    // Seta aponta direto para a imagem ic_play dentro do card da Chuva
                    View icPlayBtn = cardChuva != null ? cardChuva.findViewById(R.id.ic_play) : null;
                    posicionarSeta(icPlayBtn);
                    break;
                    
                case 1:
                    tutorialText.setText("Regule o volume aqui para criar o ambiente perfeito.");
                    
                    View vol = rootView.findViewById(R.id.seekChuva);
                    if (vol != null) {
                        vol.setVisibility(View.VISIBLE);
                        vol.setAlpha(1f);
                    }
                    if (tutorialArrow != null) tutorialArrow.setVisibility(View.VISIBLE);
                    
                    // 🔥 AGORA SIM: A Chuva "começa a tocar" (mostra Pause e liga o Equalizador)
                    View cardC1 = rootView.findViewById(R.id.cardChuva);
                    setCardPlayingState(cardC1, true); 
                    
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

                    // 🔥 LIGA TUDO: Os dois cards "tocando" (Ambos com Pause e Equalizador ligados!)
                    setCardPlayingState(card1, true);
                    setCardPlayingState(card2, true);

                    // O SINAL DE "+"
                    if (sinalMais == null) {
                        sinalMais = new TextView(context);
                        sinalMais.setText("+");
                        sinalMais.setTextSize(65f); 
                        sinalMais.setTextColor(Color.WHITE);
                        sinalMais.setTypeface(null, Typeface.BOLD);
                        sinalMais.setShadowLayer(15f, 0f, 0f, Color.parseColor("#CC000000"));
                        sinalMais.setElevation(50f);
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
                            
                            // O "+" FICA NO VÃO VERTICAL ENTRE OS CARDS
                            float centroX = pos1[0] - posOverlay[0] + (card1.getWidth() / 2f);
                            float baseCardChuva = pos1[1] - posOverlay[1] + card1.getHeight();
                            float topoCardFloresta = pos2[1] - posOverlay[1];
                            
                            float posY = baseCardChuva + ((topoCardFloresta - baseCardChuva) / 2f) - 45; 
                            float posX = centroX - 25; 
                            
                            sinalMais.setX(posX);
                            sinalMais.setY(posY);
                            sinalMais.animate().alpha(1f).setDuration(400).start();
                        });
                    }

                    focar(card1, card2);
                    break;
                    
                case 3:
                    if (sinalMais != null) {
                        sinalMais.animate().alpha(0f).setDuration(200).withEndAction(() -> sinalMais.setVisibility(View.GONE));
                    }
                    
                    // 🔥 FLORESTA DESLIGA (Volta para Play e esconde Equalizador)
                    View cardFloresta = rootView.findViewById(R.id.cardFloresta);
                    setCardPlayingState(cardFloresta, false);

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
        
        if (alvoSeta.getWidth() <= 0) {
            alvoSeta.postDelayed(() -> posicionarSeta(alvoSeta), 50);
            return;
        }
        
        tutorialArrow.post(() -> {
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

            float meioDoEcra = tutorialOverlay.getWidth() / 2f;
            float setaX;
            
            if (alvoCentroX > meioDoEcra) {
                tutorialArrow.setScaleX(1f);
                setaX = alvoCentroX - tutorialArrow.getWidth() - recuoX; 
            } else {
                tutorialArrow.setScaleX(-1f);
                setaX = alvoCentroX + recuoX; 
            }
            
            float setaY = alvoCentroY + recuoY; 
            
            tutorialArrow.animate().x(setaX).translationY(setaY).alpha(1f).setDuration(400).start();
            
            if (tutorialBox != null) {
                if (tutorialBox.getHeight() == 0) {
                    tutorialBox.post(() -> posicionarSeta(alvoSeta));
                    return;
                }
                
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tutorialBox.getLayoutParams();
                params.removeRule(RelativeLayout.CENTER_IN_PARENT);
                params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                tutorialBox.setLayoutParams(params);
                
                float caixaY = setaY + tutorialArrow.getHeight() - 20; 
                tutorialBox.animate().y(caixaY).setDuration(400).start();
            }
            
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

            moverCaixaTexto(finalY, alturaAltura); // Corrigido para variável local
        });
    }

    private void moverCaixaTexto(float alvoY, float alvoAltura) {
        if (tutorialArrow != null && tutorialArrow.getVisibility() == View.VISIBLE) return;
        
        if (tutorialBox == null) return;
        if (tutorialBox.getHeight() == 0) {
            tutorialBox.post(() -> moverCaixaTexto(alvoY, alvoAltura));
            return;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tutorialBox.getLayoutParams();
        params.removeRule(RelativeLayout.CENTER_IN_PARENT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tutorialBox.setLayoutParams(params);
        
        float caixaY;
        if (alvoY < 1000) {
            caixaY = alvoY + alvoAltura + 50; 
        } else {
            caixaY = alvoY - tutorialBox.getHeight() - 50; 
            if (caixaY < 50) caixaY = 50; 
        }
        
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
            
            // 🔥 DESLIGA TUDO NO FINAL (Volta todos os ícones para Play e esconde Equalizadores)
            View cardC = rootView.findViewById(R.id.cardChuva);
            setCardPlayingState(cardC, false);
            
            View cardF = rootView.findViewById(R.id.cardFloresta);
            setCardPlayingState(cardF, false);

            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE)
                    .edit().putBoolean("tutorial_visto", true).apply();
        });
    }
}

