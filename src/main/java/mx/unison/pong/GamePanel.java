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
        g2d.setColor(Color.WHITE); // O Color.RED para que resalte
        g2d.fillOval((int) b.getX(), (int) b.getY(), b.getDiameter(), b.getDiameter());

        // ladrillos
        for (Brick brick : model.getBricks()) {
            if (!brick.isDestroyed()) {
                g2d.setColor(brick.getColor());
                g2d.fillRect((int) brick.getShape().x, (int) brick.getShape().y, 
                             (int) brick.getShape().width, (int) brick.getShape().height);
            }
        }

        // puntaje
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Score: " + model.getScore(), 10, 20);

        // mensajes de estado
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        g2d.setColor(Color.YELLOW);
        String msg = "";
        
            if (model.getState() == GameState.READY) {
            msg = "Presiona flecha arriba para comenzar";
            g2d.setFont(new Font("Arial", Font.BOLD, 20)); // letra un poco más chica
        } else if (model.getState() == GameState.PAUSED) {
            msg = "PAUSADO";
        } else if (model.getState() == GameState.GAME_OVER) {
            msg = "GAME OVER - Presiona R para reiniciar";
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
        } else if (model.getState() == GameState.WIN) {
            msg = "¡GANASTE!";
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
        }

        if (!msg.isEmpty()) {
            // centrar el texto en pantalla
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g2d.drawString(msg, x, y);
        }
    }

    private void setupKeyBindings() {

        

        // asignar teclas
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        // flecha izquierda
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left");
        am.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (model.getState() == GameState.RUNNING) {
                    model.getPaddle().moveLeft();
                    model.getPaddle().keepInBounds(GameModel.WIDTH);
                }
            }
        });

        // flecha derecha
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


        // flecha Arriba (Disparar)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up");
        am.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("¡Flecha arriba presionada! Estado actual: " + model.getState());
                model.shootBall(); 
                System.out.println("Nuevo estado: " + model.getState());
            }
        });
    }
}