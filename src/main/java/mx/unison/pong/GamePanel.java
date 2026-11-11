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
        
        // Configurar los controles (Key Bindings)
        setupKeyBindings();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Dibujar Raqueta
        Paddle p = model.getPaddle();
        g2d.setColor(Color.WHITE);
        g2d.fillRect((int) p.getX(), (int) p.getY(), (int) p.getWidth(), (int) p.getHeight());

        // 2. Dibujar Pelota
        Ball b = model.getBall();
        g2d.setColor(Color.WHITE); // O Color.RED para que resalte
        g2d.fillOval((int) b.getX(), (int) b.getY(), b.getDiameter(), b.getDiameter());

        // 3. Dibujar Ladrillos
        for (Brick brick : model.getBricks()) {
            if (!brick.isDestroyed()) {
                g2d.setColor(brick.getColor());
                g2d.fillRect((int) brick.getShape().x, (int) brick.getShape().y, 
                             (int) brick.getShape().width, (int) brick.getShape().height);
            }
        }

        // 4. Dibujar Puntaje
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Score: " + model.getScore(), 10, 20);

        // 5. Dibujar Mensajes de Estado
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.YELLOW);
        String msg = "";
        
        if (model.getState() == GameState.PAUSED) {
            msg = "PAUSADO (Presiona Espacio)";
        } else if (model.getState() == GameState.GAME_OVER) {
            msg = "GAME OVER";
        } else if (model.getState() == GameState.WIN) {
            msg = "¡GANASTE!";
        }

        if (!msg.isEmpty()) {
            // Centrar el texto en pantalla
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
        }
    }

    private void setupKeyBindings() {
        // Asignar teclas usando InputMap y ActionMap
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // Flecha Izquierda
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left");
        am.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.RUNNING) {
                    model.getPaddle().moveLeft();
                    model.getPaddle().keepInBounds(GameModel.WIDTH);
                }
            }
        });

        // Flecha Derecha
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right");
        am.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.RUNNING) {
                    model.getPaddle().moveRight();
                    model.getPaddle().keepInBounds(GameModel.WIDTH);
                }
            }
        });

        // Espacio (Pausa / Reanudar)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "pause");
        am.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                model.togglePause();
            }
        });
        
        // Tecla R (Reiniciar si perdió)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false), "restart");
        am.put("restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.GAME_OVER) {
                    model.resetGame();
                }
            }
        });
    }
}