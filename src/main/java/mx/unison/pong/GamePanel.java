package mx.unison.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    private GameModel model;
    private MenuRenderer menuRenderer = new MenuRenderer();
    private int opcionMenu = 0;
    private boolean mostrandoInstrucciones = false;
    private boolean mostrandoNiveles = false;
    private boolean menuPausaAbierto = false;
    private int opcionPausa = 0;
    private JButton pauseButton;

    public GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(GameModel.WIDTH, GameModel.HEIGHT));
        setBackground(new Color(40, 40, 40)); 
        setLayout(null);

        pauseButton = new JButton("VOLVER");
        pauseButton.setBounds(GameModel.WIDTH - 100, 10, 90, 30);
        pauseButton.setFocusable(false);
        pauseButton.setBackground(new Color(60, 60, 60));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        pauseButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        pauseButton.setVisible(false); 
        
        pauseButton.addActionListener(e -> {
            if (model.getState() == GameState.READY) {
                // si aun no empieza, vuelve al menu
                model.resetGame();
                mostrandoInstrucciones = false;
                mostrandoNiveles = false;
                opcionMenu = 0;
                pauseButton.setVisible(false);
            } else if (model.getState() == GameState.RUNNING) {
                model.togglePause();
                menuPausaAbierto = true;
                opcionPausa = 0;
                pauseButton.setText("MENU"); 
            } else if (model.getState() == GameState.PAUSED) {
                model.togglePause();
                menuPausaAbierto = false;
                pauseButton.setText("PAUSA"); 
            }
        });
        add(pauseButton);

        JButton restartButton = new JButton("JUGAR DE NUEVO");
        restartButton.setBounds(GameModel.WIDTH / 2 - 100, GameModel.HEIGHT / 2 + 40, 200, 40);
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        restartButton.setBackground(new Color(60, 60, 60)); 
        restartButton.setForeground(Color.WHITE);
        restartButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        restartButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        restartButton.addActionListener(e -> {
            model.resetGame();
            restartButton.setVisible(false);
            mostrandoInstrucciones = false;
            mostrandoNiveles = false;
            menuPausaAbierto = false;
            opcionMenu = 0;
            pauseButton.setVisible(false);
        });
        add(restartButton);

        JButton nextLevelButton = new JButton("SIGUIENTE NIVEL");
        nextLevelButton.setBounds(GameModel.WIDTH / 2 - 100, GameModel.HEIGHT / 2 + 40, 200, 40);
        nextLevelButton.setFocusable(false);
        nextLevelButton.setVisible(false);
        nextLevelButton.setBackground(new Color(60, 60, 60)); 
        nextLevelButton.setForeground(Color.WHITE);
        nextLevelButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        nextLevelButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        nextLevelButton.addActionListener(e -> {
            model.siguienteNivel();
            nextLevelButton.setVisible(false);
            pauseButton.setVisible(true);
            pauseButton.setText("PAUSA");
        });
        add(nextLevelButton);
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                
                if (model.getState() == GameState.MENU) {
                    if (mostrandoInstrucciones) {
                        if (x >= getWidth() / 2 + 240 && x <= getWidth() / 2 + 280 && y >= getHeight() / 2 - 200 && y <= getHeight() / 2 - 160) {
                            mostrandoInstrucciones = false;
                        }
                    } else if (mostrandoNiveles) {
                        if (x >= getWidth() / 2 + 140 && x <= getWidth() / 2 + 180 && y >= getHeight() / 2 - 100 && y <= getHeight() / 2 - 60) {
                            mostrandoNiveles = false;
                        } else {
                            int boxX = (getWidth() - 400) / 2;
                            int boxY = (getHeight() - 250) / 2;
                            int nivelY = boxY + 100;
                            
                            if (y >= nivelY && y <= nivelY + 50) {
                                for (int i = 1; i <= 3; i++) {
                                    int nivelX = boxX + 50 + ((i - 1) * 100);
                                    if (x >= nivelX && x <= nivelX + 50) {
                                        model.iniciarNivel(i);
                                        mostrandoNiveles = false;
                                        pauseButton.setVisible(true);
                                        pauseButton.setText("PAUSA");
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        if (y >= 230 && y <= 270) {
                            model.iniciarJuego();
                            pauseButton.setVisible(true);
                            pauseButton.setText("VOLVER"); // al iniciar muestra volver
                        } else if (y >= 280 && y <= 320) {
                            mostrandoInstrucciones = true;
                        } else if (y >= 330 && y <= 370) {
                            mostrandoNiveles = true;
                        }
                    }
                } 
                else if (model.getState() == GameState.PAUSED && menuPausaAbierto) {
                    int boxX = (getWidth() - 300) / 2;
                    int boxY = (getHeight() - 250) / 2;
                    int startY = boxY + 70; 
                    
                    if (x >= boxX && x <= boxX + 300) {
                        if (y >= startY && y <= startY + 30) { 
                            model.togglePause();
                            menuPausaAbierto = false;
                            pauseButton.setText("PAUSA");
                        } else if (y >= startY + 40 && y <= startY + 70) { 
                            model.iniciarNivel(model.getNivel()); 
                            menuPausaAbierto = false;
                            pauseButton.setText("PAUSA");
                        } else if (y >= startY + 80 && y <= startY + 110) { 
                            model.resetGame();
                            menuPausaAbierto = false;
                            mostrandoInstrucciones = false;
                            mostrandoNiveles = false;
                            opcionMenu = 0;
                            pauseButton.setVisible(false);
                        }
                    }
                }
            }
        });
        
        setupKeyBindings();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (model.getState() == GameState.MENU || model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN) {
            pauseButton.setVisible(false);
        } else if (model.getState() == GameState.READY) {
            pauseButton.setVisible(true);
            pauseButton.setText("VOLVER");
        } else if (model.getState() == GameState.RUNNING || model.getState() == GameState.PAUSED || model.getState() == GameState.NIVEL_COMPLETADO) {
            pauseButton.setVisible(true);
        }

        if (model.getState() == GameState.MENU) {
            menuRenderer.dibujar(g2d, getWidth(), getHeight(), opcionMenu, mostrandoInstrucciones, mostrandoNiveles);
            return;
        }

        Paddle p = model.getPaddle();
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect((int) p.getX(), (int) p.getY(), (int) p.getWidth(), (int) p.getHeight());

        g2d.setColor(new Color(240, 240, 240));
        for (Ball b : model.getBalls()) {
            g2d.fillOval((int) b.getX(), (int) b.getY(), b.getDiameter(), b.getDiameter());
        }

        g2d.setColor(Color.RED);
        for (Laser l : model.getLasers()) {
            g2d.fillRect((int) l.getX(), (int) l.getY(), l.getAncho(), l.getAlto());
        }

        for (Brick brick : model.getBricks()) {
            brick.dibujar(g2d);
        }

        for (PowerUp powerUp : model.getPowerUps()) {
            powerUp.dibujar(g2d);
        }

        g2d.setColor(new Color(240, 240, 240));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("SCORE: " + model.getScore(), 10, 20);
        g2d.drawString("NIVEL: " + model.getNivel(), 180, 20);
        
        int vidaX = 20;
        int vidaY = 45;
        int diametro = 15;
        for (int i = 0; i < 3; i++) {
            g2d.setColor(new Color(240, 240, 240));
            if (i < model.getLives()) g2d.fillOval(vidaX, vidaY, diametro, diametro);
            else g2d.drawOval(vidaX, vidaY, diametro, diametro);
            vidaX += 25;
        }

        if (model.getState() == GameState.READY) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            String msg = "PRESIONA FLECHA ARRIBA";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
        } else if (model.getState() == GameState.PAUSED) {
            if (menuPausaAbierto) {
                dibujarMenuPausa(g2d);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 30));
                String msg = "PAUSADO";
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            }
        } else if (model.getState() == GameState.NIVEL_COMPLETADO) {
            int boxWidth = 450; int boxHeight = 200;
            int boxX = (getWidth() - boxWidth) / 2; int boxY = (getHeight() - boxHeight) / 2;

            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 30));
            String msg = "¡NIVEL " + model.getNivel() + " COMPLETADO!";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, boxX + (boxWidth - fm.stringWidth(msg)) / 2, boxY + 80);
            
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            String msg2 = "SIGUIENTE: NIVEL " + (model.getNivel() + 1);
            FontMetrics fm2 = g2d.getFontMetrics();
            g2d.drawString(msg2, boxX + (boxWidth - fm2.stringWidth(msg2)) / 2, boxY + 120);
        } else if (model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN) {
            int boxWidth = 450; int boxHeight = 250;
            int boxX = (getWidth() - boxWidth) / 2; int boxY = (getHeight() - boxHeight) / 2;

            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 35));
            String titulo = (model.getState() == GameState.WIN) ? "¡GANASTE!" : "FIN DEL JUEGO";
            FontMetrics fmTitulo = g2d.getFontMetrics();
            g2d.drawString(titulo, boxX + (boxWidth - fmTitulo.stringWidth(titulo)) / 2, boxY + 70);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
            String scoreMsg = "PUNTUACION: " + model.getScore();
            FontMetrics fmScore = g2d.getFontMetrics();
            g2d.drawString(scoreMsg, boxX + (boxWidth - fmScore.stringWidth(scoreMsg)) / 2, boxY + 120);

            String highScoreMsg = "RECORD: " + model.getHighScore();
            FontMetrics fmHighScore = g2d.getFontMetrics();
            g2d.drawString(highScoreMsg, boxX + (boxWidth - fmHighScore.stringWidth(highScoreMsg)) / 2, boxY + 150);
        }

        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                String text = ((JButton) c).getText();
                if (text.equals("JUGAR DE NUEVO")) {
                    c.setVisible(model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN);
                } else if (text.equals("SIGUIENTE NIVEL")) {
                    c.setVisible(model.getState() == GameState.NIVEL_COMPLETADO);
                }
            }
        }
    }

    private void dibujarMenuPausa(Graphics2D g2d) {
        int boxWidth = 300; int boxHeight = 250;
        int boxX = (getWidth() - boxWidth) / 2; int boxY = (getHeight() - boxHeight) / 2;

        g2d.setColor(new Color(20, 20, 20));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
        String titulo = "PAUSA";
        FontMetrics fmTitulo = g2d.getFontMetrics();
        g2d.drawString(titulo, boxX + (boxWidth - fmTitulo.stringWidth(titulo)) / 2, boxY + 40);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        String[] opciones = {"REANUDAR", "REINICIAR NIVEL", "VOLVER AL MENU"};
        int y = boxY + 100;
        for (int i = 0; i < opciones.length; i++) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = boxX + (boxWidth - fm.stringWidth(opciones[i])) / 2;
            
            if (i == opcionPausa) {
                g2d.setColor(new Color(180, 180, 180));
                g2d.drawString(opciones[i], x, y);
                g2d.setColor(Color.WHITE);
            } else {
                g2d.drawString(opciones[i], x, y);
            }
            y += 40;
        }
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "upAction");
        am.put("upAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.MENU && !mostrandoInstrucciones && !mostrandoNiveles) {
                    opcionMenu--;
                    if (opcionMenu < 0) opcionMenu = 2;
                } else if (model.getState() == GameState.PAUSED && menuPausaAbierto) {
                    opcionPausa--;
                    if (opcionPausa < 0) opcionPausa = 2;
                } else if (model.getState() == GameState.READY) {
                    model.shootBall();
                    pauseButton.setText("PAUSA");
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "downAction");
        am.put("downAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.MENU && !mostrandoInstrucciones && !mostrandoNiveles) {
                    opcionMenu++;
                    if (opcionMenu > 2) opcionMenu = 0;
                } else if (model.getState() == GameState.PAUSED && menuPausaAbierto) {
                    opcionPausa++;
                    if (opcionPausa > 2) opcionPausa = 0;
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enterAction");
        am.put("enterAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.MENU) {
                    if (mostrandoInstrucciones || mostrandoNiveles) {
                        mostrandoInstrucciones = false;
                        mostrandoNiveles = false;
                    } else {
                        if (opcionMenu == 0) {
                            model.iniciarJuego();
                            pauseButton.setVisible(true);
                            pauseButton.setText("VOLVER");
                        }
                        else if (opcionMenu == 1) mostrandoInstrucciones = true;
                        else if (opcionMenu == 2) mostrandoNiveles = true;
                    }
                } else if (model.getState() == GameState.PAUSED && menuPausaAbierto) {
                    if (opcionPausa == 0) {
                        model.togglePause();
                        menuPausaAbierto = false;
                        pauseButton.setText("PAUSA");
                    } else if (opcionPausa == 1) {
                        model.iniciarNivel(model.getNivel());
                        menuPausaAbierto = false;
                        pauseButton.setText("PAUSA");
                    } else if (opcionPausa == 2) {
                        model.resetGame();
                        menuPausaAbierto = false;
                        pauseButton.setVisible(false);
                    }
                }
            }
        });



        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "leftPressed");
        am.put("leftPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.getPaddle().setMovingLeft(true); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "leftReleased");
        am.put("leftReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.getPaddle().setMovingLeft(false); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "rightPressed");
        am.put("rightPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.getPaddle().setMovingRight(true); }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rightReleased");
        am.put("rightReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { model.getPaddle().setMovingRight(false); }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.RUNNING) {
                    model.togglePause();
                    menuPausaAbierto = true;
                    opcionPausa = 0;
                    pauseButton.setText("MENU");
                } else if (model.getState() == GameState.PAUSED) {
                    model.togglePause();
                    menuPausaAbierto = false;
                    pauseButton.setText("PAUSA");
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false), "teclaR");
        am.put("teclaR", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.PAUSED) {
                    // si esta pausado, la R reanuda el juego
                    model.togglePause();
                    menuPausaAbierto = false;
                    pauseButton.setText("PAUSA");
                } else if (model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN) {
                    // si perdio o gano, la R reinicia
                    model.resetGame();
                    mostrandoInstrucciones = false;
                    mostrandoNiveles = false;
                    menuPausaAbierto = false;
                    opcionMenu = 0;
                    pauseButton.setVisible(false);
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0, false), "menuKey");
        am.put("menuKey", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.RUNNING || model.getState() == GameState.PAUSED) {
                    model.resetGame();
                    mostrandoInstrucciones = false;
                    mostrandoNiveles = false;
                    menuPausaAbierto = false;
                    opcionMenu = 0;
                    pauseButton.setVisible(false);
                }
            }
        });
    }
}