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

    // 🔥 FUNÇÃO RAIO-X: Varre o botão e acha a imagem do Play para trocar pelo Pause
    private void setCardPlayingState(View card, int idBotaoReal, boolean isPlaying) {
        if (card == null) return;
        
        View btnView = rootView.findViewById(idBotaoReal);
        if (btnView != null) {
            alterarImagemPlayPause(btnView, isPlaying);
        }
        
        // Equalizador: Agora ele pula na tela com animação!
        View equalizer = card.findViewById(R.id.equalizer);
        if (equalizer != null) {
            if (isPlaying) {
                equalizer.setVisibility(View.VISIBLE);
                equalizer.setAlpha(1f);
                equalizer.bringToFront();
                equalizer.setScaleX(0.8f);
                equalizer.setScaleY(0.8f);
                equalizer.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
            } else {
                equalizer.setVisibility(View.GONE);
            }
        }
    }

    // Método recursivo e cego a erros: procura a ImageView em qualquer nível do botão
    private void alterarImagemPlayPause(View view, boolean isPlaying) {
        // Ignora o equalizador se ele for uma ImageView
        if (view.getId() == R.id.equalizer) return;
        
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(isPlaying ? R.drawable.ic_media_pause : R.drawable.ic_media_play);
        } else if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                alterarImagemPlayPause(vg.getChildAt(i), isPlaying);
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
                    
                    // 🔥 CHUVA COMEÇA A TOCAR: Muda pra Pause e aciona o Equalizador!
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

                    // 🔥 OS DOIS TOCANDO: Pause + Equalizador em ambos!
                    setCardPlayingState(card1, R.id.btnPlayChuva, true);
                    setCardPlayingState(card2, R.id.btnPlayFloresta, true);

                    // SINAL DE "+" - Agora num tamanho decente
                    if (sinalMais == null) {
                        sinalMais = new TextView(context);
                        sinalMais.setText("+");
                        sinalMais.setTextSize(45f); // Reduzido para não engolir a tela
                        sinalMais.setTextColor(Color.WHITE);
                        sinalMais.setTypeface(null, Typeface.BOLD);
                        sinalMais.setShadowLayer(10f, 0f, 0f, Color.parseColor("#CC000000"));
                        tutorialOverlay.addView(sinalMais);
                    }
                    sinalMais.setVisibility(View.VISIBLE);
                    sinalMais.setAlpha(0f);

                    if (card1 != null && card2 != null) {
                        card1.post(() -> {
                            // Mede o tamanho real do "+" na tela
                            sinalMais.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            float widthMais = sinalMais.getMeasuredWidth();
                            float heightMais = sinalMais.getMeasuredHeight();
                            
                            int[] pos1 = new int[2];
                            int[] pos2 = new int[2];
                            int[] posOverlay = new int[2];
                            card1.getLocationOnScreen(pos1);
                            card2.getLocationOnScreen(pos2);
                            tutorialOverlay.getLocationOnScreen(posOverlay);
                            
                            // Acha o centro exato do card 1 e do card 2
                            float centroX1 = pos1[0] - posOverlay[0] + (card1.getWidth() / 2f);
                            float centroY1 = pos1[1] - posOverlay[1] + (card1.getHeight() / 2f);
                            
                            float centroX2 = pos2[0] - posOverlay[0] + (card2.getWidth() / 2f);
                            float centroY2 = pos2[1] - posOverlay[1] + (card2.getHeight() / 2f);
                            
                            // 🔥 MATEMÁTICA ABSOLUTA: O "+" fica EXATAMENTE no meio dos dois cards!
                            float meioX = (centroX1 + centroX2) / 2f;
                            float meioY = (centroY1 + centroY2) / 2f;
                            
                            sinalMais.setX(meioX - (widthMais / 2f));
                            sinalMais.setY(meioY - (heightMais / 2f));
                            sinalMais.animate().alpha(1f).setDuration(400).start();
                        });
                    }

                    focar(card1, card2);
                    break;
                    
                case 3:
                    if (sinalMais != null) sinalMais.setVisibility(View.GONE);
                    
                    // 🔥 Desliga a Floresta (Volta o Play e some o Equalizador)
                    setCardPlayingState(rootView.findViewById(R.id.cardFloresta), R.id.btnPlayFloresta, false);

                    View seekF2 = rootView.findViewById(R.id.seekFloresta);
                    if (seekF2 != null) seekF2.setVisibility(View.GONE);
                    
                    tutorialText.setText("Os sons com o cadeado são PREMIUM. Assista um anúncio rápido e desbloqueie!");
                    View lock = rootView.findViewById(R.id.lockOverlayFloresta);
                    if (lock != null) {
                        lock.setVisibility(View.VISIBLE);
                        lock.setBackgroundResource(0);
                    }
                    
                    focar(lock);
                    posicionarSeta(lock);
                    break;
            }
            tutorialBox.animate().alpha(1f).setDuration(200);
        });
    }

    private void posicionarSeta(View alvoSeta) {
        if (tutorialArrow == null || alvoSeta == null) return;
        
        alvoSeta.post(() -> {
            if (alvoSeta.getWidth() <= 0) {
                alvoSeta.postDelayed(() -> posicionarSeta(alvoSeta), 50);
                return;
            }
            
            tutorialArrow.setVisibility(View.VISIBLE);
            
            int[] posAlvo = new int[2];
            int[] posOverlay = new int[2];
            alvoSeta.getLocationOnScreen(posAlvo);
            tutorialOverlay.getLocationOnScreen(posOverlay);
            
            float alvoCentroX = posAlvo[0] - posOverlay[0] + (alvoSeta.getWidth() / 2f);
            float alvoCentroY = posAlvo[1] - posOverlay[1] + (alvoSeta.getHeight() / 2f);
            
            float recuoX = 40f;
            float recuoY = 40f;
            
            // Se o alvo for gigante (tipo o card ou cadeado), aponta mais para a borda
            if (alvoSeta.getHeight() > 200) {
                recuoX = alvoSeta.getWidth() / 2.5f;
                recuoY = alvoSeta.getHeight() / 2.5f;
            }

            float setaX;
            if (alvoCentroX > tutorialOverlay.getWidth() / 2f) {
                tutorialArrow.setScaleX(1f); 
                setaX = alvoCentroX - tutorialArrow.getWidth() - recuoX; 
            } else {
                tutorialArrow.setScaleX(-1f); 
                setaX = alvoCentroX + recuoX; 
            }
            
            // Fixando o eixo Y para a seta não virar de ponta cabeça
            tutorialArrow.setScaleY(1f);
            float setaY = alvoCentroY + recuoY; 
            
            tutorialArrow.animate().x(setaX).y(setaY).alpha(1f).setDuration(400).start();
            
            if (arrowAnimator != null) arrowAnimator.cancel();
            arrowAnimator = ObjectAnimator.ofFloat(tutorialArrow, "y", setaY, setaY + 15f);
            arrowAnimator.setDuration(600).setRepeatMode(ValueAnimator.REVERSE);
            arrowAnimator.setRepeatCount(ValueAnimator.INFINITE);
            arrowAnimator.start();
            
            if (tutorialBox != null) {
                tutorialBox.post(() -> {
                    float caixaY = setaY + tutorialArrow.getHeight() + 20; 
                    
                    // 🔥 TRAVAS DE SEGURANÇA MÁXIMA: A caixa de texto nunca mais vai sumir da tela!
                    if (caixaY < 50) caixaY = 50;
                    if (caixaY + tutorialBox.getHeight() > tutorialOverlay.getHeight() - 50) {
                        caixaY = tutorialOverlay.getHeight() - tutorialBox.getHeight() - 50;
                    }
                    
                    tutorialBox.animate().y(caixaY).setDuration(400).start();
                });
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
                float startX = pos[0] - posOverlay[0] - 20;
                float startY = pos[1] - posOverlay[1] - 20;
                minX = Math.min(minX, startX); 
                minY = Math.min(minY, startY);
                maxX = Math.max(maxX, startX + alvo.getWidth() + 40); 
                maxY = Math.max(maxY, startY + alvo.getHeight() + 40);
            }
            
            highlightFrame.animate().x(minX).y(minY).setDuration(500).start();
            ViewGroup.LayoutParams p = highlightFrame.getLayoutParams();
            p.width = (int)(maxX - minX); 
            p.height = (int)(maxY - minY);
            highlightFrame.setLayoutParams(p);
            
            if (tutorialOverlay instanceof TutorialMaskView) {
                ((TutorialMaskView) tutorialOverlay).setTarget(minX, minY, p.width, p.height);
            }
            
            // Se a seta NÃO estiver aparecendo (como no Passo 2 do Mix), o 'focar' posiciona a caixa
            if (tutorialArrow == null || tutorialArrow.getVisibility() == View.GONE) {
                moverCaixaTexto(minY, p.height);
            }
        });
    }

    private void moverCaixaTexto(float alvoY, float alvoAltura) {
        if (tutorialBox == null) return;
        
        tutorialBox.post(() -> {
            float caixaY = (alvoY < tutorialOverlay.getHeight() / 2f) 
                    ? alvoY + alvoAltura + 50 
                    : alvoY - tutorialBox.getHeight() - 50;
            
            if (caixaY < 50) caixaY = 50;
            if (caixaY + tutorialBox.getHeight() > tutorialOverlay.getHeight() - 50) {
                caixaY = tutorialOverlay.getHeight() - tutorialBox.getHeight() - 50;
            }
            tutorialBox.animate().y(caixaY).setDuration(400).start();
        });
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

