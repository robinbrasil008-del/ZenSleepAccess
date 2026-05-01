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

    // 🔥 RAIO-X: Acha a ImageView mesmo que ela esteja escondida dentro de um Layout!
    private void setCardPlayingState(View card, int idBotaoReal, boolean isPlaying) {
        if (card == null) return;
        
        View btnView = rootView.findViewById(idBotaoReal);
        if (btnView != null) {
            int imagemDrawable = isPlaying ? R.drawable.ic_media_pause : R.drawable.ic_media_play;
            
            if (btnView instanceof ImageView) {
                ((ImageView) btnView).setImageResource(imagemDrawable);
            } else if (btnView instanceof ViewGroup) {
                // Se o botão for um layout, ele caça a imagem dentro dele!
                ViewGroup vg = (ViewGroup) btnView;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    if (child instanceof ImageView) {
                        ((ImageView) child).setImageResource(imagemDrawable);
                        break;
                    }
                }
            }
        }
        
        // Equalizador blindado: Traz pra frente e dá elevação pra não sumir
        View equalizer = card.findViewById(R.id.equalizer);
        if (equalizer != null) {
            if (isPlaying) {
                equalizer.setVisibility(View.VISIBLE);
                equalizer.setAlpha(1f);
                equalizer.bringToFront();
                equalizer.setElevation(100f); 
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
                    
                    setCardPlayingState(cardChuva, R.id.btnPlayChuva, false);
                    focar(cardChuva);
                    
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
                    
                    // 🔥 AGORA VAI: Chuva COMEÇA A TOCAR (Mostra o PAUSE e o equalizer)
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

                    // 🔥 LIGA TUDO: Os dois tocando (Pause + Equalizador)!
                    setCardPlayingState(card1, R.id.btnPlayChuva, true);
                    setCardPlayingState(card2, R.id.btnPlayFloresta, true);

                    // O SINAL DE "+" MATEMATICAMENTE PERFEITO
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
                            
                            // Calcula se os cards estão lado a lado ou um em cima do outro
                            boolean ladoALado = Math.abs(pos1[1] - pos2[1]) < 100;
                            float posX, posY;
                            
                            if (ladoALado) {
                                float bordaDir1 = pos1[0] - posOverlay[0] + card1.getWidth();
                                float bordaEsq2 = pos2[0] - posOverlay[0];
                                posX = ((bordaDir1 + bordaEsq2) / 2f) - 25;
                                posY = pos1[1] - posOverlay[1] + (card1.getHeight() / 2f) - 45;
                            } else {
                                posX = pos1[0] - posOverlay[0] + (card1.getWidth() / 2f) - 25;
                                float fundoCard1 = pos1[1] - posOverlay[1] + card1.getHeight();
                                float topoCard2 = pos2[1] - posOverlay[1];
                                posY = ((fundoCard1 + topoCard2) / 2f) - 45;
                            }
                            
                            sinalMais.setX(posX);
                            sinalMais.setY(posY);
                            sinalMais.animate().alpha(1f).setDuration(400).start();
                        });
                    }

                    focar(card1, card2);
                    break;
                    
                case 3:
                    if (sinalMais != null) sinalMais.setVisibility(View.GONE);
                    
                    // 🔥 Desliga Floresta (volta pro play original)
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
        
        if (alvoSeta.getWidth() <= 0) {
            alvoSeta.postDelayed(() -> posicionarSeta(alvoSeta), 50);
            return;
        }
        
        tutorialArrow.post(() -> {
            tutorialArrow.setVisibility(View.VISIBLE);
            int[] posAlvo = new int[2];
            int[] posOverlay = new int[2];
            alvoSeta.getLocationOnScreen(posAlvo);
            tutorialOverlay.getLocationOnScreen(posOverlay);
            
            float alvoCentroX = posAlvo[0] - posOverlay[0] + (alvoSeta.getWidth() / 2f);
            float alvoCentroY = posAlvo[1] - posOverlay[1] + (alvoSeta.getHeight() / 2f);
            
            // Verifica se o alvo está muito no fundo da tela (ex: o cadeado)
            boolean alvoNoRodape = alvoCentroY > (tutorialOverlay.getHeight() * 0.65f);
            
            float recuoX = 70f;
            float recuoY = 70f;
            if (alvoSeta.getHeight() > 200) {
                recuoX = alvoSeta.getWidth() / 2.0f;
                recuoY = alvoSeta.getHeight() / 2.0f;
            }

            float setaX = (alvoCentroX > tutorialOverlay.getWidth()/2f) ? alvoCentroX - tutorialArrow.getWidth() - recuoX : alvoCentroX + recuoX;
            if (alvoCentroX <= tutorialOverlay.getWidth()/2f) tutorialArrow.setScaleX(-1f); else tutorialArrow.setScaleX(1f);
            
            float setaY;
            if (alvoNoRodape) {
                // 🔥 Seta ninja: Aponta DE CIMA PRA BAIXO se o item tá no rodapé!
                setaY = alvoCentroY - recuoY - tutorialArrow.getHeight();
                tutorialArrow.setScaleY(-1f); 
            } else {
                // Seta normal: Aponta DE BAIXO PRA CIMA
                setaY = alvoCentroY + recuoY;
                tutorialArrow.setScaleY(1f);
            }
            
            // Usando .y() em vez de translationY pra animação não pular
            tutorialArrow.animate().x(setaX).y(setaY).alpha(1f).setDuration(400).start();
            
            if (arrowAnimator != null) arrowAnimator.cancel();
            float puloY = alvoNoRodape ? setaY - 15f : setaY + 15f;
            arrowAnimator = ObjectAnimator.ofFloat(tutorialArrow, "y", setaY, puloY);
            arrowAnimator.setDuration(600).setRepeatMode(ValueAnimator.REVERSE);
            arrowAnimator.setRepeatCount(ValueAnimator.INFINITE);
            arrowAnimator.start();
            
            if (tutorialBox != null) {
                float caixaY = alvoNoRodape ? setaY - tutorialBox.getHeight() - 20 : setaY + tutorialArrow.getHeight() - 20;
                
                // Trava de segurança para a caixa não sumir da tela
                if (caixaY < 50) caixaY = 50;
                if (caixaY + tutorialBox.getHeight() > tutorialOverlay.getHeight() - 50) {
                    caixaY = tutorialOverlay.getHeight() - tutorialBox.getHeight() - 50;
                }
                
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
            
            // Só move a caixa via 'focar' se não tiver seta agindo
            if (tutorialArrow == null || tutorialArrow.getVisibility() != View.VISIBLE) {
                moverCaixaTexto(minY, p.height);
            }
        });
    }

    private void moverCaixaTexto(float alvoY, float alvoAltura) {
        if (tutorialBox == null) return;
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
            
            // 🔥 Tudo finalizado: Zera a simulação e volta pro Play!
            setCardPlayingState(rootView.findViewById(R.id.cardChuva), R.id.btnPlayChuva, false);
            setCardPlayingState(rootView.findViewById(R.id.cardFloresta), R.id.btnPlayFloresta, false);
            
            context.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE).edit().putBoolean("tutorial_visto", true).apply();
        });
    }
}

