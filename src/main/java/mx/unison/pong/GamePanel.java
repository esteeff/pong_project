package mx.unison.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    private GameModel model;

    public GamePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(GameModel.WIDTH, GameModel.HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null); // Para posicionar el botón manualmente

        // Crear botón de Pausa
        JButton pauseButton = new JButton("Pausa");
        pauseButton.setBounds(GameModel.WIDTH - 90, 10, 80, 30); 
        pauseButton.setFocusable(false); 
        pauseButton.addActionListener(e -> {
            model.togglePause();
            if (model.getState() == GameState.PAUSED) {
                pauseButton.setText("Despausa");
            } else {
                pauseButton.setText("Pausa");
            }
        });
        add(pauseButton);
        
        setupKeyBindings();
    }

      @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // raqueta
        Paddle p = model.getPaddle();
        g2d.setColor(Color.WHITE);
        g2d.fillRect((int) p.getX(), (int) p.getY(), (int) p.getWidth(), (int) p.getHeight());

        // pelota
        Ball b = model.getBall();
        g2d.setColor(Color.WHITE); 
        g2d.fillOval((int) b.getX(), (int) b.getY(), b.getDiameter(), b.getDiameter());

        // ladrillos
        for (Brick brick : model.getBricks()) {
            if (!brick.isDestroyed()) {
                g2d.setColor(brick.getColor());
                g2d.fillRect((int) brick.getShape().x, (int) brick.getShape().y, 
                             (int) brick.getShape().width, (int) brick.getShape().height);
            }
        }

        
        // dibujar puntaje
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Score: " + model.getScore(), 10, 20);
        
        // NUEVO: Dibujar Vidas como círculos (estilo arcade)
        int vidaX = 20; // Posición inicial X de los círculos
        int vidaY = 45; // Posición inicial Y
        int diametro = 15; // Tamaño de los círculos

        for (int i = 0; i < 3; i++) {
            g2d.setColor(Color.WHITE); // Color de los círculos
            
            if (i < model.getLives()) {
                // Si es una vida que aún tienes, se dibuja rellena
                g2d.fillOval(vidaX, vidaY, diametro, diametro);
            } else {
                // Si es una vida perdida, se dibuja solo el contorno
                g2d.drawOval(vidaX, vidaY, diametro, diametro);
            }
            
            vidaX += 25; // Separación entre círculos
        }

        
        // mensajes de estado
        g2d.setColor(Color.YELLOW);
        String msg = "";
        
        if (model.getState() == GameState.READY) {
            msg = "Presiona Flecha Arriba para comenzar";
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
        } else if (model.getState() == GameState.PAUSED) {
            msg = "PAUSADO";
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
        } else if (model.getState() == GameState.GAME_OVER) {
            msg = "GAME OVER - Presiona R para reiniciar";
            g2d.setFont(new Font("Arial", Font.BOLD, 25));
        } else if (model.getState() == GameState.WIN) {
            msg = "¡GANASTE! - Presiona R para reiniciar";
            g2d.setFont(new Font("Arial", Font.BOLD, 25));
        }

        if (!msg.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
        }
    }

       private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // --- FLECHA IZQUIERDA ---
        // Presionar
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "leftPressed");
        am.put("leftPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.getPaddle().setMovingLeft(true);
            }
        });
        // Soltar
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "leftReleased");
        am.put("leftReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.getPaddle().setMovingLeft(false);
            }
        });

        // --- FLECHA DERECHA ---
        // Presionar
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "rightPressed");
        am.put("rightPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.getPaddle().setMovingRight(true);
            }
        });
        // Soltar
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rightReleased");
        am.put("rightReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.getPaddle().setMovingRight(false);
            }
        });

        // --- FLECHA ARRIBA (Disparar) ---
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up");
        am.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.shootBall();
            }
        });

        // --- ESPACIO (Pausa) ---
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.togglePause();
            }
        });

        // --- R (Reiniciar) ---
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false), "restart");
        am.put("restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.GAME_OVER || model.getState() == GameState.WIN) {
                    model.resetGame();
                }
            }
        });
    }
}